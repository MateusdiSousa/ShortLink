package com.mateus.encurta_link.service;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.exceptions.ShortLinkConflictException;
import com.mateus.encurta_link.exceptions.ShortLinkNotFoundException;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.ShortLink;
import com.mateus.encurta_link.model.User;
import com.mateus.encurta_link.repository.ShortLinkRepository;
import com.mateus.encurta_link.repository.UserRepository;
import com.mateus.encurta_link.service.interfaces.IShortLinkService;
import com.mateus.encurta_link.utils.RandomAlphanumeric;

import jakarta.transaction.Transactional;

@Service
public class ShortLinkService implements IShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final UserRepository userRepository;

    public ShortLinkService(ShortLinkRepository shortLinkRepository, UserRepository userRepository) {
        this.shortLinkRepository = shortLinkRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(value = "shorLink", key = "#code")
    public String GetLink(String code) throws ShortLinkNotFoundException {
        ShortLink link = this.shortLinkRepository.findByShortLink(code)
                .orElseThrow(() -> new ShortLinkNotFoundException());
        return link.getOriginalLink();
    }

    public ShortLink AddLink(ShortLinkDtoRequest dto, String email)
            throws ShortLinkConflictException, UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());

        ShortLink newShortLink = new ShortLink(dto);
        newShortLink.setUser(user);

        if (dto.shortLink() == null || dto.shortLink().isBlank()) {
            String code = RandomShortLink();
            newShortLink.setShortLink(code);
            return shortLinkRepository.save(newShortLink);
        }

        boolean linkExist = shortLinkRepository.findByShortLink(dto.shortLink()).isPresent();

        if (linkExist) {
            throw new ShortLinkConflictException();
        }

        return shortLinkRepository.save(newShortLink);
    }

    private String RandomShortLink() {
        while (true) {
            String code = RandomAlphanumeric.GenerateString();
            if (shortLinkRepository.findByShortLink(code).isEmpty()) {
                return code;
            }
        }
    }

    @Transactional
    public void removeExpiredLinks() {
        shortLinkRepository.deleteByExpirationTimeBefore(LocalDateTime.now());

        System.out.println("\n\n\n\nShort Links removed\n\n\n\n\n");
    }
}
