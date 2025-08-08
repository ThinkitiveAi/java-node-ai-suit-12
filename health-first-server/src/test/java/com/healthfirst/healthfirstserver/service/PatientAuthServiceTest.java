package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.exception.AccountNotActiveException;
import com.healthfirst.healthfirstserver.exception.AuthenticationException;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.payload.request.patient.PatientLoginRequest;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientAuthResponse;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import com.healthfirst.healthfirstserver.security.JwtTokenProvider;
import com.healthfirst.healthfirstserver.security.PatientDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientAuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientAuthService patientAuthService;

    private Patient testPatient;
    private PatientDetails testPatientDetails;
    private PatientLoginRequest loginRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Setup test patient
        testPatient = new Patient();
        testPatient.setId(UUID.randomUUID());
        testPatient.setEmail("test@example.com");
        testPatient.setPhoneNumber("+1234567890");
        testPatient.setPasswordHash("hashedPassword");
        testPatient.setEmailVerified(true);
        testPatient.setPhoneVerified(false);
        testPatient.setActive(true);

        // Setup patient details
        testPatientDetails = new PatientDetails(
                testPatient.getId(),
                testPatient.getEmail(),
                testPatient.getPhoneNumber(),
                testPatient.getPasswordHash(),
                testPatient.isActive(),
                testPatient.isEmailVerified(),
                testPatient.isPhoneVerified()
        );

        // Setup login request
        loginRequest = new PatientLoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Setup authentication
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testPatientDetails);
    }

    @Test
    void authenticatePatient_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any())).thenReturn("jwt-token");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(tokenProvider.getJwtExpirationInMs()).thenReturn(3600000);

        // Act
        PatientAuthResponse response = patientAuthService.authenticatePatient(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(testPatient.getId(), response.getId());
        assertEquals(testPatient.getEmail(), response.getEmail());
        assertTrue(response.getTokenExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void authenticatePatient_WithDisabledAccount_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("Account is disabled"));

        // Act & Assert
        assertThrows(AccountNotActiveException.class, () -> {
            patientAuthService.authenticatePatient(loginRequest);
        });
    }

    @Test
    void authenticatePatient_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            patientAuthService.authenticatePatient(loginRequest);
        });
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewTokens() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(refreshToken)).thenReturn(testPatient.getId());
        when(patientRepository.findById(testPatient.getId())).thenReturn(Optional.of(testPatient));
        when(tokenProvider.generateToken(any())).thenReturn("new-jwt-token");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("new-refresh-token");
        when(tokenProvider.getJwtExpirationInMs()).thenReturn(3600000);

        // Act
        PatientAuthResponse response = patientAuthService.refreshToken(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("new-jwt-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals(testPatient.getId(), response.getId());
        assertTrue(response.getTokenExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid-token";
        when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            patientAuthService.refreshToken(invalidToken);
        });
    }

    @Test
    void refreshToken_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        String refreshToken = "valid-token";
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        UUID nonExistentUserId = UUID.randomUUID();
        when(tokenProvider.getUserIdFromToken(refreshToken)).thenReturn(nonExistentUserId);
        when(patientRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            patientAuthService.refreshToken(refreshToken);
        });
    }
}
