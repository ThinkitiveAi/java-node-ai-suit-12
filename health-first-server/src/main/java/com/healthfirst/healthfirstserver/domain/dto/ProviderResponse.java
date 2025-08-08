package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String licenseNumber;
    private boolean isActive;
    
    public static ProviderResponse fromEntity(Provider provider) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .email(provider.getEmail())
                .phoneNumber(provider.getPhoneNumber())
                .specialization(provider.getSpecialization())
                .licenseNumber(provider.getLicenseNumber())
                .isActive(provider.isActive())
                .build();
    }
}
