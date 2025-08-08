package com.healthfirst.healthfirstserver.repository;

import com.healthfirst.healthfirstserver.domain.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    
    Optional<VerificationToken> findByToken(String token);
    
    Optional<VerificationToken> findByPatientIdAndTokenType(UUID patientId, VerificationToken.TokenType tokenType);
    
    void deleteByPatientIdAndTokenType(UUID patientId, VerificationToken.TokenType tokenType);
}
