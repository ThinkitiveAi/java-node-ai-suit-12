package com.healthfirst.healthfirstserver.payload.request;

import com.healthfirst.healthfirstserver.domain.entity.ClinicAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProviderRegistrationRequest {
    
    // Explicit getters for all fields to ensure they're accessible
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public ClinicAddress getClinicAddress() {
        return clinicAddress;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
    private String phoneNumber;
    
    @NotBlank(message = "Specialization is required")
    @Size(min = 3, max = 100, message = "Specialization must be between 3 and 100 characters")
    private String specialization;
    
    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "License number must be alphanumeric")
    @Size(max = 50, message = "License number cannot be longer than 50 characters")
    private String licenseNumber;
    
    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;
    
    @Valid
    @NotNull(message = "Clinic address is required")
    private ClinicAddress clinicAddress;
    
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,100}$",
        message = "Password must contain at least 8 characters, including uppercase, lowercase, number and special character"
    )
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
