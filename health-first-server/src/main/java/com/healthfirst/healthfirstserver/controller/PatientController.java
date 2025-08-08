package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.payload.request.patient.PatientRegistrationRequest;
import com.healthfirst.healthfirstserver.payload.response.ApiResponse;
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
        description = "Registers a new patient with the provided details. Email and phone number must be unique. " +
                    "A verification email will be sent to the provided email address.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Patient registered successfully. Verification email sent.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PatientRegistrationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Email or phone number already registered",
                content = @Content
            )
        }
    )
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        
        PatientRegistrationResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/verify-email")
    @Operation(
        summary = "Verify email address",
        description = "Verifies a patient's email address using the verification token sent to their email.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email verified successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid or expired token",
                content = @Content
            )
        }
    )
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        verificationService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse(true, "Email verified successfully"));
    }
    
    @PostMapping("/resend-verification")
    @Operation(
        summary = "Resend verification email",
        description = "Resends the verification email to the specified email address.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Verification email resent successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Email is already verified or invalid",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "No patient found with the provided email",
                content = @Content
            )
        }
    )
    public ResponseEntity<ApiResponse> resendVerificationEmail(
            @RequestParam @Email(message = "Invalid email format") String email) {
        
        verificationService.resendVerificationEmail(email);
        return ResponseEntity.ok(new ApiResponse(true, "Verification email resent successfully"));
    }
}
