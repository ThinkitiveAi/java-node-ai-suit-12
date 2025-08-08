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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientAuthService {

    private final AuthenticationManager authenticationManager;
    private final PatientRepository patientRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PatientAuthResponse authenticatePatient(PatientLoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail().toLowerCase(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate tokens
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(tokenProvider.getJwtExpirationInMs() / 1000);
            
            // Get user details
            PatientDetails patientDetails = (PatientDetails) authentication.getPrincipal();
            
            // Return the response
            return PatientAuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .id(patientDetails.getId())
                    .email(patientDetails.getEmail())
                    .phoneNumber(patientDetails.getPhoneNumber())
                    .emailVerified(patientDetails.isEmailVerified())
                    .phoneVerified(patientDetails.isPhoneVerified())
                    .tokenExpiry(tokenExpiry)
                    .build();
                    
        } catch (DisabledException e) {
            throw new AccountNotActiveException("Account is not active. Please verify your email first.");
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid email or password");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed");
        }
    }

    @Transactional
    public PatientAuthResponse refreshToken(String refreshToken) {
        try {
            // Validate the refresh token
            if (!tokenProvider.validateToken(refreshToken)) {
                throw new AuthenticationException("Invalid refresh token");
            }

            // Get user ID from the token
            UUID userId = tokenProvider.getUserIdFromToken(refreshToken);
            
            // Load user details
            Patient patient = patientRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    
            if (!patient.isActive()) {
                throw new AccountNotActiveException("Account is not active");
            }

            // Create authentication
            PatientDetails patientDetails = PatientDetails.build(patient);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    patientDetails, null, patientDetails.getAuthorities());

            // Generate new tokens
            String newAccessToken = tokenProvider.generateToken(authentication);
            String newRefreshToken = tokenProvider.generateRefreshToken(authentication);
            LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(tokenProvider.getJwtExpirationInMs() / 1000);

            return PatientAuthResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .id(patient.getId())
                    .email(patient.getEmail())
                    .phoneNumber(patient.getPhoneNumber())
                    .emailVerified(patient.isEmailVerified())
                    .phoneVerified(patient.isPhoneVerified())
                    .tokenExpiry(tokenExpiry)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            throw new AuthenticationException("Failed to refresh token");
        }
    }
}
