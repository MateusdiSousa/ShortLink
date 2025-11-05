package com.mateus.encurta_link.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoResponse;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.User;
import com.mateus.encurta_link.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não existe");
        }
        return user.get();
    }

    public List<ShortLinkDtoResponse> getUserLinks(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());

        List<ShortLinkDtoResponse> shortLinkList = user.getUserLinks()
                .stream()
                .map((link) -> ShortLinkDtoResponse.fromEntity(link))
                .toList();
        shortLinkList = shortLinkList.stream().sorted((a , b) -> a.id().compareTo(b.id())).toList();
        return shortLinkList;
    }
}
