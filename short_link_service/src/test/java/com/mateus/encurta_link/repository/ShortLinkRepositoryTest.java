package com.mateus.encurta_link.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mateus.encurta_link.dto.Auth.UserRegisterRequest;
import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.model.ShortLink;
import com.mateus.encurta_link.model.User;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class ShortLinkRepositoryTest {
    @Autowired
    ShortLinkRepository shortLinkRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should create a new ShortLink in database")
    void createShortLinkSucess() {
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("www.google.com", "google");
        ShortLink newShortLink = createShortLink(dto);
        System.out.println(newShortLink);
        Assertions.assertThat(newShortLink.getId()).isNotBlank();
    }

    @Test
    @DisplayName("Should get ShortLinkcode from database by shortcode")
    void findShortLinkByShortCodeSucess() {
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("www.google.com", "google");
        createShortLink(dto);
        Optional<ShortLink> shortLink = shortLinkRepository.findByShortLink("google");
        Assertions.assertThat(shortLink.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should return empty when shortcode doesn't exist")
    void findShortLinkByShortCodeNotFound() {
        Optional<ShortLink> shortLink = shortLinkRepository.findByShortLink("nonexistent");
        Assertions.assertThat(shortLink.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete all ShortLink expired from database")
    void deleteShortLinkByExpirationTime() {
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("www.google.com", "google");
        createShortLinkExpired(dto);
        shortLinkRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
        List<ShortLink> allShortLinks = shortLinkRepository.findAll();
        Assertions.assertThat(allShortLinks.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should not delete ShortLink when not expired")
    void notDeleteShortLinkWhenNotExpired() {
        ShortLinkDtoRequest dto = new ShortLinkDtoRequest("www.google.com", "google");
        createShortLink(dto); // Not expired
        
        shortLinkRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
        List<ShortLink> allShortLinks = shortLinkRepository.findAll();

        Assertions.assertThat(allShortLinks.size()).isEqualTo(1);
    }

    private ShortLink createShortLink(ShortLinkDtoRequest dto) {
        User newUser = createUser(
                new UserRegisterRequest("teste@gmail.com", "12345678"));
        ShortLink newShortLink = new ShortLink(dto);
        newShortLink.setUser(newUser);
        newShortLink.setExpirationTime(LocalDateTime.now().plusDays(7));
        return newShortLink = shortLinkRepository.save(newShortLink);
    }

    private ShortLink createShortLinkExpired(ShortLinkDtoRequest dto) {
        User newUser = createUser(
                new UserRegisterRequest("teste@gmail.com", "12345678"));
        ShortLink newShortLink = new ShortLink(dto);
        newShortLink.setUser(newUser);
        newShortLink.setExpirationTime(LocalDateTime.now().minusDays(1));

        return newShortLink = shortLinkRepository.save(newShortLink);
    }

    private User createUser(UserRegisterRequest dto) {
        User newUser = new User(dto);
        entityManager.persist(newUser);
        return newUser;
    }
}
