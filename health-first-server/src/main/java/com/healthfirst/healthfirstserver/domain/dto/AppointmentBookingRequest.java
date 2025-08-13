package com.healthfirst.healthfirstserver.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class AppointmentBookingRequest {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Provider ID is required")
    private String providerId;
    
    @NotBlank(message = "Slot ID is required")
    private String slotId;
    
    @NotBlank(message = "Appointment type is required")
    private String appointmentType;
    
    private String reason;
    
    private List<@NotBlank(message = "Symptoms cannot contain blank values") String> symptoms;
    
    private String notes;
    
    // For rescheduling
    private String originalAppointmentId;
    
    // For telemedicine appointments
    private String meetingPreference; // VIDEO or PHONE
    private String contactNumber;
    
    // Insurance information if different from patient's default
    private InsuranceInfo insuranceInfo;
    
    @Data
    public static class InsuranceInfo {
        private String provider;
        private String memberId;
        private String groupNumber;
        private String policyHolderName;
        private String relationshipToPatient;
    }
}
