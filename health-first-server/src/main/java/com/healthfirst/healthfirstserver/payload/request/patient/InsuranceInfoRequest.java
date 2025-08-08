package com.healthfirst.healthfirstserver.payload.request.patient;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InsuranceInfoRequest {
    @Size(max = 100, message = "Provider must be less than 100 characters")
    private String provider;
    
    @Size(max = 50, message = "Policy number must be less than 50 characters")
    private String policyNumber;
}
