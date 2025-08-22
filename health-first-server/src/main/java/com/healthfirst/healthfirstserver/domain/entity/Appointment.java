package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Represents a medical appointment between a patient and a healthcare provider.
 */
@Entity
@Table(name = "appointments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Appointment extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(name = "appointment_type", nullable = false)
    private String appointmentType;

    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(length = 1000)
    private String reason;

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "is_recurring")
    private boolean isRecurring;

    @Column(name = "recurrence_pattern")
    private String recurrencePattern;

    @ElementCollection
    @CollectionTable(name = "appointment_symptoms", joinColumns = @JoinColumn(name = "appointment_id"))
    @Column(name = "symptom")
    private List<String> symptoms;

    @Column(name = "checked_in_time")
    private ZonedDateTime checkedInTime;

    @Column(name = "started_time")
    private ZonedDateTime startedTime;

    @Column(name = "completed_time")
    private ZonedDateTime completedTime;

    @Column(name = "cancelled_time")
    private ZonedDateTime cancelledTime;

    @Column(name = "rescheduled_time")
    private ZonedDateTime rescheduledTime;

    @Column(name = "rescheduled_from_id")
    private Long rescheduledFromId;

    @Column(name = "rescheduled_to_id")
    private Long rescheduledToId;

    @Column(name = "reschedule_reason", length = 1000)
    private String rescheduleReason;

    @Column(name = "reminder_sent")
    private boolean reminderSent;

    @Column(name = "follow_up_required")
    private boolean followUpRequired;

    @Column(name = "follow_up_notes", length = 2000)
    private String followUpNotes;

    @Column(name = "billing_notes", length = 1000)
    private String billingNotes;

    @Column(name = "external_id")
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_slot_id", foreignKey = @ForeignKey(name = "fk_appointment_slot"))
    private AppointmentSlot appointmentSlot;

    @Embedded
    private AppointmentLocation location;

    @Column(name = "previous_appointment_date")
    private ZonedDateTime previousAppointmentDate;

    @Column(name = "previous_appointment_time")
    private ZonedDateTime previousAppointmentTime;

    // === Convenience methods ===

    public void setCheckedInAt() {
        this.checkedInTime = ZonedDateTime.now();
    }

    public void setStartedAt() {
        this.startedTime = ZonedDateTime.now();
    }

    public void setCompletedAt() {
        this.completedTime = ZonedDateTime.now();
    }

    public void setCancelledAt(ZonedDateTime cancelledTime) {
        this.cancelledTime = cancelledTime != null ? cancelledTime : ZonedDateTime.now();
    }

    public void setRescheduledAt(ZonedDateTime rescheduledTime) {
        this.rescheduledTime = rescheduledTime != null ? rescheduledTime : ZonedDateTime.now();
    }
}
