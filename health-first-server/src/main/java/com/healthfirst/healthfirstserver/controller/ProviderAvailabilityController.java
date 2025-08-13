package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.domain.dto.AvailabilityRequest;
import com.healthfirst.healthfirstserver.domain.dto.AvailabilityResponse;
import com.healthfirst.healthfirstserver.domain.dto.SlotSearchRequest;
import com.healthfirst.healthfirstserver.domain.dto.SlotSearchResponse;
import com.healthfirst.healthfirstserver.service.ProviderAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/provider/availability")
@RequiredArgsConstructor
@Tag(name = "Provider Availability", description = "APIs for managing provider availability and appointment slots")
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService availabilityService;

    @PostMapping
    @Operation(
        summary = "Create availability slots",
        description = "Create a new availability slot or a series of recurring slots"
    )
    @ApiResponse(responseCode = "201", description = "Availability slots created successfully")
    public ResponseEntity<List<AvailabilityResponse>> createAvailability(
            @Valid @RequestBody AvailabilityRequest request) {
        List<AvailabilityResponse> response = availabilityService.createAvailability(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{availabilityId}")
    @Operation(
        summary = "Get availability by ID",
        description = "Get a specific availability slot by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Availability found")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @Parameter(description = "ID of the availability slot") 
            @PathVariable UUID availabilityId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(availabilityId));
    }

    @GetMapping
    @Operation(
        summary = "Get provider availability",
        description = "Get availability for a provider within a date range"
    )
    @ApiResponse(responseCode = "200", description = "List of availability slots")
    public ResponseEntity<List<AvailabilityResponse>> getProviderAvailability(
            @Parameter(description = "ID of the provider") 
            @RequestParam UUID providerId,
            
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Timezone (e.g., America/New_York)")
            @RequestParam(required = false) String timezone) {
        
        return ResponseEntity.ok(availabilityService.getProviderAvailability(
                providerId, startDate, endDate, timezone));
    }

    @PutMapping("/{availabilityId}")
    @Operation(
        summary = "Update availability",
        description = "Update an existing availability slot"
    )
    @ApiResponse(responseCode = "200", description = "Availability updated successfully")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @Parameter(description = "ID of the availability slot to update")
            @PathVariable UUID availabilityId,
            
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.updateAvailability(availabilityId, request));
    }

    @DeleteMapping("/{availabilityId}")
    @Operation(
        summary = "Delete availability",
        description = "Delete an availability slot"
    )
    @ApiResponse(responseCode = "204", description = "Availability deleted successfully")
    public ResponseEntity<Void> deleteAvailability(
            @Parameter(description = "ID of the availability slot to delete")
            @PathVariable UUID availabilityId,
            
            @Parameter(description = "Reason for deletion")
            @RequestParam(required = false) String reason) {
        
        availabilityService.deleteAvailability(availabilityId, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(
        summary = "Search available slots",
        description = "Search for available appointment slots based on various criteria"
    )
    @ApiResponse(responseCode = "200", description = "Search results")
    public ResponseEntity<SlotSearchResponse> searchAvailableSlots(
            @Valid @RequestBody SlotSearchRequest request) {
        return ResponseEntity.ok(availabilityService.searchAvailableSlots(request));
    }

    @GetMapping("/provider/{providerId}")
    @Operation(
        summary = "Get paginated provider availabilities",
        description = "Get a paginated list of availabilities for a provider"
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of availabilities")
    public ResponseEntity<Page<AvailabilityResponse>> getProviderAvailabilities(
            @Parameter(description = "ID of the provider")
            @PathVariable UUID providerId,
            
            Pageable pageable) {
        return ResponseEntity.ok(availabilityService.getProviderAvailabilities(providerId, pageable));
    }

    @PatchMapping("/{availabilityId}/status")
    @Operation(
        summary = "Update availability status",
        description = "Update the status of an availability slot (e.g., available, blocked, maintenance)"
    )
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    public ResponseEntity<AvailabilityResponse> updateStatus(
            @Parameter(description = "ID of the availability slot")
            @PathVariable UUID availabilityId,
            
            @Parameter(description = "New status (AVAILABLE, BLOCKED, MAINTENANCE, etc.)")
            @RequestParam String status,
            
            @Parameter(description = "Reason for the status change")
            @RequestParam(required = false) String reason) {
        
        return ResponseEntity.ok(availabilityService.updateAvailabilityStatus(
                availabilityId, status, reason));
    }

    @GetMapping("/provider/{providerId}/upcoming")
    @Operation(
        summary = "Get upcoming availabilities",
        description = "Get a list of upcoming availabilities for a provider"
    )
    @ApiResponse(responseCode = "200", description = "List of upcoming availabilities")
    public ResponseEntity<List<AvailabilityResponse>> getUpcomingAvailabilities(
            @Parameter(description = "ID of the provider")
            @PathVariable UUID providerId,
            
            @Parameter(description = "Timezone (e.g., America/New_York)")
            @RequestParam(required = false) String timezone) {
        
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(3); // Default to 3 months in the future
        
        return ResponseEntity.ok(availabilityService.getProviderAvailability(
                providerId, today, endDate, timezone));
    }
}
