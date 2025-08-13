package com.healthfirst.healthfirstserver.domain.enums;

/**
 * Represents the status of an appointment in the system.
 */
public enum AppointmentStatus {
    /**
     * Appointment has been requested but not yet confirmed.
     */
    REQUESTED,
    
    /**
     * Appointment has been confirmed by the provider.
     */
    CONFIRMED,
    
    /**
     * Appointment has been booked but not yet confirmed.
     */
    BOOKED,
    
    /**
     * Appointment has been rescheduled.
     */
    RESCHEDULED,
    
    /**
     * Appointment has been cancelled.
     */
    CANCELLED,
    
    /**
     * Patient has checked in for the appointment.
     */
    CHECKED_IN,
    
    /**
     * Appointment is currently in progress.
     */
    IN_PROGRESS,
    
    /**
     * Appointment has been completed.
     */
    COMPLETED,
    
    /**
     * Patient did not show up for the appointment.
     */
    NO_SHOW
}
