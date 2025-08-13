package com.healthfirst.healthfirstserver.service.impl;

import com.healthfirst.healthfirstserver.domain.dto.AvailabilityRequest;
import com.healthfirst.healthfirstserver.domain.dto.AvailabilityResponse;
import com.healthfirst.healthfirstserver.domain.dto.SlotSearchRequest;
import com.healthfirst.healthfirstserver.domain.dto.SlotSearchResponse;
import com.healthfirst.healthfirstserver.domain.entity.AppointmentLocation;
import com.healthfirst.healthfirstserver.domain.entity.AppointmentPricing;
import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.entity.ProviderAvailability;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import com.healthfirst.healthfirstserver.domain.enums.LocationType;
import com.healthfirst.healthfirstserver.domain.enums.RecurrencePattern;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.repository.ProviderAvailabilityRepository;
import com.healthfirst.healthfirstserver.repository.ProviderRepository;
import com.healthfirst.healthfirstserver.service.ProviderAvailabilityService;
import com.healthfirst.healthfirstserver.service.TimeZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderAvailabilityServiceImpl implements ProviderAvailabilityService {

    private final ProviderAvailabilityRepository availabilityRepository;
    private final ProviderRepository providerRepository;
    private final ModelMapper modelMapper;
    private final TimeZoneService timeZoneService;

    @Override
    @Transactional
    public List<AvailabilityResponse> createAvailability(AvailabilityRequest request) {
        UUID providerId = UUID.fromString(request.getProviderId());
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + providerId));

        // Check for overlapping slots
        if (hasOverlappingSlots(providerId, request.getDate(), request.getStartTime(), 
                request.getEndTime(), null)) {
            throw new IllegalArgumentException("The requested time slot overlaps with an existing availability");
        }

        List<ProviderAvailability> availabilities = new ArrayList<>();
        
        if (request.isRecurring() && request.getRecurrencePattern() != null 
                && request.getRecurrenceEndDate() != null) {
            // Handle recurring availability
            availabilities = generateRecurringAvailabilities(provider, request);
        } else {
            // Single availability
            ProviderAvailability availability = createSingleAvailability(provider, request);
            availabilities.add(availability);
        }

        // Save all availabilities
        List<ProviderAvailability> savedAvailabilities = availabilityRepository.saveAll(availabilities);
        
        // Generate appointment slots for each availability
        List<ProviderAvailability> withSlots = new ArrayList<>();
        for (ProviderAvailability availability : savedAvailabilities) {
            withSlots.addAll(generateAppointmentSlots(availability));
        }
        
        return withSlots.stream()
                .map(AvailabilityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AvailabilityResponse updateAvailability(UUID availabilityId, AvailabilityRequest request) {
        ProviderAvailability existing = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));

        // Check for overlapping slots excluding the current one
        if (hasOverlappingSlots(existing.getProvider().getId(), request.getDate(), 
                request.getStartTime(), request.getEndTime(), availabilityId)) {
            throw new IllegalArgumentException("The requested time slot overlaps with an existing availability");
        }

        // Update fields
        updateAvailabilityFromRequest(existing, request);
        
        // Save the updated availability
        ProviderAvailability updated = availabilityRepository.save(existing);
        
        // Regenerate appointment slots
        List<ProviderAvailability> updatedWithSlots = generateAppointmentSlots(updated);
        
        return AvailabilityResponse.fromEntity(updatedWithSlots.get(0));
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID availabilityId, String reason) {
        ProviderAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));
        
        // Check if there are any booked appointments
        if (availability.getCurrentAppointments() > 0) {
            throw new IllegalStateException("Cannot delete availability with existing appointments");
        }
        
        // Delete associated appointment slots
        // (Assuming you have a method to find and delete slots by availability ID)
        // appointmentSlotRepository.deleteByAvailabilityId(availabilityId);
        
        // Delete the availability
        availabilityRepository.delete(availability);
        
        log.info("Deleted availability {} for provider {}: {}", 
                availabilityId, availability.getProvider().getId(), reason);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getProviderAvailability(UUID providerId, LocalDate startDate, 
                                                           LocalDate endDate, String timezone) {
        // Convert dates to provider's timezone if needed
        ZoneId zoneId = timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
        
        List<ProviderAvailability> availabilities = availabilityRepository
                .findByProviderIdAndDateBetween(providerId, startDate, endDate);
        
        return availabilities.stream()
                .map(availability -> {
                    // Convert times to the requested timezone
                    // (Implementation depends on your timezone handling strategy)
                    return AvailabilityResponse.fromEntity(availability);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SlotSearchResponse searchAvailableSlots(SlotSearchRequest request) {
        // Validate and normalize the request
        request.validateDateRange();
        
        // Implementation depends on your search criteria and data model
        // This is a simplified example
        List<SlotSearchResponse.ProviderSlotAvailability> results = new ArrayList<>();
        
        // 1. Find providers matching the criteria (specialization, location, etc.)
        // List<Provider> providers = findMatchingProviders(request);
        
        // 2. For each provider, find available slots
        /*
        for (Provider provider : providers) {
            List<ProviderAvailability> availabilities = availabilityRepository
                .findAvailableSlotsInRange(
                    provider.getId(), 
                    request.getStartDate(), 
                    request.getEndDate()
                );
                
            // Convert to DTOs and add to results
            // ...
        }
        */
        
        return SlotSearchResponse.builder()
                .searchCriteria(createSearchCriteria(request))
                .totalResults(results.size())
                .results(results)
                .build();
    }

    @Override
    @Transactional
    public List<ProviderAvailability> generateAppointmentSlots(ProviderAvailability availability) {
        List<ProviderAvailability> slots = new ArrayList<>();
        LocalTime currentTime = availability.getStartTime();
        
        while (currentTime.plusMinutes(availability.getSlotDurationMinutes())
                .isBefore(availability.getEndTime().plusSeconds(1))) {
            
            ProviderAvailability slot = new ProviderAvailability();
            modelMapper.map(availability, slot);
            
            slot.setId(null);
            slot.setStartTime(currentTime);
            slot.setEndTime(currentTime.plusMinutes(availability.getSlotDurationMinutes()));
            slot.setStatus(AvailabilityStatus.AVAILABLE);
            
            slots.add(slot);
            
            // Move to next slot, adding break time if needed
            currentTime = currentTime.plusMinutes(
                    availability.getSlotDurationMinutes() + availability.getBreakDurationMinutes());
        }
        
        return availabilityRepository.saveAll(slots);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOverlappingSlots(UUID providerId, LocalDate date, LocalTime startTime, 
                                     LocalTime endTime, UUID excludeAvailabilityId) {
        List<ProviderAvailability> overlapping = availabilityRepository.findOverlappingSlots(
                providerId, date, startTime, endTime);
        
        if (excludeAvailabilityId != null) {
            overlapping = overlapping.stream()
                    .filter(av -> !av.getId().equals(excludeAvailabilityId))
                    .collect(Collectors.toList());
        }
        
        return !overlapping.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailabilityById(UUID availabilityId) {
        ProviderAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));
        
        return AvailabilityResponse.fromEntity(availability);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AvailabilityResponse> getProviderAvailabilities(UUID providerId, Pageable pageable) {
        return availabilityRepository.findByProviderId(providerId, pageable)
                .map(AvailabilityResponse::fromEntity);
    }

    @Override
    @Transactional
    public AvailabilityResponse updateAvailabilityStatus(UUID availabilityId, String status, String reason) {
        ProviderAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));
        
        AvailabilityStatus newStatus = AvailabilityStatus.valueOf(status.toUpperCase());
        availability.setStatus(newStatus);
        
        // Add audit log or history entry
        log.info("Updated status of availability {} to {}: {}", availabilityId, status, reason);
        
        return AvailabilityResponse.fromEntity(availabilityRepository.save(availability));
    }

    // Helper methods
    private List<ProviderAvailability> generateRecurringAvailabilities(
            Provider provider, AvailabilityRequest request) {
        
        List<ProviderAvailability> availabilities = new ArrayList<>();
        LocalDate currentDate = request.getDate();
        LocalDate endDate = request.getRecurrenceEndDate();
        
        while (!currentDate.isAfter(endDate)) {
            // Check if current date matches the recurrence pattern
            if (matchesRecurrencePattern(currentDate, request.getDate(), request.getRecurrencePattern())) {
                ProviderAvailability availability = createSingleAvailability(provider, request);
                // No need to set date separately as it's set in createSingleAvailability
                availabilities.add(availability);
            }
            
            // Move to next occurrence based on pattern
            currentDate = getNextOccurrence(currentDate, request.getRecurrencePattern());
        }
        
        return availabilities;
    }
    
    private boolean matchesRecurrencePattern(LocalDate date, LocalDate startDate, RecurrencePattern pattern) {
        if (pattern == null) {
            return date.equals(startDate);
        }
        
        switch (pattern) {
            case DAILY:
                return true;
            case WEEKLY:
                return date.getDayOfWeek() == startDate.getDayOfWeek();
            case MONTHLY:
                return date.getDayOfMonth() == startDate.getDayOfMonth();
            default:
                return false;
        }
    }
    
    private LocalDate getNextOccurrence(LocalDate currentDate, RecurrencePattern pattern) {
        if (pattern == null) {
            return currentDate.plusDays(1);
        }
        
        switch (pattern) {
            case DAILY:
                return currentDate.plusDays(1);
            case WEEKLY:
                return currentDate.plusWeeks(1);
            case MONTHLY:
                return currentDate.plusMonths(1);
            default:
                return currentDate.plusDays(1);
        }
    }
    
    private ProviderAvailability createSingleAvailability(Provider provider, AvailabilityRequest request) {
        // Create a new ProviderAvailability with required fields
        ProviderAvailability availability = new ProviderAvailability();
        availability.setProvider(provider);
        availability.setDate(request.getDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setTimezone(request.getTimezone());
        availability.setRecurring(request.isRecurring());
        availability.setRecurrencePattern(request.getRecurrencePattern());
        availability.setRecurrenceEndDate(request.getRecurrenceEndDate());
        availability.setSlotDurationMinutes(request.getSlotDurationMinutes());
        availability.setBreakDurationMinutes(request.getBreakDurationMinutes());
        availability.setAppointmentType(request.getAppointmentType());
        availability.setNotes(request.getNotes());
        availability.setSpecialRequirements(request.getSpecialRequirements());
        availability.setStatus(AvailabilityStatus.AVAILABLE);
        availability.setCurrentAppointments(0);
        
        // Set location if provided
        if (request.getLocation() != null) {
            AppointmentLocation location = new AppointmentLocation();
            location.setType(LocationType.valueOf(request.getLocation().getType().toUpperCase()));
            location.setAddress(request.getLocation().getAddress());
            location.setRoomNumber(request.getLocation().getRoomNumber());
            availability.setLocation(location);
        }
        
        // Set pricing if provided
        if (request.getPricing() != null) {
            AppointmentPricing pricing = new AppointmentPricing();
            pricing.setBaseFee(BigDecimal.valueOf(request.getPricing().getBaseFee()));
            pricing.setInsuranceAccepted(request.getPricing().isInsuranceAccepted());
            pricing.setCurrency(request.getPricing().getCurrency());
            availability.setPricing(pricing);
        }
        
        return availability;
    }
    
    private void updateAvailabilityFromRequest(ProviderAvailability availability, AvailabilityRequest request) {
        // Update basic fields
        if (request.getDate() != null) {
            availability.setDate(request.getDate());
        }
        if (request.getStartTime() != null) {
            availability.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            availability.setEndTime(request.getEndTime());
        }
        if (request.getTimezone() != null) {
            availability.setTimezone(request.getTimezone());
        }
        
        // Update recurrence fields
        availability.setRecurring(request.isRecurring());
        if (request.getRecurrencePattern() != null) {
            availability.setRecurrencePattern(request.getRecurrencePattern());
        }
        if (request.getRecurrenceEndDate() != null) {
            availability.setRecurrenceEndDate(request.getRecurrenceEndDate());
        }
        
        // Update timing fields
        if (request.getSlotDurationMinutes() > 0) {
            availability.setSlotDurationMinutes(request.getSlotDurationMinutes());
        }
        if (request.getBreakDurationMinutes() >= 0) {
            availability.setBreakDurationMinutes(request.getBreakDurationMinutes());
        }
        
        // Update appointment type
        if (request.getAppointmentType() != null) {
            availability.setAppointmentType(request.getAppointmentType());
        }
        
        // Update location if provided
        if (request.getLocation() != null) {
            AppointmentLocation location = availability.getLocation() != null ? 
                    availability.getLocation() : new AppointmentLocation();
                    
            if (request.getLocation().getType() != null) {
                location.setType(LocationType.valueOf(request.getLocation().getType().toUpperCase()));
            }
            if (request.getLocation().getAddress() != null) {
                location.setAddress(request.getLocation().getAddress());
            }
            if (request.getLocation().getRoomNumber() != null) {
                location.setRoomNumber(request.getLocation().getRoomNumber());
            }
            availability.setLocation(location);
        }
        
        // Update pricing if provided
        if (request.getPricing() != null) {
            AppointmentPricing pricing = availability.getPricing() != null ? 
                    availability.getPricing() : new AppointmentPricing();
                    
            if (request.getPricing().getBaseFee() > 0) {
                pricing.setBaseFee(BigDecimal.valueOf(request.getPricing().getBaseFee()));
            }
            pricing.setInsuranceAccepted(request.getPricing().isInsuranceAccepted());
            if (request.getPricing().getCurrency() != null) {
                pricing.setCurrency(request.getPricing().getCurrency());
            }
            availability.setPricing(pricing);
        }
        
        // Update additional fields
        if (request.getSpecialRequirements() != null) {
            availability.setSpecialRequirements(request.getSpecialRequirements());
        }
        if (request.getNotes() != null) {
            availability.setNotes(request.getNotes());
        }
    }
    
    private SlotSearchResponse.SearchCriteria createSearchCriteria(SlotSearchRequest request) {
        return SlotSearchResponse.SearchCriteria.builder()
                .date(request.getDate())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .specialization(request.getSpecialization())
                .location(request.getLocation())
                .appointmentType(request.getAppointmentType() != null ? 
                        request.getAppointmentType().name() : null)
                .build();
    }
}
