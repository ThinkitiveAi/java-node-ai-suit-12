package com.healthfirst.healthfirstserver.service.impl;

import com.healthfirst.healthfirstserver.domain.entity.Appointment;
import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import com.healthfirst.healthfirstserver.service.EmailService;
import com.healthfirst.healthfirstserver.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the NotificationService that sends email notifications.
 * SMS functionality has been removed and will be implemented in a future update.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy 'at' h:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.email.from}")
    private String fromEmail;

    @Async
    @Override
    public void sendAppointmentConfirmation(Appointment appointment) {
        String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
        String date = appointment.getStartTime().format(DATE_FORMATTER);
        String time = appointment.getStartTime().format(TIME_FORMATTER) + " - " + 
                     appointment.getEndTime().format(TIME_FORMATTER);
        String location = formatLocation(appointment);
        
        // Email content
        String subject = "Appointment Confirmation: " + appointment.getAppointmentType();
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("patientName", patientName);
        emailModel.put("providerName", providerName);
        emailModel.put("appointmentType", appointment.getAppointmentType());
        emailModel.put("date", date);
        emailModel.put("time", time);
        emailModel.put("location", location);
        emailModel.put("reason", appointment.getReason());
        emailModel.put("bookingReference", appointment.getBookingReference());
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                appointment.getPatient().getEmail(),
                subject,
                "emails/appointment-confirmation",
                emailModel
            );
            log.info("Appointment confirmation sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Failed to send appointment confirmation email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendAppointmentCancellation(Appointment appointment) {
        String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
        String date = appointment.getStartTime().format(DATE_FORMATTER);
        String time = appointment.getStartTime().format(TIME_FORMATTER);
        
        // Email content
        String subject = "Appointment Cancelled: " + appointment.getAppointmentType();
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("patientName", patientName);
        emailModel.put("providerName", providerName);
        emailModel.put("appointmentType", appointment.getAppointmentType());
        emailModel.put("date", date);
        emailModel.put("time", time);
        emailModel.put("cancellationReason", appointment.getCancellationReason());
        emailModel.put("bookingReference", appointment.getBookingReference());
        emailModel.put("rescheduleUrl", baseUrl + "/book?provider=" + appointment.getProvider().getId());
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                appointment.getPatient().getEmail(),
                subject,
                "emails/appointment-cancelled",
                emailModel
            );
            log.info("Appointment cancellation notification sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Failed to send appointment cancellation email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendAppointmentRescheduled(Appointment appointment) {
        String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
        String date = appointment.getStartTime().format(DATE_FORMATTER);
        String time = appointment.getStartTime().format(TIME_FORMATTER);
        String oldDate = appointment.getPreviousAppointmentDate() != null ? 
            appointment.getPreviousAppointmentDate().format(DATE_FORMATTER) : "N/A";
        String oldTime = appointment.getPreviousAppointmentTime() != null ? 
            appointment.getPreviousAppointmentTime().format(TIME_FORMATTER) : "N/A";
        
        // Email content
        String subject = "Appointment Rescheduled: " + appointment.getAppointmentType();
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("patientName", patientName);
        emailModel.put("providerName", providerName);
        emailModel.put("appointmentType", appointment.getAppointmentType());
        emailModel.put("oldDate", oldDate);
        emailModel.put("oldTime", oldTime);
        emailModel.put("newDate", date);
        emailModel.put("newTime", time);
        emailModel.put("reason", appointment.getRescheduleReason());
        emailModel.put("bookingReference", appointment.getBookingReference());
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                appointment.getPatient().getEmail(),
                subject,
                "emails/appointment-rescheduled",
                emailModel
            );
            log.info("Appointment rescheduled notification sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Failed to send appointment rescheduled email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendAppointmentReminder(Appointment appointment) {
        try {
            if (appointment.getStartTime().isBefore(appointment.getStartTime().plusHours(48))) {
                String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
                String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
                String date = appointment.getStartTime().format(DATE_FORMATTER);
                String time = appointment.getStartTime().format(TIME_FORMATTER) + " - " + 
                             appointment.getEndTime().format(TIME_FORMATTER);
                
                // Email content
                String subject = "Reminder: Upcoming " + appointment.getAppointmentType() + " Appointment";
                Map<String, Object> emailModel = new HashMap<>();
                emailModel.put("patientName", patientName);
                emailModel.put("providerName", providerName);
                emailModel.put("appointmentType", appointment.getAppointmentType());
                emailModel.put("date", date);
                emailModel.put("time", time);
                emailModel.put("location", formatLocation(appointment));
                emailModel.put("reason", appointment.getReason() != null ? appointment.getReason() : "");
                emailModel.put("bookingReference", appointment.getBookingReference());
                emailModel.put("cancelUrl", baseUrl + "/appointments/" + appointment.getId() + "/cancel");
                emailModel.put("rescheduleUrl", baseUrl + "/appointments/" + appointment.getId() + "/reschedule");
                emailModel.put("baseUrl", baseUrl);
                
                // Send email
                emailService.sendTemplatedEmail(
                    fromEmail,
                    appointment.getPatient().getEmail(),
                    subject,
                    "emails/appointment-reminder",
                    emailModel
                );
                
                log.info("Appointment reminder sent for appointment: {}", appointment.getId());
            }
        } catch (Exception e) {
            log.error("Failed to send appointment reminder email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendAppointmentStatusUpdate(Appointment appointment) {
        String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
        String date = appointment.getStartTime().format(DATE_FORMATTER);
        String time = appointment.getStartTime().format(TIME_FORMATTER);
        
        // Email content
        String subject = "Appointment Status Update: " + appointment.getStatus().toString();
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("patientName", patientName);
        emailModel.put("providerName", providerName);
        emailModel.put("appointmentType", appointment.getAppointmentType());
        emailModel.put("date", date);
        emailModel.put("time", time);
        emailModel.put("status", appointment.getStatus().toString());
        emailModel.put("statusMessage", getStatusMessage(appointment.getStatus()));
        emailModel.put("bookingReference", appointment.getBookingReference());
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                appointment.getPatient().getEmail(),
                subject,
                "emails/appointment-status-update",
                emailModel
            );
            log.info("Appointment status update sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Failed to send appointment status update email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendAppointmentCheckedIn(Appointment appointment) {
        // Notify provider that patient has checked in
        String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
        String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        String time = appointment.getStartTime().format(TIME_FORMATTER);
        
        // Email content
        String subject = "Patient Checked In: " + patientName;
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("providerName", providerName);
        emailModel.put("patientName", patientName);
        emailModel.put("appointmentType", appointment.getAppointmentType());
        emailModel.put("time", time);
        emailModel.put("date", appointment.getStartTime().format(DATE_FORMATTER));
        emailModel.put("notes", appointment.getNotes() != null ? appointment.getNotes() : "");
        emailModel.put("baseUrl", baseUrl);
        
        // Send email to provider
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                appointment.getProvider().getEmail(),
                subject,
                "emails/provider-patient-checked-in",
                emailModel
            );
            log.info("Patient checked in notification sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Failed to send patient checked-in email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendAppointmentCompleted(Appointment appointment) {
        String patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        String providerName = appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName();
        
        // Email content
        String subject = "Appointment Completed: Feedback Requested";
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("patientName", patientName);
        emailModel.put("providerName", providerName);
        emailModel.put("appointmentType", appointment.getAppointmentType());
        emailModel.put("date", appointment.getStartTime().format(DATE_FORMATTER));
        emailModel.put("time", appointment.getStartTime().format(TIME_FORMATTER));
        emailModel.put("feedbackUrl", baseUrl + "/feedback/" + appointment.getBookingReference());
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                appointment.getPatient().getEmail(),
                subject,
                "emails/appointment-completed",
                emailModel
            );
            log.info("Appointment completed notification sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Failed to send appointment completed email for appointment: {}", appointment.getId(), e);
        }
    }

    @Async
    @Override
    public void sendNewAvailabilityNotification(String providerName, String providerEmail, 
                                              String startTime, String endTime) {
        // Email content
        String subject = "New Availability Added";
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("providerName", providerName);
        emailModel.put("startTime", startTime);
        emailModel.put("endTime", endTime);
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                providerEmail,
                subject,
                "emails/provider-availability-added",
                emailModel
            );
            log.info("New availability notification sent to provider: {}", providerEmail);
        } catch (Exception e) {
            log.error("Failed to send new availability notification to provider: {}", providerEmail, e);
        }
    }

    @Async
    @Override
    public void sendAvailabilityUpdatedNotification(String providerName, String providerEmail, 
                                                  String originalTime, String newTime, String reason) {
        // Email content
        String subject = "Availability Updated";
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("providerName", providerName);
        emailModel.put("originalTime", originalTime);
        emailModel.put("newTime", newTime);
        emailModel.put("reason", reason);
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                providerEmail,
                subject,
                "emails/provider-availability-updated",
                emailModel
            );
            log.info("Availability update notification sent to provider: {}", providerEmail);
        } catch (Exception e) {
            log.error("Failed to send availability update notification to provider: {}", providerEmail, e);
        }
    }

    @Async
    @Override
    public void sendAvailabilityCancelledNotification(String providerName, String providerEmail, 
                                                    String timeSlot, String reason) {
        // Email content
        String subject = "Availability Cancelled";
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("providerName", providerName);
        emailModel.put("timeSlot", timeSlot);
        emailModel.put("reason", reason);
        emailModel.put("baseUrl", baseUrl);
        
        // Send email
        try {
            emailService.sendTemplatedEmail(
                fromEmail,
                providerEmail,
                subject,
                "emails/provider-availability-cancelled",
                emailModel
            );
            log.info("Availability cancellation notification sent to provider: {}", providerEmail);
        } catch (Exception e) {
            log.error("Failed to send availability cancellation notification to provider: {}", providerEmail, e);
        }
    }
    
    private String formatLocation(Appointment appointment) {
        // Format location information based on appointment type
        if (appointment.getLocation() != null) {
            com.healthfirst.healthfirstserver.domain.entity.AppointmentLocation location = appointment.getLocation();
            switch (location.getType()) {
                case TELEMEDICINE:
                    return location.getMeetingUrl() != null ? 
                        String.format("Telemedicine - Join at: %s", location.getMeetingUrl()) :
                        "Telemedicine - A link will be provided before your appointment";
                case HOME_VISIT:
                    return String.format("Home Visit - %s", 
                        appointment.getPatient().getAddress() != null ? 
                            appointment.getPatient().getAddress() : "Address not specified");
                case CLINIC:
                case HOSPITAL:
                default:
                    String locationName = location.getRoomNumber() != null ?
                        String.format("%s (Room %s)", location.getAddress(), location.getRoomNumber()) :
                        location.getAddress();
                    return locationName != null ? locationName : "Location details not specified";
            }
        }
        return "Location not specified";
    }
    
    /**
     * Gets a user-friendly status message for the given appointment status
     * @param status The appointment status
     * @return A user-friendly status message
     */
    private String getStatusMessage(AppointmentStatus status) {
        if (status == null) {
            return "Status not available";
        }
        
        switch (status) {
            case REQUESTED:
                return "Your appointment request has been received. We will notify you once it's confirmed.";
            case CONFIRMED:
                return "Your appointment has been confirmed. We look forward to seeing you!";
            case BOOKED:
                return "Your appointment has been booked. You will receive a confirmation email shortly.";
            case RESCHEDULED:
                return "Your appointment has been rescheduled. Please check your email for the new details.";
            case CANCELLED:
                return "Your appointment has been cancelled. Please contact us if you need to reschedule.";
            case CHECKED_IN:
                return "You have checked in for your appointment. The provider will see you shortly.";
            case IN_PROGRESS:
                return "Your appointment is currently in progress.";
            case COMPLETED:
                return "Your appointment has been completed. Thank you for choosing our services!";
            case NO_SHOW:
                return "You were marked as a no-show for this appointment. Please contact us to reschedule.";
            default:
                return "Your appointment status has been updated.";
        }
    }
}
