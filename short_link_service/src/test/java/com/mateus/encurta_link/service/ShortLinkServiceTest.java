package com.mateus.encurta_link.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.exceptions.ShortLinkConflictException;
import com.mateus.encurta_link.exceptions.ShortLinkNotFoundException;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.ShortLink;
import com.mateus.encurta_link.model.User;
import com.mateus.encurta_link.repository.ShortLinkRepository;
import com.mateus.encurta_link.repository.UserRepository;

public class ShortLinkServiceTest {
    @Mock
    private ShortLinkRepository shortLinkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ShortLinkService shortLinkService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Test: GetLink

    @Test
    @DisplayName("Should return original link when short link exists")
    void testGetLink_WhenLinkExists_ReturnOriginalLink() {
        String code = "abcd";
        String originalLink = "https://original.com";

        ShortLink mockShortLink = new ShortLink();
        mockShortLink.setOriginalLink(originalLink);

        when(shortLinkRepository.findByShortLink(code)).thenReturn(Optional.of(mockShortLink));

        String result = shortLinkService.GetLink(code);

        assertEquals(originalLink, result);

    }

    @Test
    @DisplayName("Should not found a link when search for a shortlink that not exist")
    void testGetLink_WhenShortLinkNotExist_ThrowNotFoundError() {
        String code = "abcd";

        when(shortLinkRepository.findByShortLink(code)).thenReturn(Optional.empty());

        assertThrows(ShortLinkNotFoundException.class, () -> shortLinkService.GetLink(code));
    }

    // --- Test: AddLink() ---

    @Test
    @DisplayName("Should create a new ShortLink on database")
    void testAddLink_WhenCreateShortLink_Sucess() throws ShortLinkConflictException, UserNotFoundException {
        String email = "user@gmail.com";
        User mockUser = new User("", email, "", null, null);
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("https://original.com", "abcd");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(shortLinkRepository.findByShortLink(dto.shortLink())).thenReturn(Optional.empty());
        when(shortLinkRepository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLink result = shortLinkService.AddLink(dto, email);

        assertEquals(result.getShortLink(), dto.shortLink());
        assertEquals(result.getOriginalLink(), dto.link());
        assertEquals(result.getUser(), email);
    }

    @Test
    @DisplayName("Should create a new ShortLink with random code on database")
    void testAddLink_WithNullShortLink_GeneratesRandomCode() throws ShortLinkConflictException, UserNotFoundException {
        String email = "user@test.com";
        User mockUser = new User();
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("https://original.com", null);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(shortLinkRepository.findByShortLink(anyString())).thenReturn(Optional.empty());
        when(shortLinkRepository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLink result = shortLinkService.AddLink(dto, email);

        assertNotNull(result.getShortLink());
        assertTrue(result.getShortLink().length() > 0);
    }

    @Test
    @DisplayName("Should return user not found when try to create a new ShortLink")
    void testAddLink_WhenUserNotFound_ThrowsException() {
        String email = "nonexistent@test.com";
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("https://original.com", "custom123");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> shortLinkService.AddLink(dto, email));
    }

    @Test
    @DisplayName("Should return ShortLink already exists exception when try to create a ShortLink that already exists")
    void testAddLink_WhenShortLinkAlreadyExists_ThrowsException() {
        String email = "user@test.com";
        User mockUser = new User();
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("https://original.com", "duplicate");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(shortLinkRepository.findByShortLink("duplicate")).thenReturn(Optional.of(new ShortLink()));

        assertThrows(ShortLinkConflictException.class, () -> shortLinkService.AddLink(dto, email));
    }

    // --- Test: removeExpiredLinks() ---
    @Test
    void testRemoveExpiredLinks_CallsRepositoryDeleteMethod() {
        LocalDateTime now = LocalDateTime.now();
        doNothing().when(shortLinkRepository).deleteByExpirationTimeBefore(now);

        shortLinkService.removeExpiredLinks();

        verify(shortLinkRepository, times(1)).deleteByExpirationTimeBefore(any(LocalDateTime.class));
    }
}
