package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.domain.entity.VerificationToken;
import com.healthfirst.healthfirstserver.exception.ExpiredTokenException;
import com.healthfirst.healthfirstserver.exception.InvalidTokenException;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import com.healthfirst.healthfirstserver.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final PatientRepository patientRepository;
    private final EmailService emailService;

    @Transactional
    public void createVerificationToken(Patient patient, VerificationToken.TokenType tokenType) {
        // Delete any existing tokens for this user and token type
        tokenRepository.deleteByPatientIdAndTokenType(patient.getId(), tokenType);
        
        // Create and save new token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .patient(patient)
                .tokenType(tokenType)
                .build();
        
        tokenRepository.save(verificationToken);
        
        // Send verification email if token type is EMAIL_VERIFICATION
        if (tokenType == VerificationToken.TokenType.EMAIL_VERIFICATION) {
            emailService.sendVerificationEmail(patient, token);
        }
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            throw new ExpiredTokenException("Verification token has expired");
        }

        if (verificationToken.getTokenType() != VerificationToken.TokenType.EMAIL_VERIFICATION) {
            throw new InvalidTokenException("Invalid token type for email verification");
        }

        // Update patient's email verification status
        Patient patient = verificationToken.getPatient();
        patient.setEmailVerified(true);
        patient.setActive(true); // Activate the account
        patientRepository.save(patient);

        // Delete the used token
        tokenRepository.delete(verificationToken);
        
        log.info("Email verified for patient: {}", patient.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));

        if (patient.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        // Create and send a new verification token
        createVerificationToken(patient, VerificationToken.TokenType.EMAIL_VERIFICATION);
    }
}
