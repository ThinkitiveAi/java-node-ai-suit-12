package com.healthfirst.healthfirstserver.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ClinicAddress {
    
    // Explicit getters for fields that are causing compilation errors
    public String getStreet() {
        return street;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getState() {
        return state;
    }
    
    public String getZip() {
        return zip;
    }
    
    @NotBlank
    @Size(max = 200)
    @Column(name = "clinic_street", nullable = false, length = 200)
    private String street;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "clinic_city", nullable = false, length = 100)
    private String city;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "clinic_state", nullable = false, length = 50)
    private String state;
    
    @NotBlank
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$") // US ZIP code format
    @Column(name = "clinic_zip", nullable = false, length = 10)
    private String zip;
}
