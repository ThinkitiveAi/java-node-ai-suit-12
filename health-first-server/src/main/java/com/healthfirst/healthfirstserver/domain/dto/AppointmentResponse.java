package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    
    private String id;
    private String bookingReference;
    private ProviderInfo provider;
    private PatientInfo patient;
    private AppointmentDetails appointment;
    private LocationInfo location;
    private BillingInfo billing;
    private String status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private String id;
        private String name;
        private String specialization;
        private String email;
        private String phoneNumber;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private String id;
        private String name;
        private String email;
        private String phoneNumber;
        private String dateOfBirth;
        private String gender;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentDetails {
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        private String timezone;
        private AppointmentType type;
        private String reason;
        private List<String> symptoms;
        private String notes;
        private Integer durationMinutes;
        private String cancellationReason;
        private ZonedDateTime cancelledAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private String type;
        private String name;
        private String address;
        private String roomNumber;
        private String meetingUrl;
        private String instructions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingInfo {
        private Double baseFee;
        private String currency;
        private Boolean insuranceAccepted;
        private String insuranceProvider;
        private String memberId;
        private Double copayAmount;
        private Integer coveragePercentage;
        private String billingStatus;
    }
    
    // Add a method to convert from entity if needed
    public static AppointmentResponse fromEntity(com.healthfirst.healthfirstserver.domain.entity.Appointment appointment) {
        // Implementation would convert your entity to DTO
        return AppointmentResponse.builder()
                .id(appointment.getId().toString())
                .bookingReference(appointment.getBookingReference())
                // Map other fields as needed
                .build();
    }
}
