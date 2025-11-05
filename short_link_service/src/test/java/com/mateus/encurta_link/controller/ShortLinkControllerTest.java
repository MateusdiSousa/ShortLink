package com.mateus.encurta_link.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.exceptions.ShortLinkConflictException;
import com.mateus.encurta_link.exceptions.ShortLinkNotFoundException;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.ShortLink;
import com.mateus.encurta_link.service.JwtService;
import com.mateus.encurta_link.service.ShortLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

class ShortLinkControllerUnitTest {

    @Mock
    private ShortLinkService encurtadorService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ShortLinkController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRedirecionarLink_Success() throws ShortLinkNotFoundException {
        // Arrange
        String codigo = "abc123";
        String linkOriginal = "https://original.com";
        when(encurtadorService.GetLink(codigo)).thenReturn(linkOriginal);

        // Act
        RedirectView result = controller.redirecionarLink(codigo);

        // Assert
        assertEquals(linkOriginal, result.getUrl());
    }

    @SuppressWarnings("null")
    @Test
    void testCriarShortLink_Success() throws ShortLinkConflictException, UserNotFoundException {
        // Arrange
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("https://original.com", null);
        String token = "valid.token.here";
        String email = "user@test.com";
        ShortLink shortLink = new ShortLink();
        shortLink.setShortLink("xyz789");

        when(jwtService.extractEmail("valid.token.here")).thenReturn(email);
        when(encurtadorService.AddLink(dto, email)).thenReturn(shortLink);

        // Act
        ResponseEntity<String> response = controller.criarShortLink(dto, "bearer " + token);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("xyz789"));

    }
}