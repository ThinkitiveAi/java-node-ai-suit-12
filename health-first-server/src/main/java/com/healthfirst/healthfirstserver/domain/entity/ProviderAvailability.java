package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import com.healthfirst.healthfirstserver.domain.enums.RecurrencePattern;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "provider_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderAvailability extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false, foreignKey = @ForeignKey(name = "fk_availability_provider"))
    private Provider provider;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_pattern", length = 20)
    private RecurrencePattern recurrencePattern;

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes = 30;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;

    @Column(name = "max_appointments_per_slot", nullable = false)
    private Integer maxAppointmentsPerSlot = 1;

    @Column(name = "current_appointments", nullable = false)
    private Integer currentAppointments = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 20)
    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @Embedded
    private AppointmentLocation location;

    @Embedded
    private AppointmentPricing pricing;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "special_requirements")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> specialRequirements;

    // Helper methods
    public boolean isAvailable() {
        return status == AvailabilityStatus.AVAILABLE && 
               (maxAppointmentsPerSlot == null || currentAppointments < maxAppointmentsPerSlot);
    }

    public boolean overlapsWith(LocalTime otherStart, LocalTime otherEnd) {
        return !(endTime.isBefore(otherStart) || startTime.isAfter(otherEnd));
    }

    public void incrementAppointmentCount() {
        if (currentAppointments < maxAppointmentsPerSlot) {
            currentAppointments++;
            if (currentAppointments >= maxAppointmentsPerSlot) {
                status = AvailabilityStatus.BOOKED;
            }
        }
    }

    public void decrementAppointmentCount() {
        if (currentAppointments > 0) {
            currentAppointments--;
            if (status == AvailabilityStatus.BOOKED && currentAppointments < maxAppointmentsPerSlot) {
                status = AvailabilityStatus.AVAILABLE;
            }
        }
    }
}
