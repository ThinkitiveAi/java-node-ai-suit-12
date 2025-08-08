package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.entity.ClinicAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProviderRegistrationRequest {
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot be longer than 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot be longer than 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
    private String phoneNumber;
    
    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization cannot be longer than 100 characters")
    private String specialization;
    
    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number cannot be longer than 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "License number can only contain alphanumeric characters")
    private String licenseNumber;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 80, message = "Years of experience seems too high")
    private Integer yearsOfExperience;
    
    @Valid
    @NotNull(message = "Clinic address is required")
    private ClinicAddress clinicAddress;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    // Getter for password to match the error message
    public String getPassword() {
        return password;
    }
    
    // Getter for confirmPassword to match the error message
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    // Getter for email to match the error message
    public String getEmail() {
        return email;
    }
    
    // Getter for phoneNumber to match the error message
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    // Getter for licenseNumber to match the error message
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    // Getter for clinicAddress to match the error message
    public ClinicAddress getClinicAddress() {
        return clinicAddress;
    }
    
    // Getter for yearsOfExperience to match the error message
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
}
