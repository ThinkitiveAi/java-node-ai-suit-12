package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.enums.VerificationStatus;
import com.healthfirst.healthfirstserver.exception.AccountNotActiveException;
import com.healthfirst.healthfirstserver.exception.AuthenticationException;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.exception.VerificationRequiredException;
import com.healthfirst.healthfirstserver.payload.request.LoginRequest;
import com.healthfirst.healthfirstserver.payload.response.JwtResponse;
import com.healthfirst.healthfirstserver.repository.ProviderRepository;
import com.healthfirst.healthfirstserver.security.JwtTokenProvider;
import com.healthfirst.healthfirstserver.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail().toLowerCase(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Check if account is active
            if (!userDetails.isEnabled()) {
                throw new AccountNotActiveException("Account is not active. Please contact support.");
            }
            
            // Check if email is verified
            Provider provider = providerRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userDetails.getEmail()));
                
            if (provider.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new VerificationRequiredException("Email verification required. Please check your email.");
            }
            
            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);
            
            // Update last login timestamp
            provider.setLastLogin(LocalDateTime.now());
            providerRepository.save(provider);
            
            log.info("User logged in successfully: {}", userDetails.getEmail());
            
            // Get user roles
            List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            // Build and return JWT response
            return JwtResponse.builder()
                    .token(jwt)
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getJwtExpirationInMs() / 1000) // Convert to seconds
                    .id(userDetails.getId().toString())
                    .email(userDetails.getEmail())
                    .fullName(userDetails.getFullName())
                    .specialization(userDetails.getSpecialization())
                    .roles(roles)
                    .build();
                    
        } catch (DisabledException e) {
            log.error("User account is disabled: {}", loginRequest.getEmail());
            throw new AccountNotActiveException("Account is disabled. Please contact support.");
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", loginRequest.getEmail());
            throw new AuthenticationException("Invalid username/email or password");
        } catch (Exception e) {
            log.error("Authentication error for user: {}", loginRequest.getEmail(), e);
            throw new AuthenticationException("Authentication failed. Please try again.");
        }
    }
}
