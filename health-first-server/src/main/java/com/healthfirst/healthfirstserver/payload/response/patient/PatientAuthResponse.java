package com.healthfirst.healthfirstserver.payload.response.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PatientAuthResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private UUID id;
    private String email;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean phoneVerified;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tokenExpiry;
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public boolean isPhoneVerified() {
        return phoneVerified;
    }
    
    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }
    
    public LocalDateTime getTokenExpiry() {
        return tokenExpiry;
    }
    
    public void setTokenExpiry(LocalDateTime tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }
    
    @JsonProperty("token_type")
    public String getTokenType() {
        return type;
    }
    
    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final PatientAuthResponse response = new PatientAuthResponse();
        
        public Builder token(String token) {
            response.token = token;
            return this;
        }
        
        public Builder type(String type) {
            response.type = type;
            return this;
        }
        
        public Builder refreshToken(String refreshToken) {
            response.refreshToken = refreshToken;
            return this;
        }
        
        public Builder id(UUID id) {
            response.id = id;
            return this;
        }
        
        public Builder email(String email) {
            response.email = email;
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            response.phoneNumber = phoneNumber;
            return this;
        }
        
        public Builder emailVerified(boolean emailVerified) {
            response.emailVerified = emailVerified;
            return this;
        }
        
        public Builder phoneVerified(boolean phoneVerified) {
            response.phoneVerified = phoneVerified;
            return this;
        }
        
        public Builder tokenExpiry(LocalDateTime tokenExpiry) {
            response.tokenExpiry = tokenExpiry;
            return this;
        }
        
        public PatientAuthResponse build() {
            return response;
        }
    }
}
