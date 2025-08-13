package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SlotSearchRequest {
    
    private LocalDate date;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String specialization;
    
    private String location;
    
    private AppointmentType appointmentType;
    
    private Boolean insuranceAccepted;
    
    private Double maxPrice;
    
    private String timezone;
    
    private Boolean availableOnly = true;
    
    private List<@NotNull String> providerIds;
    
    // Helper method to validate date range
    public void validateDateRange() {
        if (date == null && (startDate == null || endDate == null)) {
            throw new IllegalArgumentException("Either date or both startDate and endDate must be provided");
        }
        
        if (date != null) {
            this.startDate = date;
            this.endDate = date;
        } else if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
    }
}
