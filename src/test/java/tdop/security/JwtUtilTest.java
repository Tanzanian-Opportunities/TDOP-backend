package tdop.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import tdop.config.JwtUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private String secretKey;
    private Key signingKey;
    private String validToken;
    private String refreshToken;
    private String username;
    private String role;

    @BeforeEach
    void setUp() {
        secretKey = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        username = "test@example.com";
        role = "SEEKER";

        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800000L);

        validToken = Jwts.builder()
            .setClaims(new HashMap<>())
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(signingKey)
            .compact();

        refreshToken = Jwts.builder()
            .setClaims(new HashMap<>())
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 604800000))
            .signWith(signingKey)
            .compact();
    }

    @AfterEach
    void tearDown() {
        jwtUtil = null;
        validToken = null;
        refreshToken = null;
        secretKey = null;
        signingKey = null;
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(username, role);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testExtractUsername() {
        String extracted = jwtUtil.extractUsername(validToken);

        assertEquals(username, extracted);
    }

    @Test
    void testExtractRole() {
        String extractedRole = jwtUtil.extractRole(validToken);

        assertEquals(role, extractedRole);
    }

    @Test
    void testExtractExpiration() {
        Date expiration = jwtUtil.extractExpiration(validToken);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateTokenValid() {
        org.springframework.security.core.userdetails.UserDetails userDetails =
            new org.springframework.security.core.userdetails.User(username, "password", java.util.Collections.emptyList());

        assertTrue(jwtUtil.validateToken(validToken, userDetails));
    }

    @Test
    void testValidateTokenInvalid() {
        String wrongUserToken = Jwts.builder()
            .setClaims(new HashMap<>())
            .setSubject("other@example.com")
            .claim("role", role)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(signingKey)
            .compact();

        org.springframework.security.core.userdetails.UserDetails userDetails =
            new org.springframework.security.core.userdetails.User(username, "password", java.util.Collections.emptyList());

        assertFalse(jwtUtil.validateToken(wrongUserToken, userDetails));
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtUtil.generateRefreshToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testTokenExpired() {
        String expiredToken = Jwts.builder()
            .setClaims(new HashMap<>())
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis() - 172800000))
            .setExpiration(new Date(System.currentTimeMillis() - 86400000))
            .signWith(signingKey)
            .compact();

        assertTrue(jwtUtil.extractExpiration(expiredToken).before(new Date()));
        assertFalse(jwtUtil.validateToken(expiredToken,
            new org.springframework.security.core.userdetails.User(username, "password", java.util.Collections.emptyList())));
    }

    @Test
    void testExtractAllClaims() {
        Claims claims = jwtUtil.extractAllClaims(validToken);

        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(role, claims.get("role"));
    }

    @Test
    void testExtractClaimCustom() {
        Function<Claims, String> extractor = Claims::getSubject;
        String result = jwtUtil.extractClaim(validToken, extractor);

        assertEquals(username, result);
    }
}
