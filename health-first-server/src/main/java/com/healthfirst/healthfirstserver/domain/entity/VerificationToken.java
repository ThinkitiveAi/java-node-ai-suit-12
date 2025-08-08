package com.healthfirst.healthfirstserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    
    private static final int EXPIRATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = Patient.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    @PrePersist
    protected void onCreate() {
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
    }

    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET,
        PHONE_VERIFICATION
    }
}
