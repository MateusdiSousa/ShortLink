package com.mateus.encurta_link.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mateus.encurta_link.dto.Auth.AuthenticationResponse;
import com.mateus.encurta_link.dto.Auth.UserRegisterRequest;
import com.mateus.encurta_link.dto.User.UserRole;
import com.mateus.encurta_link.exceptions.InvalidCredentialsException;
import com.mateus.encurta_link.exceptions.UserAlreadyExistException;
import com.mateus.encurta_link.model.User;
import com.mateus.encurta_link.repository.UserRepository;

public class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String TEST_EMAIL = "user@test.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String JWT_TOKEN = "generated.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Testes para Register() ---
    @Test
    void testRegister_NewUser_ReturnsToken() throws UserAlreadyExistException {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest(TEST_EMAIL, TEST_PASSWORD);
        User savedUser = new User();
        savedUser.setEmail(TEST_EMAIL);
        savedUser.setPassword(ENCODED_PASSWORD);
        savedUser.setRole(UserRole.USER);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn(JWT_TOKEN);

        // Act
        AuthenticationResponse response = authenticationService.Register(request);

        // Assert
        assertNotNull(response);
        assertEquals(JWT_TOKEN, response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_ExistingUser_ThrowsException() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistException.class, () -> {
            authenticationService.Register(request);
        });
    }

    // --- Testes para Authenticate() ---
    @Test
    void testAuthenticate_ValidCredentials_ReturnsToken() throws InvalidCredentialsException {
        // Arrange
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(ENCODED_PASSWORD);

        // Mock successful authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(TEST_EMAIL, ENCODED_PASSWORD));

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(JWT_TOKEN);

        // Act
        AuthenticationResponse response = authenticationService.Authenticate(user);

        // Assert
        assertNotNull(response);
        assertEquals(JWT_TOKEN, response.getToken());
    }

    @Test
    void testAuthenticate_InvalidCredentials_ThrowsException() {
        // Arrange
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.Authenticate(user);
        });
    }

    @Test
    void testAuthenticate_UserNotFound_ThrowsException() {
        // Arrange
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.Authenticate(user);
        });
    }
}
