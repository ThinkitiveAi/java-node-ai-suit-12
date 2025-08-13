package com.healthfirst.healthfirstserver.domain.entity;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

import java.time.ZonedDateTime;

/**
 * Represents a medical appointment between a patient and a healthcare provider.
 */
@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
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
    
    // createdAt and updatedAt are inherited from BaseEntity
    
    @Column(name = "checked_in_time")
    private ZonedDateTime checkedInTime;
    
    @Column(name = "started_time")
    private ZonedDateTime startedTime;
    
    @Column(name = "completed_time")
    private ZonedDateTime completedTime;
    
    @Column(name = "cancelled_time")
    private ZonedDateTime cancelledTime;
    
    /**
     * Sets the cancellation timestamp to the current time.
     * This is a convenience method that sets the cancelledTime field to now.
     */
    public void setCancelledAt() {
        this.cancelledTime = ZonedDateTime.now();
    }
    
    /**
     * Sets the rescheduled timestamp to the current time.
     * This is a convenience method that updates the updatedAt field to now.
     */
    public void setRescheduledAt() {
        this.updatedAt = ZonedDateTime.now();
    }
    
    /**
     * Sets the check-in timestamp to the current time.
     * This is a convenience method that sets the checkedInTime field to now.
     */
    public void setCheckedInAt() {
        this.checkedInTime = ZonedDateTime.now();
    }
    
    /**
     * Sets the started timestamp to the current time.
     * This is a convenience method that sets the startedTime field to now.
     */
    public void setStartedAt() {
        this.startedTime = ZonedDateTime.now();
    }
    
    /**
     * Sets the completion timestamp to the current time.
     * This is a convenience method that sets the completedTime field to now.
     */
    public void setCompletedAt() {
        this.completedTime = ZonedDateTime.now();
    }
    
    @Column(name = "rescheduled_from_id")
    private Long rescheduledFromId;
    
    @Column(name = "rescheduled_to_id")
    private Long rescheduledToId;
    
    /**
     * Sets the rescheduled from appointment ID.
     *
     * @param appointmentId the ID of the appointment this one was rescheduled from
     */
    public void setRescheduledFromId(Long appointmentId) {
        this.rescheduledFromId = appointmentId;
    }
    
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
    
    @Embedded
    private AppointmentLocation location;
    
    @Column(name = "previous_appointment_date")
    private ZonedDateTime previousAppointmentDate;
    
    @Column(name = "previous_appointment_time")
    private ZonedDateTime previousAppointmentTime;
    
    @Column(name = "reschedule_reason", length = 1000)
    private String rescheduleReason;

    public AppointmentLocation getLocation() {
        return location;
    }
    
    /**
     * Gets the previous appointment date if this appointment was rescheduled
     * @return the previous appointment date or null if not rescheduled
     */
    public ZonedDateTime getPreviousAppointmentDate() {
        return previousAppointmentDate;
    }
    
    /**
     * Gets the previous appointment time if this appointment was rescheduled
     * @return the previous appointment time or null if not rescheduled
     */
    public ZonedDateTime getPreviousAppointmentTime() {
        return previousAppointmentTime;
    }
    
    /**
     * Gets the reason for rescheduling the appointment
     * @return the reschedule reason or null if not rescheduled
     */
    public String getRescheduleReason() {
        return rescheduleReason;
    }
}
