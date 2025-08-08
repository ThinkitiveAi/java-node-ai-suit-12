package com.healthfirst.healthfirstserver.repository;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    Optional<Provider> findByEmail(String email);
    
    Optional<Provider> findByEmailIgnoreCase(String email);
}
