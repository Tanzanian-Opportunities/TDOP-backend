package tdop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tdop.config.JwtUtil;
import tdop.dto.request.LoginRequest;
import tdop.dto.request.RegisterRequest;
import tdop.dto.request.AuthRequest;
import tdop.dto.response.AuthResponse;
import tdop.entity.User;
import tdop.entity.enums.UserRole;
import tdop.exception.BadRequestException;
import tdop.repository.UserRepository;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
        registerRequest.setRole("SEEKER");

        authRequest = new AuthRequest();
        authRequest.setEmail("refresh@example.com");

        user = User.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .fullName("Test User")
            .role(UserRole.SEEKER)
            .enabled(true)
            .verified(false)
            .build();

        authResponse = AuthResponse.builder()
            .token("jwt.token.here")
            .refreshToken("refresh.token.here")
            .email("test@example.com")
            .fullName("Test User")
            .role("SEEKER")
            .build();

        when(jwtUtil.generateToken(any(String.class), any(String.class))).thenReturn("jwt.token.here");
        when(jwtUtil.generateRefreshToken(any(String.class))).thenReturn("refresh.token.here");
    }

    @AfterEach
    void tearDown() {
        loginRequest = null;
        registerRequest = null;
        authRequest = null;
        user = null;
        authResponse = null;
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authResponse.getToken()).thenReturn("jwt.token.here");

        AuthResponse result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("SEEKER", result.getRole());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginInvalidCredentials() {
        doThrow(new BadRequestException("Invalid credentials"))
            .when(authenticationManager).authenticate(any());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.login(loginRequest);
        });
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("SEEKER", result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.register(registerRequest);
        });
        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void testRefreshTokenSuccess() {
        when(jwtUtil.extractUsername("refresh.token.here")).thenReturn("refresh@example.com");
        when(userRepository.findByEmail("refresh@example.com")).thenReturn(Optional.of(user));

        AuthResponse result = authService.refreshToken("refresh.token.here");

        assertNotNull(result);
        assertEquals("refresh@example.com", result.getEmail());
        verify(jwtUtil).extractUsername("refresh.token.here");
    }

    @Test
    void testRefreshTokenUserNotFound() {
        when(jwtUtil.extractUsername("invalid.token")).thenReturn("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.refreshToken("invalid.token");
        });
        assertEquals("User not found", exception.getMessage());
    }
}
