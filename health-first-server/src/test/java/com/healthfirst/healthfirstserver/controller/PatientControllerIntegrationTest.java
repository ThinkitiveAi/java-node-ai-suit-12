package com.healthfirst.healthfirstserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.payload.request.patient.PatientLoginRequest;
import com.healthfirst.healthfirstserver.payload.request.patient.PatientRegistrationRequest;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientAuthResponse;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientRegistrationResponse;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import com.healthfirst.healthfirstserver.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Patient testPatient;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "Test@1234";

    @BeforeEach
    void setUp() {
        // Setup test patient
        testPatient = new Patient();
        testPatient.setId(UUID.randomUUID());
        testPatient.setFirstName("Test");
        testPatient.setLastName("User");
        testPatient.setEmail(TEST_EMAIL);
        testPatient.setPasswordHash(passwordEncoder.encode(TEST_PASSWORD));
        testPatient.setPhoneNumber("+1234567890");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testPatient.setEmailVerified(true);
        testPatient.setActive(true);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
    }

    @Test
    void registerPatient_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        PatientRegistrationRequest request = createValidRegistrationRequest();
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/patient/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Verify response
        String response = result.getResponse().getContentAsString();
        PatientRegistrationResponse registrationResponse = objectMapper.readValue(
                response, PatientRegistrationResponse.class);
        
        assertTrue(registrationResponse.isSuccess());
        assertEquals("Patient registered successfully. Verification email sent.", 
                    registrationResponse.getMessage());
        assertNotNull(registrationResponse.getData().getPatientId());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        // Arrange
        PatientLoginRequest loginRequest = new PatientLoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        when(patientRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(testPatient));
        when(tokenProvider.generateToken(any())).thenReturn("test-jwt-token");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("test-refresh-token");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/patient/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response
        String response = result.getResponse().getContentAsString();
        PatientAuthResponse authResponse = objectMapper.readValue(response, PatientAuthResponse.class);
        
        assertEquals("test-jwt-token", authResponse.getToken());
        assertEquals("test-refresh-token", authResponse.getRefreshToken());
        assertEquals(TEST_EMAIL, authResponse.getEmail());
    }

    @Test
    void verifyEmail_WithValidToken_ShouldReturnSuccess() throws Exception {
        // Arrange
        String token = "valid-verification-token";
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // Act & Assert
        mockMvc.perform(get("/api/v1/patient/verify-email")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully"));
    }

    @Test
    void resendVerification_WithValidEmail_ShouldReturnSuccess() throws Exception {
        // Arrange
        String email = TEST_EMAIL;
        when(patientRepository.findByEmail(email)).thenReturn(java.util.Optional.of(testPatient));

        // Act & Assert
        mockMvc.perform(post("/api/v1/patient/resend-verification")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Verification email resent successfully"));
    }

    private PatientRegistrationRequest createValidRegistrationRequest() {
        PatientRegistrationRequest request = new PatientRegistrationRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test@example.com");
        request.setPhoneNumber("+1234567890");
        request.setPassword("Test@1234");
        request.setConfirmPassword("Test@1234");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender(Patient.Gender.OTHER);
        
        // Set up address
        PatientRegistrationRequest.AddressRequest address = new PatientRegistrationRequest.AddressRequest();
        address.setStreet("123 Test St");
        address.setCity("Test City");
        address.setState("TS");
        address.setZip("12345");
        request.setAddress(address);
        
        return request;
    }
}
