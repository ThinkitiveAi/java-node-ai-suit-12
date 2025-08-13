package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.LocationType;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
