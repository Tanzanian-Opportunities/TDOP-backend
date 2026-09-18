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
import tdop.entity.User;
import tdop.entity.enums.UserRole;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.email.EmailService;
import tdop.repository.UserRepository;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    private final java.util.Map<String, String> passwordResetTokens = new ConcurrentHashMap<>();

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid credentials");
        }
        User user = userRepository.findByEmail(request.getEmail())
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

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(),
                "http://localhost:3000/verify?token=" + UUID.randomUUID());
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
            auditLogService.logAction("FORGOT_PASSWORD", "User", user.getId(), user.getId());
            log.info("Password reset token for {}: {}", email, resetToken);
        });
    }

    public void resetPassword(String token, String newPassword) {
        String email = passwordResetTokens.remove(token);
        if (email == null) {
            throw new BadRequestException("Invalid or expired reset token");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        auditLogService.logAction("RESET_PASSWORD", "User", user.getId(), user.getId());
    }
}
