package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import com.healthfirst.healthfirstserver.domain.enums.RecurrencePattern;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AvailabilityRequest {
    
    @NotBlank(message = "Provider ID is required")
    private String providerId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    @NotBlank(message = "Timezone is required")
    private String timezone;
    
    private boolean isRecurring = false;
    
    private RecurrencePattern recurrencePattern;
    
    private LocalDate recurrenceEndDate;
    
    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 240, message = "Slot duration cannot exceed 240 minutes")
    private int slotDurationMinutes = 30;
    
    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 60, message = "Break duration cannot exceed 60 minutes")
    private int breakDurationMinutes = 0;
    
    @NotNull(message = "Appointment type is required")
    private AppointmentType appointmentType;
    
    @Valid
    private LocationDto location;
    
    private PricingDto pricing;
    
    private List<@NotBlank(message = "Special requirement cannot be blank") String> specialRequirements;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    @Data
    public static class LocationDto {
        @NotNull(message = "Location type is required")
        private String type;
        
        @Size(max = 500, message = "Address cannot exceed 500 characters")
        private String address;
        
        @Size(max = 50, message = "Room number cannot exceed 50 characters")
        private String roomNumber;
    }
    
    @Data
    public static class PricingDto {
        @DecimalMin(value = "0.0", message = "Base fee cannot be negative")
        private double baseFee;
        
        private boolean insuranceAccepted = false;
        
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
        private String currency = "USD";
    }
}
