package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.dto.AppointmentBookingRequest;
import com.healthfirst.healthfirstserver.domain.dto.AppointmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    
    /**
     * Book a new appointment
     */
    AppointmentResponse bookAppointment(AppointmentBookingRequest request);
    
    /**
     * Get appointment by ID
     */
    AppointmentResponse getAppointment(String appointmentId);
    
    /**
     * Get all appointments for a patient
     */
    List<AppointmentResponse> getPatientAppointments(String patientId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get all appointments for a provider
     */
    Page<AppointmentResponse> getProviderAppointments(String providerId, LocalDate date, Pageable pageable);
    
    /**
     * Cancel an appointment
     */
    AppointmentResponse cancelAppointment(String appointmentId, String reason);
    
    /**
     * Reschedule an appointment
     */
    AppointmentResponse rescheduleAppointment(String appointmentId, String newSlotId, String reason);
    
    /**
     * Update appointment status
     */
    AppointmentResponse updateAppointmentStatus(String appointmentId, String status, String notes);
    
    /**
     * Send appointment reminder
     */
    void sendAppointmentReminder(String appointmentId);
    
    /**
     * Check-in for an appointment
     */
    AppointmentResponse checkIn(String appointmentId);
    
    /**
     * Start an appointment (for providers)
     */
    AppointmentResponse startAppointment(String appointmentId);
    
    /**
     * Complete an appointment
     */
    AppointmentResponse completeAppointment(String appointmentId, String notes);
    
    /**
     * Get appointment by booking reference
     */
    AppointmentResponse getAppointmentByReference(String bookingReference);
}
