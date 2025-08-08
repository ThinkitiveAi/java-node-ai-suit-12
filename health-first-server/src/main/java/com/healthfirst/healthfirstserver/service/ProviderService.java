package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.dto.ProviderResponse;
import com.healthfirst.healthfirstserver.domain.entity.ClinicAddress;
import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.entity.ProviderCredentials;
import com.healthfirst.healthfirstserver.domain.enums.VerificationStatus;
import com.healthfirst.healthfirstserver.exception.BadRequestException;
import com.healthfirst.healthfirstserver.exception.ResourceAlreadyExistsException;
import com.healthfirst.healthfirstserver.exception.ValidationException;
import com.healthfirst.healthfirstserver.payload.request.ProviderRegistrationRequest;
import com.healthfirst.healthfirstserver.payload.response.ProviderRegistrationResponse;
import com.healthfirst.healthfirstserver.repository.ProviderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final PasswordService passwordService;

    @Transactional
    public ProviderRegistrationResponse registerProvider(ProviderRegistrationRequest request) {
        log.info("Starting provider registration for email: {}", request.getEmail());
        
        // Validate request
        validateRegistrationRequest(request);
        
        // Create and save provider
        Provider provider = createProviderFromRequest(request);
        Provider savedProvider = providerRepository.save(provider);
        
        log.info("Provider registered successfully with ID: {}", savedProvider.getId());
        
        // TODO: Send verification email
        
        return ProviderRegistrationResponse.success(
            savedProvider.getId(),
            savedProvider.getEmail()
        );
    }
    
    private void validateRegistrationRequest(ProviderRegistrationRequest request) {
        // Check email uniqueness
        if (providerRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use: " + request.getEmail());
        }
        
        // Check phone number uniqueness
        if (providerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException("Phone number already in use: " + request.getPhoneNumber());
        }
        
        // Check license number uniqueness
        if (providerRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new ResourceAlreadyExistsException("License number already in use: " + request.getLicenseNumber());
        }
        
        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }
        
        // Additional business validations can be added here
        if (request.getYearsOfExperience() != null && request.getYearsOfExperience() < 0) {
            throw new BadRequestException("Years of experience cannot be negative");
        }
    }
    
    private Provider createProviderFromRequest(ProviderRegistrationRequest request) {
        // Create clinic address
        ClinicAddress clinicAddress = ClinicAddress.builder()
                .street(request.getClinicAddress().getStreet())
                .city(request.getClinicAddress().getCity())
                .state(request.getClinicAddress().getState())
                .zip(request.getClinicAddress().getZip())
                .build();
        
        // Create provider
        Provider provider = Provider.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber().toUpperCase())
                .yearsOfExperience(request.getYearsOfExperience())
                .clinicAddress(clinicAddress)
                .verificationStatus(VerificationStatus.PENDING)
                .isActive(true)
                .build();
        
        // Create and set credentials
        String hashedPassword = passwordService.encodePassword(request.getPassword());
        ProviderCredentials credentials = ProviderCredentials.builder()
                .provider(provider)
                .passwordHash(hashedPassword)
                .lastPasswordChange(LocalDateTime.now())
                .build();
        
        provider.setCredentials(credentials);
        
        return provider;
    }
}
