package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.payload.request.ProviderRegistrationRequest;
import com.healthfirst.healthfirstserver.payload.response.ProviderRegistrationResponse;
import com.healthfirst.healthfirstserver.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Provider", description = "Provider management APIs")
@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @Operation(
        summary = "Register a new healthcare provider",
        description = "Registers a new healthcare provider with the system. Returns the provider's ID and verification status."
    )
    @PostMapping("/register")
    public ResponseEntity<ProviderRegistrationResponse> registerProvider(
            @Valid @RequestBody ProviderRegistrationRequest request) {
        ProviderRegistrationResponse response = providerService.registerProvider(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Additional provider-related endpoints can be added here
    // For example: getProfile, updateProfile, etc.
}
