package tdop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tdop.config.JwtUtil;
import tdop.dto.request.AuthRequest;
import tdop.dto.request.LoginRequest;
import tdop.dto.request.RegisterRequest;
import tdop.dto.response.AuthResponse;
import tdop.entity.User;
import tdop.entity.enums.UserRole;
import tdop.exception.BadRequestException;
import tdop.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid credentials");
        }
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
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
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder().token(token).email(user.getEmail())
            .fullName(user.getFullName()).role(user.getRole().name()).build();
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
}
