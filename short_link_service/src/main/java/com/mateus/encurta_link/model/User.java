package com.mateus.encurta_link.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mateus.encurta_link.dto.Auth.UserRegisterRequest;
import com.mateus.encurta_link.dto.User.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id()
    @Column(name = "id", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ID;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email invalid")

    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be 6 characters or more")
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", length = 5)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private Set<ShortLink> userLinks;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    public User(UserRegisterRequest dto) {
        this.email = dto.email();
        this.password = dto.password();
    }
}
