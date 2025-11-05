package com.mateus.encurta_link.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import com.mateus.encurta_link.model.User;

import com.mateus.encurta_link.dto.Auth.AuthenticationResponse;
import com.mateus.encurta_link.dto.Auth.UserRegisterRequest;
import com.mateus.encurta_link.exceptions.InvalidCredentialsException;
import com.mateus.encurta_link.exceptions.UserAlreadyExistException;
import com.mateus.encurta_link.service.AuthenticationService;

public class AuthenticationControllerTest {
    private final String email = "user@gmail.com";

    private final String token = "Bearer 1234";

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSign_sucess() throws UserAlreadyExistException {
        UserRegisterRequest dto = new UserRegisterRequest(email, "12345678");
        AuthenticationResponse response = new AuthenticationResponse(token);

        when(authenticationService.Register(dto)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = this.authenticationController.registerUser(dto);

        assertEquals(response, result.getBody());
    }

    @Test
    public void testSign_userAlreadyExists() throws UserAlreadyExistException {
        UserRegisterRequest dto = new UserRegisterRequest(email, "12345678");

        when(authenticationService.Register(dto)).thenThrow(new UserAlreadyExistException());

        assertThrows(UserAlreadyExistException.class, () -> authenticationService.Register(dto));
    }

    @Test
    public void testLogin_sucess() throws InvalidCredentialsException {
        User user = new User();
        user.setEmail(email);
        user.setPassword("12345678");
        AuthenticationResponse response = new AuthenticationResponse(token);

        when(authenticationService.Authenticate(user)).thenReturn(response);

        ResponseEntity<AuthenticationResponse> result = this.authenticationController.login(user);

        assertEquals(response, result.getBody());
    }

    @Test
    public void testLogin_InvalidCredentials() throws InvalidCredentialsException {
        User user = new User();
        user.setEmail(email);
        user.setPassword("12345678");

        when(authenticationService.Authenticate(user)).thenThrow(InvalidCredentialsException.class);

        assertThrows(InvalidCredentialsException.class, () -> authenticationController.login(user));
    }
}
