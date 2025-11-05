package com.mateus.encurta_link.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mateus.encurta_link.dto.Auth.AuthenticationResponse;
import com.mateus.encurta_link.dto.Auth.UserRegisterRequest;
import com.mateus.encurta_link.dto.User.UserRole;
import com.mateus.encurta_link.exceptions.InvalidCredentialsException;
import com.mateus.encurta_link.exceptions.UserAlreadyExistException;
import com.mateus.encurta_link.model.User;
import com.mateus.encurta_link.repository.UserRepository;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository,
            JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean UserAlreadyExist(String email) {
        return !userRepository.findByEmail(email).isEmpty();
    }


    public AuthenticationResponse Register(UserRegisterRequest request) throws UserAlreadyExistException {

        if (UserAlreadyExist(request.email())) {
            throw new UserAlreadyExistException();
        }

        User user = new User();

        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        user = this.userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse Authenticate(User request) throws InvalidCredentialsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException());
            
            String token = this.jwtService.generateToken(user);
            
            return new AuthenticationResponse(token);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }
        

    }
}
