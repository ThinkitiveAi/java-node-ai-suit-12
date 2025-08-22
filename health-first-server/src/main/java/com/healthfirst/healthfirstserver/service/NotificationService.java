package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Appointment;

/**
 * Service for sending various types of notifications related to appointments.
 */
public interface NotificationService {
    
    /**
     * Send a confirmation when an appointment is booked.
     * @param appointment The appointment that was booked
     */
    void sendAppointmentConfirmation(Appointment appointment);
    
    /**
     * Send a notification when an appointment is cancelled.
     * @param appointment The appointment that was cancelled
     */
    void sendAppointmentCancellation(Appointment appointment);
    
    /**
     * Send a notification when an appointment is rescheduled.
     * @param appointment The appointment that was rescheduled
     */
    void sendAppointmentRescheduled(Appointment appointment);
    
    /**
     * Send a reminder for an upcoming appointment.
     * @param appointment The appointment to remind about
     */
    void sendAppointmentReminder(Appointment appointment);
    
    /**
     * Send a notification when an appointment status is updated.
     * @param appointment The appointment with updated status
     */
    void sendAppointmentStatusUpdate(Appointment appointment);
    
    /**
     * Send a notification when a patient checks in for an appointment.
     * @param appointment The appointment that was checked in
     */
    void sendAppointmentCheckedIn(Appointment appointment);
    
    /**
     * Send a notification when an appointment is completed.
     * @param appointment The appointment that was completed
     */
    void sendAppointmentCompleted(Appointment appointment);
    
    /**
     * Send a notification when an appointment is started.
     * @param appointment The appointment that was started
     */
    void sendAppointmentStarted(Appointment appointment);
    
    /**
     * Send a notification when an appointment starts.
     * @param appointment The appointment that has started
     */
    /**
     * Send a notification when a new availability slot is created.
     * @param providerName The name of the provider
     * @param providerEmail The email of the provider
     * @param startTime The start time of the availability
     * @param endTime The end time of the availability
     */
    void sendNewAvailabilityNotification(String providerName, String providerEmail, 
                                       String startTime, String endTime);
    
    /**
     * Send a notification when an availability is updated.
     * @param providerName The name of the provider
     * @param providerEmail The email of the provider
     * @param originalTime The original time slot
     * @param newTime The new time slot
     * @param reason The reason for the update
     */
    void sendAvailabilityUpdatedNotification(String providerName, String providerEmail, 
                                           String originalTime, String newTime, String reason);
    
    /**
     * Send a notification when an availability is cancelled.
     * @param providerName The name of the provider
     * @param providerEmail The email of the provider
     * @param timeSlot The time slot that was cancelled
     * @param reason The reason for cancellation
     */
    void sendAvailabilityCancelledNotification(String providerName, String providerEmail, 
                                             String timeSlot, String reason);
}
