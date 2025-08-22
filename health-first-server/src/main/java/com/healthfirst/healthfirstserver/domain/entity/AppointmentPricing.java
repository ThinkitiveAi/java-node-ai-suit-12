package com.healthfirst.healthfirstserver.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
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
    
    // Getter and Setter methods
    public BigDecimal getBaseFee() {
        return baseFee;
    }
    
    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }
    
    public boolean isInsuranceAccepted() {
        return insuranceAccepted;
    }
    
    public void setInsuranceAccepted(boolean insuranceAccepted) {
        this.insuranceAccepted = insuranceAccepted;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getInsuranceProvider() {
        return insuranceProvider;
    }
    
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }
    
    public BigDecimal getCopayAmount() {
        return copayAmount;
    }
    
    public void setCopayAmount(BigDecimal copayAmount) {
        this.copayAmount = copayAmount;
    }
    
    public Integer getCoveragePercentage() {
        return coveragePercentage;
    }
    
    public void setCoveragePercentage(Integer coveragePercentage) {
        this.coveragePercentage = coveragePercentage;
    }
    
    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private BigDecimal baseFee;
        private boolean insuranceAccepted;
        private String currency = "USD";
        private String insuranceProvider;
        private BigDecimal copayAmount;
        private Integer coveragePercentage;
        
        public Builder baseFee(BigDecimal baseFee) {
            this.baseFee = baseFee;
            return this;
        }
        
        public Builder insuranceAccepted(boolean insuranceAccepted) {
            this.insuranceAccepted = insuranceAccepted;
            return this;
        }
        
        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }
        
        public Builder insuranceProvider(String insuranceProvider) {
            this.insuranceProvider = insuranceProvider;
            return this;
        }
        
        public Builder copayAmount(BigDecimal copayAmount) {
            this.copayAmount = copayAmount;
            return this;
        }
        
        public Builder coveragePercentage(Integer coveragePercentage) {
            this.coveragePercentage = coveragePercentage;
            return this;
        }
        
        public AppointmentPricing build() {
            AppointmentPricing pricing = new AppointmentPricing();
            pricing.setBaseFee(this.baseFee);
            pricing.setInsuranceAccepted(this.insuranceAccepted);
            pricing.setCurrency(this.currency);
            pricing.setInsuranceProvider(this.insuranceProvider);
            pricing.setCopayAmount(this.copayAmount);
            pricing.setCoveragePercentage(this.coveragePercentage);
            return pricing;
        }
    }
}
