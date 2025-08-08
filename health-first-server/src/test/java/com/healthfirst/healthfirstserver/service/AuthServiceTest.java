package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.entity.ProviderCredentials;
import com.healthfirst.healthfirstserver.domain.enums.VerificationStatus;
import com.healthfirst.healthfirstserver.exception.AccountNotActiveException;
import com.healthfirst.healthfirstserver.exception.AuthenticationException;
import com.healthfirst.healthfirstserver.exception.VerificationRequiredException;
import com.healthfirst.healthfirstserver.payload.request.LoginRequest;
import com.healthfirst.healthfirstserver.repository.ProviderRepository;
import com.healthfirst.healthfirstserver.security.JwtTokenProvider;
import com.healthfirst.healthfirstserver.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Provider testProvider;
    private ProviderCredentials testCredentials;
    private UserDetailsImpl testUserDetails;
    private final String testEmail = "test@example.com";
    private final String testPassword = "Password123!";
    private final String encodedPassword = "encodedPassword123";
    private final String testToken = "test.jwt.token";

    @BeforeEach
    void setUp() {
        // Setup test provider
        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setFirstName("Test");
        testProvider.setLastName("User");
        testProvider.setEmail(testEmail);
        testProvider.setVerificationStatus(VerificationStatus.VERIFIED);
        testProvider.setActive(true);

        // Setup test credentials
        testCredentials = new ProviderCredentials();
        testCredentials.setPasswordHash(encodedPassword);
        testCredentials.setLastPasswordChange(LocalDateTime.now());
        testProvider.setCredentials(testCredentials);

        // Setup test user details
        testUserDetails = UserDetailsImpl.build(testProvider);
    }

    @Test
    void authenticateUser_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(testEmail);
        loginRequest.setPassword(testPassword);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(providerRepository.findByEmail(testEmail))
            .thenReturn(java.util.Optional.of(testProvider));
        when(tokenProvider.generateToken(authentication))
            .thenReturn(testToken);
        when(tokenProvider.getJwtExpirationInMs())
            .thenReturn(3600000L); // 1 hour in milliseconds

        // When
        var response = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());
        assertEquals(testProvider.getId().toString(), response.getId());
        assertEquals(testEmail, response.getEmail());
        assertEquals("Test User", response.getFullName());
        
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(providerRepository).save(testProvider);
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void authenticateUser_InvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(testEmail);
        loginRequest.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When/Then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
            () -> authService.authenticateUser(loginRequest));
        
        assertEquals("Invalid username/email or password", exception.getMessage());
    }

    @Test
    void authenticateUser_AccountNotActive() {
        // Given
        testProvider.setActive(false);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(testEmail);
        loginRequest.setPassword(testPassword);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(providerRepository.findByEmail(testEmail))
            .thenReturn(java.util.Optional.of(testProvider));

        // When/Then
        AccountNotActiveException exception = assertThrows(AccountNotActiveException.class,
            () -> authService.authenticateUser(loginRequest));
        
        assertEquals("Account is not active. Please contact support.", exception.getMessage());
    }

    @Test
    void authenticateUser_EmailNotVerified() {
        // Given
        testProvider.setVerificationStatus(VerificationStatus.PENDING);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(testEmail);
        loginRequest.setPassword(testPassword);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(providerRepository.findByEmail(testEmail))
            .thenReturn(java.util.Optional.of(testProvider));

        // When/Then
        VerificationRequiredException exception = assertThrows(VerificationRequiredException.class,
            () -> authService.authenticateUser(loginRequest));
        
        assertEquals("Email verification required. Please check your email.", exception.getMessage());
    }
}
