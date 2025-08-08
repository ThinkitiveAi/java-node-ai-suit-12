package com.healthfirst.healthfirstserver.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "provider_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProviderCredentials extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false, foreignKey = @ForeignKey(name = "fk_provider_credentials_provider"))
    private Provider provider;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(name = "last_password_change", nullable = false)
    private LocalDateTime lastPasswordChange;
    
    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (this.lastPasswordChange == null) {
            this.lastPasswordChange = LocalDateTime.now();
        }
    }
}
