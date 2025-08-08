package com.healthfirst.healthfirstserver.payload.request.patient;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmergencyContactRequest {
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phone;

    @Size(max = 50, message = "Relationship must be less than 50 characters")
    private String relationship;
}
