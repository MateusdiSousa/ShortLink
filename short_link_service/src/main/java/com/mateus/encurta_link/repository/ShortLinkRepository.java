package com.mateus.encurta_link.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mateus.encurta_link.model.ShortLink;

public interface ShortLinkRepository extends JpaRepository<ShortLink, String> {
    Optional<ShortLink> findByShortLink(String shortLink);

    void deleteByExpirationTimeBefore(LocalDateTime currentTime);
}