package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.payload.request.LoginRequest;
import com.healthfirst.healthfirstserver.payload.response.JwtResponse;
import com.healthfirst.healthfirstserver.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        // Setup test request
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("test@example.com");
        loginRequest.setPassword("Password123!");

        // Setup test response
        jwtResponse = JwtResponse.builder()
                .token("test.jwt.token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .id("123e4567-e89b-12d3-a456-426614174000")
                .email("test@example.com")
                .fullName("Test User")
                .build();
    }

    @Test
    void login_ValidCredentials_ReturnsJwtResponse() {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class)))
            .thenReturn(jwtResponse);

        // When
        ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jwtResponse.getToken(), response.getBody().getToken());
        assertEquals(jwtResponse.getTokenType(), response.getBody().getTokenType());
        assertEquals(jwtResponse.getExpiresIn(), response.getBody().getExpiresIn());
        
        verify(authService, times(1)).authenticateUser(loginRequest);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // When/Then
        assertThrows(RuntimeException.class, 
            () -> authController.authenticateUser(loginRequest));
        
        verify(authService, times(1)).authenticateUser(loginRequest);
    }

    @Test
    void login_NullRequest_ThrowsException() {
        // When/Then
        assertThrows(NullPointerException.class, 
            () -> authController.authenticateUser(null));
        
        verify(authService, never()).authenticateUser(any());
    }
}
