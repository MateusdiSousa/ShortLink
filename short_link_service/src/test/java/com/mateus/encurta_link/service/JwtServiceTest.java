package com.mateus.encurta_link.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import com.mateus.encurta_link.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@ActiveProfiles("test")
public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private static final String TEST_EMAIL = "user@test.com";
    private static final String SECRET = "504bfb885f330ecf2d135888424fd06e0e2c046135fd435afd59358ac3768c94";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtService.setSecretKey(SECRET);
    }

    // --- Test: generateToken ---
    @Test
    @DisplayName("Should generate a token using User information")
    void testGenerateToken() {
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);

        String result = jwtService.generateToken(mockUser);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // --- Test: extractEmail ---
    @Test
    @DisplayName("Should extract email from a valid email")
    void testExtractEmail_FromValidEmail_ReturnEmail() {
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);

        String token = jwtService.generateToken(mockUser);

        String result = jwtService.extractEmail(token);

        assertEquals(result, TEST_EMAIL);
    }

    // --- Test: isValid ---
    @Test
    @DisplayName("Should validate a valid token")
    void testIsValid_FromValidToken() {
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);

        String token = jwtService.generateToken(mockUser);
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);

        boolean result = jwtService.isValid(token, userDetails);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate a invalid token")
    void testIsValid_FromInvalidToken() {
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        String token = generateMockInvalidToken(mockUser);
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);

        boolean result = jwtService.isValid(token, userDetails);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate a token with a user does not match")
    void testIsValid_FromValidToken_UserDoesNotMatch() {
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        String token = generateMockInvalidToken(mockUser);
        when(userDetails.getUsername()).thenReturn("another@gmail.com");

        boolean result = jwtService.isValid(token, userDetails);
        assertFalse(result);
    }

    // --- Test: isTokenExpired --- 
    @Test
    @DisplayName("Should return false for unexpired token")
    void testIsTokenExpired_FromUnexperidToken(){
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        String token = jwtService.generateToken(mockUser);

        boolean result = jwtService.isTokenExpired(token);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for expired token")
    void testIsTokenExpired_FromExpiredToken(){
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        String token = generateMockInvalidToken(mockUser);

        boolean result = jwtService.isTokenExpired(token);

        assertTrue(result);
    }


    private String generateMockInvalidToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(getMockSigningKey())
                .compact();
    }

    private SecretKey getMockSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
