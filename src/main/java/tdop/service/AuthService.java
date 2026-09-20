package tdop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tdop.audit.AuditLogService;
import tdop.config.JwtUtil;
import tdop.dto.request.LoginRequest;
import tdop.dto.request.RegisterRequest;
import tdop.dto.response.AuthResponse;
import tdop.dto.response.UserResponse;
import tdop.entity.EmailVerificationToken;
import tdop.entity.User;
import tdop.entity.enums.UserRole;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.email.EmailService;
import tdop.repository.EmailVerificationTokenRepository;
import tdop.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    private final java.util.Map<String, String> passwordResetTokens = new ConcurrentHashMap<>();
    private final java.util.Map<String, LocalDateTime> passwordResetExpiry = new ConcurrentHashMap<>();
    private final java.util.Map<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private final java.util.Map<String, LocalDateTime> accountLockouts = new ConcurrentHashMap<>();

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;
    private static final int PASSWORD_RESET_EXPIRY_MINUTES = 30;
    private static final int EMAIL_VERIFICATION_EXPIRY_MINUTES = 60;

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();
        checkAccountLockout(email);
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (BadCredentialsException e) {
            recordFailedLogin(email);
            throw new BadRequestException("Invalid credentials");
        }
        clearLoginAttempts(email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));
        if (!user.isEnabled()) {
            throw new BadRequestException("Account is disabled");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        auditLogService.logAction("LOGIN", "User", user.getId(), user.getId());
        return AuthResponse.builder().token(token).refreshToken(refreshToken)
            .email(user.getEmail()).fullName(user.getFullName())
            .role(user.getRole().name()).build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
        User user = User.builder().email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName()).phone(request.getPhone())
            .role(role).enabled(true).verified(false).build();
        userRepository.save(user);
        auditLogService.logAction("REGISTER", "User", user.getId(), user.getId());

        String verificationToken = UUID.randomUUID().toString();
        EmailVerificationToken evToken = EmailVerificationToken.builder()
            .token(verificationToken)
            .email(user.getEmail())
            .expiresAt(LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_EXPIRY_MINUTES))
            .used(false)
            .build();
        emailVerificationTokenRepository.save(evToken);
        try {
            String verifyUrl = System.getenv("FRONTEND_URL") + "/verify?token=" + verificationToken;
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), verifyUrl);
        } catch (Exception e) {
            log.warn("Could not send verification email to {}: {}", user.getEmail(), e.getMessage());
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return AuthResponse.builder().token(token).refreshToken(refreshToken)
            .email(user.getEmail()).fullName(user.getFullName())
            .role(user.getRole().name()).build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder().token(token).refreshToken(refreshToken)
            .email(user.getEmail()).fullName(user.getFullName())
            .role(user.getRole().name()).build();
    }

    public void logout(String token) {
        revokedTokens.add(token);
        auditLogService.logAction("LOGOUT", "User", null, null);
        log.info("Token revoked for logout");
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .role(user.getRole().name())
            .enabled(user.isEnabled())
            .verified(user.isVerified())
            .build();
    }

    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            passwordResetTokens.put(resetToken, email);
            passwordResetExpiry.put(resetToken, LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_MINUTES));
            auditLogService.logAction("FORGOT_PASSWORD", "User", user.getId(), user.getId());
            log.info("Password reset token generated for {}", email);
        });
    }

    public void resetPassword(String token, String newPassword) {
        LocalDateTime expiry = passwordResetExpiry.get(token);
        if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
            passwordResetTokens.remove(token);
            passwordResetExpiry.remove(token);
            throw new BadRequestException("Invalid or expired reset token");
        }
        String email = passwordResetTokens.remove(token);
        passwordResetExpiry.remove(token);
        if (email == null) {
            throw new BadRequestException("Invalid or expired reset token");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        auditLogService.logAction("RESET_PASSWORD", "User", user.getId(), user.getId());
    }

    public boolean verifyEmail(String token) {
        EmailVerificationToken evToken = emailVerificationTokenRepository.findByToken(token)
            .orElse(null);
        if (evToken == null || evToken.isExpired() || evToken.isUsed()) {
            return false;
        }
        evToken.setUsed(true);
        emailVerificationTokenRepository.save(evToken);
        userRepository.findByEmail(evToken.getEmail()).ifPresent(user -> {
            user.setVerified(true);
            userRepository.save(user);
        });
        return true;
    }

    private void checkAccountLockout(String email) {
        LocalDateTime lockout = accountLockouts.get(email);
        if (lockout != null && LocalDateTime.now().isBefore(lockout)) {
            throw new BadRequestException("Account is locked due to too many failed attempts. Try again later.");
        }
        if (lockout != null && LocalDateTime.now().isAfter(lockout)) {
            accountLockouts.remove(email);
            loginAttempts.remove(email);
        }
    }

    private void recordFailedLogin(String email) {
        AtomicInteger attempts = loginAttempts.computeIfAbsent(email, k -> new AtomicInteger(0));
        int count = attempts.incrementAndGet();
        if (count >= MAX_LOGIN_ATTEMPTS) {
            accountLockouts.put(email, LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            log.warn("Account locked for {} after {} failed attempts", email, count);
        }
    }

    private void clearLoginAttempts(String email) {
        loginAttempts.remove(email);
        accountLockouts.remove(email);
    }
}
