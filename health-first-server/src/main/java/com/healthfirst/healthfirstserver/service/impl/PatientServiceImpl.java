package com.healthfirst.healthfirstserver.service.impl;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.exception.DuplicateResourceException;
import com.healthfirst.healthfirstserver.payload.request.patient.*;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientRegistrationResponse;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import com.healthfirst.healthfirstserver.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {
        // Check if email already exists
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Check if phone number already exists
        if (patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered");
        }

        // Map request to entity
        Patient patient = mapRegistrationRequestToPatient(request);
        
        // Save the patient
        Patient savedPatient = patientRepository.save(patient);

        // TODO: Send verification email
        // emailService.sendVerificationEmail(savedPatient.getEmail(), verificationToken);

        // Build and return response
        return PatientRegistrationResponse.builder()
                .success(true)
                .message("Patient registered successfully. Verification email sent.")
                .data(PatientRegistrationResponse.PatientData.builder()
                        .patientId(savedPatient.getId())
                        .email(savedPatient.getEmail())
                        .phoneNumber(savedPatient.getPhoneNumber())
                        .emailVerified(savedPatient.isEmailVerified())
                        .phoneVerified(savedPatient.isPhoneVerified())
                        .registeredAt(LocalDateTime.now())
                        .build())
                .build();
    }

    private Patient mapRegistrationRequestToPatient(PatientRegistrationRequest request) {
        return Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(mapAddressRequest(request.getAddress()))
                .emergencyContact(request.getEmergencyContact() != null ? 
                        mapEmergencyContactRequest(request.getEmergencyContact()) : null)
                .medicalHistory(request.getMedicalHistory() != null ? 
                        List.of(request.getMedicalHistory()) : null)
                .insuranceInfo(request.getInsuranceInfo() != null ? 
                        mapInsuranceInfoRequest(request.getInsuranceInfo()) : null)
                .isActive(true)
                .build();
    }

    private Patient.Address mapAddressRequest(AddressRequest addressRequest) {
        if (addressRequest == null) return null;
        return Patient.Address.builder()
                .street(addressRequest.getStreet())
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .zip(addressRequest.getZip())
                .build();
    }

    private Patient.EmergencyContact mapEmergencyContactRequest(EmergencyContactRequest ecRequest) {
        if (ecRequest == null) return null;
        return Patient.EmergencyContact.builder()
                .name(ecRequest.getName())
                .phone(ecRequest.getPhone())
                .relationship(ecRequest.getRelationship())
                .build();
    }

    private Patient.InsuranceInfo mapInsuranceInfoRequest(InsuranceInfoRequest iiRequest) {
        if (iiRequest == null) return null;
        return Patient.InsuranceInfo.builder()
                .provider(iiRequest.getProvider())
                .policyNumber(iiRequest.getPolicyNumber())
                .build();
    }
}
