package com.mateus.encurta_link.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoResponse;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.ShortLink;
import com.mateus.encurta_link.model.User;
import com.mateus.encurta_link.repository.UserRepository;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static final String TEST_EMAIL = "user@test.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@test.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Testes para loadUserByUsername() ---
    @Test
    void testLoadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails result = userService.loadUserByUsername(TEST_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getUsername());
    }

    @Test
    void testLoadUserByUsername_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(NON_EXISTENT_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(NON_EXISTENT_EMAIL);
        });
    }

    // --- Testes para getUserLinks() ---
    @Test
    void testGetUserLinks_WhenUserExists_ReturnsLinks() throws UserNotFoundException {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);

        ShortLink link1 = new ShortLink();
        link1.setShortLink("abcd");
        link1.setOriginalLink("https://original1.com");
        link1.setUser(mockUser);
        link1.setId("1");

        ShortLink link2 = new ShortLink();
        link2.setShortLink("efghi");
        link2.setOriginalLink("https://original2.com");
        link2.setUser(mockUser);
        link2.setId("2");

        mockUser.setUserLinks(Set.of(link1, link2));

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        // Act
        List<ShortLinkDtoResponse> result = userService.getUserLinks(TEST_EMAIL);

        // Assert
        assertEquals(2, result.size());
        assertEquals("abcd", result.get(0).shortLink());
        assertEquals("https://original1.com", result.get(0).originalLink());
    }

    @Test
    void testGetUserLinks_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(NON_EXISTENT_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserLinks(NON_EXISTENT_EMAIL);
        });
    }

    @Test
    void testGetUserLinks_WhenUserHasNoLinks_ReturnsEmptyList() throws UserNotFoundException {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        mockUser.setUserLinks(Set.of()); // Sem links

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        // Act
        List<ShortLinkDtoResponse> result = userService.getUserLinks(TEST_EMAIL);

        // Assert
        assertTrue(result.isEmpty());
    }
}
