package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.dto.AvailabilityRequest;
import com.healthfirst.healthfirstserver.domain.dto.AvailabilityResponse;
import com.healthfirst.healthfirstserver.domain.dto.SlotSearchRequest;
import com.healthfirst.healthfirstserver.domain.dto.SlotSearchResponse;
import com.healthfirst.healthfirstserver.domain.entity.ProviderAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ProviderAvailabilityService {
    
    /**
     * Create a new availability slot or a series of recurring slots
     */
    List<AvailabilityResponse> createAvailability(AvailabilityRequest request);
    
    /**
     * Update an existing availability slot
     */
    AvailabilityResponse updateAvailability(UUID availabilityId, AvailabilityRequest request);
    
    /**
     * Delete an availability slot
     */
    void deleteAvailability(UUID availabilityId, String reason);
    
    /**
     * Get provider availability for a specific date range
     */
    List<AvailabilityResponse> getProviderAvailability(
            UUID providerId, LocalDate startDate, LocalDate endDate, String timezone);
    
    /**
     * Search for available slots based on various criteria
     */
    SlotSearchResponse searchAvailableSlots(SlotSearchRequest request);
    
    /**
     * Generate appointment slots from provider availability
     */
    List<ProviderAvailability> generateAppointmentSlots(ProviderAvailability availability);
    
    /**
     * Check for overlapping availability slots
     */
    boolean hasOverlappingSlots(UUID providerId, LocalDate date,
                                LocalTime startTime, LocalTime endTime,
                                UUID excludeAvailabilityId);
    
    /**
     * Get a single availability by ID
     */
    AvailabilityResponse getAvailabilityById(UUID availabilityId);
    
    /**
     * Get paginated list of availabilities for a provider
     */
    Page<AvailabilityResponse> getProviderAvailabilities(
            UUID providerId, Pageable pageable);
    
    /**
     * Update the status of an availability slot
     */
    AvailabilityResponse updateAvailabilityStatus(
            UUID availabilityId, String status, String reason);
}
