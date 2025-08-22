package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.LocationType;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentLocation {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false, length = 20)
    private LocationType type;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "room_number", length = 50)
    private String roomNumber;
    
    // For telemedicine appointments
    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;
    
    // Getter and Setter methods
    public LocationType getType() {
        return type;
    }
    
    public void setType(LocationType type) {
        this.type = type;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public String getMeetingUrl() {
        return meetingUrl;
    }
    
    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }
    
    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private LocationType type;
        private String address;
        private String roomNumber;
        private String meetingUrl;
        
        public Builder type(LocationType type) {
            this.type = type;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder roomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }
        
        public Builder meetingUrl(String meetingUrl) {
            this.meetingUrl = meetingUrl;
            return this;
        }
        
        public AppointmentLocation build() {
            AppointmentLocation location = new AppointmentLocation();
            location.setType(this.type);
            location.setAddress(this.address);
            location.setRoomNumber(this.roomNumber);
            location.setMeetingUrl(this.meetingUrl);
            return location;
        }
    }
}
