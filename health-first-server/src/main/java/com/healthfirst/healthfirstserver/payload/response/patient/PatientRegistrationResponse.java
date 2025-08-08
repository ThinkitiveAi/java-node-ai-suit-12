package com.healthfirst.healthfirstserver.payload.response.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationResponse {
    private boolean success;
    private String message;
    private PatientData data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientData {
        private UUID patientId;
        private String email;
        private String phoneNumber;
        private boolean emailVerified;
        private boolean phoneVerified;
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime registeredAt;
    }
}
