package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "providers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone_number"),
        @UniqueConstraint(columnNames = "license_number")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Provider extends BaseEntity {
    
    // Explicit getters for fields that are causing compilation errors
    public UUID getId() {
        return super.getId();
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public ProviderCredentials getCredentials() {
        return credentials;
    }
    
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") // E.164 format
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;
    
    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String specialization;
    
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;
    
    @Min(0)
    @Max(50)
    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;
    
    @Embedded
    private ClinicAddress clinicAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private ProviderCredentials credentials;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
