package tdop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tdop.dto.request.LoginRequest;
import tdop.dto.request.RegisterRequest;
import tdop.dto.request.AuthRequest;
import tdop.dto.response.AuthResponse;
import tdop.service.AuthService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
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

        authResponse = AuthResponse.builder()
            .token("eyJhbGciOiJIUzI1NiJ9.test.token")
            .refreshToken("refresh.token.here")
            .email("test@example.com")
            .fullName("Test User")
            .role("SEEKER")
            .build();
    }

    @AfterEach
    void tearDown() {
        loginRequest = null;
        registerRequest = null;
        authRequest = null;
        authResponse = null;
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9.test.token"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.role").value("SEEKER"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.email").value("new@example.com"))
            .andExpect(jsonPath("$.fullName").value("New User"));
    }

    @Test
    void testRefreshTokenSuccess() throws Exception {
        when(authService.refreshToken("refresh@example.com")).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.refreshToken").value("refresh.token.here"))
            .andExpect(jsonPath("$.email").value("refresh@example.com"));
    }

    @Test
    void testLogoutSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(content().string("Logged out"));
    }

    @Test
    void testGetProfileSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/auth/profile"))
            .andExpect(status().isOk())
            .andExpect(content().string("Profile"));
    }
}
