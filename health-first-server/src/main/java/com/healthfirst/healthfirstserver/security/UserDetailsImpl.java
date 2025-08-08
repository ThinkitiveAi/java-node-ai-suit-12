package com.healthfirst.healthfirstserver.security;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String email;
    private final String fullName;
    private final String specialization;
    private final boolean isActive;

    @JsonIgnore
    private final String password;

    public static UserDetailsImpl build(Provider provider) {
        // Ensure the provider and its credentials are not null
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        
        // Get the password hash from credentials
        String passwordHash = "";
        if (provider.getCredentials() != null) {
            passwordHash = provider.getCredentials().getPasswordHash();
        }
        
        // Build the UserDetailsImpl
        return new UserDetailsImpl(
            provider.getId(),
            provider.getEmail(),
            provider.getFirstName() + " " + provider.getLastName(),
            provider.getSpecialization(),
            provider.isActive(),
            passwordHash
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROVIDER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
