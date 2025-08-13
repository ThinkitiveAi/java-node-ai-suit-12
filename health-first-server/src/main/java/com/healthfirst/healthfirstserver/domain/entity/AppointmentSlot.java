package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false, foreignKey = @ForeignKey(name = "fk_slot_availability"))
    private ProviderAvailability availability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false, foreignKey = @ForeignKey(name = "fk_slot_provider"))
    private Provider provider;

    @Column(name = "slot_start_time", nullable = false)
    private ZonedDateTime slotStartTime;

    @Column(name = "slot_end_time", nullable = false)
    private ZonedDateTime slotEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_slot_patient"))
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 20)
    private AppointmentType appointmentType;

    @Column(name = "booking_reference", unique = true, length = 50)
    private String bookingReference;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // Helper methods
    public boolean isBooked() {
        return status == AvailabilityStatus.BOOKED;
    }

    public boolean isAvailable() {
        return status == AvailabilityStatus.AVAILABLE;
    }

    public boolean isCancelled() {
        return status == AvailabilityStatus.CANCELLED;
    }

    public void book(Patient patient, String bookingReference) {
        if (!isAvailable()) {
            throw new IllegalStateException("Cannot book an appointment slot that is not available");
        }
        this.patient = patient;
        this.bookingReference = bookingReference;
        this.status = AvailabilityStatus.BOOKED;
        this.availability.incrementAppointmentCount();
    }

    public void cancel(String reason) {
        if (isBooked()) {
            this.status = AvailabilityStatus.CANCELLED;
            this.cancellationReason = reason;
            this.availability.decrementAppointmentCount();
        } else if (isAvailable()) {
            this.status = AvailabilityStatus.CANCELLED;
            this.cancellationReason = reason;
        }
    }

    public void reschedule(ZonedDateTime newStartTime, ZonedDateTime newEndTime) {
        if (isCancelled()) {
            throw new IllegalStateException("Cannot reschedule a cancelled appointment");
        }
        this.slotStartTime = newStartTime;
        this.slotEndTime = newEndTime;
    }
}
