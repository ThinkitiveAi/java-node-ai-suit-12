package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotSearchResponse {
    
    private SearchCriteria searchCriteria;
    private int totalResults;
    private List<ProviderSlotAvailability> results;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteria {
        private LocalDate date;
        private LocalDate startDate;
        private LocalDate endDate;
        private String specialization;
        private String location;
        private String appointmentType;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderSlotAvailability {
        private ProviderInfo provider;
        private List<AvailableSlot> availableSlots;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private UUID id;
        private String name;
        private String specialization;
        private Integer yearsOfExperience;
        private Double rating;
        private String clinicAddress;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSlot {
        private UUID slotId;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String timezone;
        private String appointmentType;
        private LocationInfo location;
        private PricingInfo pricing;
        private List<String> specialRequirements;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private String type;
        private String address;
        private String roomNumber;
        private String meetingUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingInfo {
        private double baseFee;
        private boolean insuranceAccepted;
        private String currency;
        private String insuranceProvider;
        private Double copayAmount;
        private Integer coveragePercentage;
    }
}
