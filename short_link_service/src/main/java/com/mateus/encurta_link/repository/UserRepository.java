package com.mateus.encurta_link.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mateus.encurta_link.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
}
