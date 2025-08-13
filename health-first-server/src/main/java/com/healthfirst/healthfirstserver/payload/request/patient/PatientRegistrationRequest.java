package com.healthfirst.healthfirstserver.payload.request.patient;

import com.healthfirst.healthfirstserver.domain.entity.Patient.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {
    // Explicit getters for fields that are causing compilation errors
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email != null ? email.toLowerCase() : null;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public AddressRequest getAddress() {
        return address;
    }
    
    public EmergencyContactRequest getEmergencyContact() {
        return emergencyContact;
    }
    
    public String[] getMedicalHistory() {
        return medicalHistory;
    }
    
    public InsuranceInfoRequest getInsuranceInfo() {
        return insuranceInfo;
    }
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\s-']+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\s-']+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$",
        message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character"
    )
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Valid
    @NotNull(message = "Address is required")
    private AddressRequest address;

    @Valid
    private EmergencyContactRequest emergencyContact;

    private String[] medicalHistory;

    @Valid
    private InsuranceInfoRequest insuranceInfo;

    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    @AssertTrue(message = "You must be at least 13 years old")
    public boolean isAtLeast13YearsOld() {
        if (dateOfBirth == null) {
            return false;
        }
        return dateOfBirth.plusYears(13).isBefore(LocalDate.now().plusDays(1));
    }
}
