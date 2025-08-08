package com.healthfirst.healthfirstserver.security;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@AllArgsConstructor
public class PatientDetails implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String PATIENT_ROLE = "PATIENT";

    private final UUID id;
    private final String email;
    private final String phoneNumber;
    @JsonIgnore
    private final String password;
    private final boolean active;
    private final boolean emailVerified;
    private final boolean phoneVerified;

    public static PatientDetails build(Patient patient) {
        return new PatientDetails(
                patient.getId(),
                patient.getEmail(),
                patient.getPhoneNumber(),
                patient.getPasswordHash(),
                patient.isActive(),
                patient.isEmailVerified(),
                patient.isPhoneVerified()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + PATIENT_ROLE));
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Using email as username for authentication
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
        return active && emailVerified; // Only allow login if account is active and email is verified
    }
}
