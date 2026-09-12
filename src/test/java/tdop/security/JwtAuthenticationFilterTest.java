package tdop.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import tdop.config.JwtUtil;
import tdop.config.JwtAuthenticationFilter;
import tdop.config.CustomUserDetailsService;
import java.io.IOException;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String validToken;
    private String username;

    @BeforeEach
    void setUp() {
        username = "test@example.com";
        validToken = "eyJhbGciOiJIUzI1NiJ9.test.token";
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternalWithValidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(any(String.class), any(UserDetails.class))).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetails.getUsername()).thenReturn(username);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(jwtUtil).extractUsername(validToken);
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithoutAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
        when(jwtUtil.extractUsername("invalid.token.here")).thenThrow(new RuntimeException("Invalid token"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithNonBearerToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic somecredentials");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWhenAlreadyAuthenticated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(
            new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList()));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWhenUsernameIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn(null);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
