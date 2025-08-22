package com.healthfirst.healthfirstserver.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

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
        
        public String getProvider() {
            return provider;
        }
        
        public void setProvider(String provider) {
            this.provider = provider;
        }
        
        public String getMemberId() {
            return memberId;
        }
        
        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }
        
        public String getGroupNumber() {
            return groupNumber;
        }
        
        public void setGroupNumber(String groupNumber) {
            this.groupNumber = groupNumber;
        }
        
        public String getPolicyHolderName() {
            return policyHolderName;
        }
        
        public void setPolicyHolderName(String policyHolderName) {
            this.policyHolderName = policyHolderName;
        }
        
        public String getRelationshipToPatient() {
            return relationshipToPatient;
        }
        
        public void setRelationshipToPatient(String relationshipToPatient) {
            this.relationshipToPatient = relationshipToPatient;
        }
    }
    
    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    
    public String getSlotId() {
        return slotId;
    }
    
    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
    
    public String getAppointmentType() {
        return appointmentType;
    }
    
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public List<String> getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getOriginalAppointmentId() {
        return originalAppointmentId;
    }
    
    public void setOriginalAppointmentId(String originalAppointmentId) {
        this.originalAppointmentId = originalAppointmentId;
    }
    
    public String getMeetingPreference() {
        return meetingPreference;
    }
    
    public void setMeetingPreference(String meetingPreference) {
        this.meetingPreference = meetingPreference;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public InsuranceInfo getInsuranceInfo() {
        return insuranceInfo;
    }
    
    public void setInsuranceInfo(InsuranceInfo insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String patientId;
        private String providerId;
        private String slotId;
        private String appointmentType;
        private String reason;
        private List<String> symptoms;
        private String notes;
        private String originalAppointmentId;
        private String meetingPreference;
        private String contactNumber;
        private InsuranceInfo insuranceInfo;
        
        public Builder patientId(String patientId) {
            this.patientId = patientId;
            return this;
        }
        
        public Builder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }
        
        public Builder slotId(String slotId) {
            this.slotId = slotId;
            return this;
        }
        
        public Builder appointmentType(String appointmentType) {
            this.appointmentType = appointmentType;
            return this;
        }
        
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }
        
        public Builder symptoms(List<String> symptoms) {
            this.symptoms = symptoms;
            return this;
        }
        
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }
        
        public Builder originalAppointmentId(String originalAppointmentId) {
            this.originalAppointmentId = originalAppointmentId;
            return this;
        }
        
        public Builder meetingPreference(String meetingPreference) {
            this.meetingPreference = meetingPreference;
            return this;
        }
        
        public Builder contactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }
        
        public Builder insuranceInfo(InsuranceInfo insuranceInfo) {
            this.insuranceInfo = insuranceInfo;
            return this;
        }
        
        public AppointmentBookingRequest build() {
            AppointmentBookingRequest request = new AppointmentBookingRequest();
            request.setPatientId(this.patientId);
            request.setProviderId(this.providerId);
            request.setSlotId(this.slotId);
            request.setAppointmentType(this.appointmentType);
            request.setReason(this.reason);
            request.setSymptoms(this.symptoms);
            request.setNotes(this.notes);
            request.setOriginalAppointmentId(this.originalAppointmentId);
            request.setMeetingPreference(this.meetingPreference);
            request.setContactNumber(this.contactNumber);
            request.setInsuranceInfo(this.insuranceInfo);
            return request;
        }
    }
}
