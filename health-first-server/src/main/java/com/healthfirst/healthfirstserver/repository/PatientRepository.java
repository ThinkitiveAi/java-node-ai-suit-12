package com.healthfirst.healthfirstserver.repository;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    Optional<Patient> findByEmail(String email);
    
    Optional<Patient> findByPhoneNumber(String phoneNumber);
}
