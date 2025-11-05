package com.mateus.encurta_link.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoResponse;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.service.JwtService;
import com.mateus.encurta_link.service.UserService;

public class UserControllerTest {
    private final String validToken = "abcde";
    private final String bearerToken = "Bearer " + validToken;
    private final String email = "user@gmail.com";

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @BeforeEach

    private void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserLinks_sucess() throws UserNotFoundException {
        ShortLinkDtoResponse link1 = new ShortLinkDtoResponse("1", "https://original.com.br", "link1", email);
        ShortLinkDtoResponse link2 = new ShortLinkDtoResponse("2", "https://original.com.br", "link2", email);

        List<ShortLinkDtoResponse> links = List.of(link1, link2);

        when(this.jwtService.extractEmail(validToken)).thenReturn(email);
        when(this.userService.getUserLinks(email)).thenReturn(links);

        ResponseEntity<List<ShortLinkDtoResponse>> response = userController.getUserLinks(bearerToken);

        assertEquals(links, response.getBody());
        verify(jwtService).extractEmail(validToken);
        verify(userService).getUserLinks(email);
    }

    @Test
    public void testGetUserLinks_userNotFound() throws UserNotFoundException {
        when(this.jwtService.extractEmail(validToken)).thenReturn(email);
        when(this.userService.getUserLinks(email)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userController.getUserLinks(bearerToken));
    
        verify(jwtService).extractEmail(validToken);
        verify(userService).getUserLinks(email);
    }

}
