package com.healthfirst.healthfirstserver.security;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Patient not found with email: " + email)
                );

        if (!patient.isActive()) {
            throw new ResourceNotFoundException("Patient account is not active");
        }

        return PatientDetails.build(patient);
    }

    @Transactional
    public UserDetails loadUserById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> 
                    new ResourceNotFoundException("Patient not found with id: " + id)
                );

        if (!patient.isActive()) {
            throw new ResourceNotFoundException("Patient account is not active");
        }

        return PatientDetails.build(patient);
    }
}
