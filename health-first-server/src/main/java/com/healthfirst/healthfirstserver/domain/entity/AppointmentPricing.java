package com.healthfirst.healthfirstserver.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentPricing {
    
    @Column(name = "base_fee", precision = 10, scale = 2)
    private BigDecimal baseFee;
    
    @Column(name = "insurance_accepted")
    private boolean insuranceAccepted;
    
    @Column(name = "currency", length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    private String currency = "USD";
    
    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;
    
    @Column(name = "copay_amount", precision = 10, scale = 2)
    private BigDecimal copayAmount;
    
    @Column(name = "coverage_percentage")
    private Integer coveragePercentage;
}
