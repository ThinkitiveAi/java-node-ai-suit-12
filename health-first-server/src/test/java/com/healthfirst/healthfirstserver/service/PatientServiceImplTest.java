package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.domain.entity.VerificationToken;
import com.healthfirst.healthfirstserver.exception.DuplicateResourceException;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.payload.request.patient.*;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientRegistrationResponse;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import com.healthfirst.healthfirstserver.security.PatientDetails;
import com.healthfirst.healthfirstserver.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationService verificationService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientRegistrationRequest registrationRequest;
    private Patient patient;
    private Patient savedPatient;

    @BeforeEach
    void setUp() {
        // Setup test data
        registrationRequest = createTestRegistrationRequest();
        patient = createTestPatient();
        savedPatient = createTestPatient();
        savedPatient.setId(UUID.randomUUID());
    }

    @Test
    void registerPatient_WithValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        when(modelMapper.map(any(), eq(PatientRegistrationResponse.class)))
                .thenReturn(createTestRegistrationResponse());

        // Act
        PatientRegistrationResponse response = patientService.registerPatient(registrationRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jane.smith@email.com", response.getEmail());
        assertEquals("+1234567890", response.getPhoneNumber());
        verify(verificationService, times(1)).createVerificationToken(any(Patient.class), any());
    }

    @Test
    void registerPatient_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            patientService.registerPatient(registrationRequest);
        });
    }

    @Test
    void registerPatient_WithExistingPhone_ShouldThrowException() {
        // Arrange
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            patientService.registerPatient(registrationRequest);
        });
    }

    private PatientRegistrationRequest createTestRegistrationRequest() {
        AddressRequest address = new AddressRequest();
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setZip("10001");

        EmergencyContactRequest emergencyContact = new EmergencyContactRequest();
        emergencyContact.setName("John Smith");
        emergencyContact.setPhone("+1234567891");
        emergencyContact.setRelationship("Spouse");

        InsuranceInfoRequest insuranceInfo = new InsuranceInfoRequest();
        insuranceInfo.setProvider("Blue Cross");
        insuranceInfo.setPolicyNumber("BC123456789");

        PatientRegistrationRequest request = new PatientRegistrationRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@email.com");
        request.setPhoneNumber("+1234567890");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setGender(Patient.Gender.FEMALE);
        request.setAddress(address);
        request.setEmergencyContact(emergencyContact);
        request.setInsuranceInfo(insuranceInfo);
        request.setMedicalHistory(new String[]{"Hypertension", "Asthma"});

        return request;
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        patient.setEmail("jane.smith@email.com");
        patient.setPhoneNumber("+1234567890");
        patient.setPasswordHash("hashedPassword");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setGender(Patient.Gender.FEMALE);
        return patient;
    }

    private PatientRegistrationResponse createTestRegistrationResponse() {
        return PatientRegistrationResponse.builder()
                .success(true)
                .message("Patient registered successfully")
                .data(PatientRegistrationResponse.PatientData.builder()
                        .patientId(UUID.randomUUID())
                        .email("jane.smith@email.com")
                        .phoneNumber("+1234567890")
                        .emailVerified(false)
                        .phoneVerified(false)
                        .registeredAt(LocalDate.now().atStartOfDay())
                        .build())
                .build();
    }
}
