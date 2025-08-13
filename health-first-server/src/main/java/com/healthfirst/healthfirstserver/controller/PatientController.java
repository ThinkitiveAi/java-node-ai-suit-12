package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.payload.request.patient.PatientRegistrationRequest;
// Using fully qualified class name to avoid ambiguity with Swagger's ApiResponse
import com.healthfirst.healthfirstserver.payload.response.patient.PatientRegistrationResponse;
import com.healthfirst.healthfirstserver.service.PatientService;
import com.healthfirst.healthfirstserver.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient management APIs")
public class PatientController {

    private final PatientService patientService;
    private final VerificationService verificationService;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new patient",
        description = "Registers a new patient with the provided details.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Patient registered successfully",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PatientRegistrationResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = @io.swagger.v3.oas.annotations.media.Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Email or phone number already registered",
                content = @io.swagger.v3.oas.annotations.media.Content
            )
        }
    )
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        
        PatientRegistrationResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/verify-email")
    @Operation(
        summary = "Verify email",
        description = "Verifies the user's email using the verification token.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Email verified successfully",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.healthfirst.healthfirstserver.payload.response.ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid or expired token",
                content = @io.swagger.v3.oas.annotations.media.Content
            )
        }
    )
    public ResponseEntity<com.healthfirst.healthfirstserver.payload.response.ApiResponse> verifyEmail(
            @RequestParam("token") String token) {
        verificationService.verifyEmail(token);
        return ResponseEntity.ok(new com.healthfirst.healthfirstserver.payload.response.ApiResponse(true, "Email verified successfully"));
    }
    
    @PostMapping("/resend-verification")
    @Operation(
        summary = "Resend verification email",
        description = "Resends the verification email to the specified email address.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Verification email resent successfully",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.healthfirst.healthfirstserver.payload.response.ApiResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Email is already verified or invalid",
                content = @io.swagger.v3.oas.annotations.media.Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "No patient found with the provided email",
                content = @io.swagger.v3.oas.annotations.media.Content
            )
        }
    )
    public ResponseEntity<com.healthfirst.healthfirstserver.payload.response.ApiResponse> resendVerificationEmail(
            @RequestParam @Email(message = "Invalid email format") String email) {
        
        verificationService.resendVerificationEmail(email);
        return ResponseEntity.ok(new com.healthfirst.healthfirstserver.payload.response.ApiResponse(true, "Verification email resent successfully"));
    }
}
