package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.payload.request.patient.PatientLoginRequest;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientAuthResponse;
import com.healthfirst.healthfirstserver.service.PatientAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient/auth")
@RequiredArgsConstructor
@Tag(name = "Patient Authentication", description = "APIs for patient authentication and token management")
public class PatientAuthController {

    private final PatientAuthService patientAuthService;

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate a patient",
        description = "Authenticates a patient with email and password, returns JWT tokens",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Authentication successful",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PatientAuthResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = @Content
            )
        }
    )
    public ResponseEntity<PatientAuthResponse> authenticateUser(@Valid @RequestBody PatientLoginRequest loginRequest) {
        return ResponseEntity.ok(patientAuthService.authenticatePatient(loginRequest));
    }

    @PostMapping("/refresh-token")
    @Operation(
        summary = "Refresh authentication token",
        description = "Refreshes the JWT token using a valid refresh token",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Token refreshed successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PatientAuthResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Invalid or expired refresh token",
                content = @Content
            )
        }
    )
    public ResponseEntity<PatientAuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        // Extract the token from the Authorization header (remove "Bearer " prefix if present)
        String token = refreshToken;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(patientAuthService.refreshToken(token));
    }
}
