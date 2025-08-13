package com.healthfirst.healthfirstserver.service.impl;

import com.healthfirst.healthfirstserver.domain.dto.AppointmentBookingRequest;
import com.healthfirst.healthfirstserver.domain.dto.AppointmentResponse;
import com.healthfirst.healthfirstserver.domain.entity.*;
import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.repository.AppointmentRepository;
import com.healthfirst.healthfirstserver.repository.AppointmentSlotRepository;
import com.healthfirst.healthfirstserver.repository.PatientRepository;
import com.healthfirst.healthfirstserver.repository.ProviderRepository;
import com.healthfirst.healthfirstserver.service.AppointmentService;
import com.healthfirst.healthfirstserver.service.NotificationService;
import com.healthfirst.healthfirstserver.service.ProviderAvailabilityService;
import com.healthfirst.healthfirstserver.service.TimeZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final ProviderRepository providerRepository;
    private final PatientRepository patientRepository;
    private final ProviderAvailabilityService availabilityService;
    private final NotificationService notificationService;
    private final TimeZoneService timeZoneService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentBookingRequest request) {
        // 1. Validate and get the slot
        UUID slotId = UUID.fromString(request.getSlotId());
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found"));
        
        if (!slot.isAvailable()) {
            throw new IllegalStateException("The selected slot is no longer available");
        }
        
        // 2. Get patient and provider
        UUID patientId = UUID.fromString(request.getPatientId());
        UUID providerId = UUID.fromString(request.getProviderId());
        
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        
        // 3. Create the appointment
        Appointment appointment = new Appointment();
        appointment.setBookingReference(generateBookingReference());
        appointment.setPatient(patient);
        appointment.setProvider(provider);
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setStartTime(slot.getSlotStartTime());
        appointment.setEndTime(slot.getSlotEndTime());
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setSymptoms(request.getSymptoms());
        
        // 4. Update slot status
        slot.book(patient, appointment.getBookingReference());
        appointmentSlotRepository.save(slot);
        
        // 5. Save the appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // 6. Send confirmation
        notificationService.sendAppointmentConfirmation(savedAppointment);
        
        return convertToDto(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(String appointmentId) {
        UUID id = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        return convertToDto(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getPatientAppointments(String patientId, LocalDate startDate, LocalDate endDate) {
        UUID id = UUID.fromString(patientId);
        ZonedDateTime start = startDate != null ? startDate.atStartOfDay(timeZoneService.getSystemZoneId()) : null;
        ZonedDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay(timeZoneService.getSystemZoneId()) : null;
        
        List<Appointment> appointments;
        if (start != null && end != null) {
            appointments = appointmentRepository.findByPatientIdAndStartTimeBetween(id, start, end);
        } else {
            appointments = appointmentRepository.findByPatientId(id);
        }
        
        return appointments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getProviderAppointments(String providerId, LocalDate date, Pageable pageable) {
        UUID id = UUID.fromString(providerId);
        ZonedDateTime startOfDay = date != null ? date.atStartOfDay(timeZoneService.getSystemZoneId()) : null;
        ZonedDateTime endOfDay = date != null ? date.plusDays(1).atStartOfDay(timeZoneService.getSystemZoneId()) : null;
        
        Page<Appointment> appointments;
        if (startOfDay != null && endOfDay != null) {
            appointments = appointmentRepository.findByProviderIdAndStartTimeBetween(id, startOfDay, endOfDay, pageable);
        } else {
            appointments = appointmentRepository.findByProviderId(id, pageable);
        }
        
        return appointments.map(this::convertToDto);
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(String appointmentId, String reason) {
        UUID id = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
                
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment is already cancelled");
        }
        
        // Update appointment status and cancellation reason
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(ZonedDateTime.now());
        
        // Mark the slot as available again
        if (appointment.getAppointmentSlot() != null) {
            appointmentSlotRepository.findById(appointment.getAppointmentSlot().getId())
                    .ifPresent(slot -> slot.setAvailable(true));
        }
        
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        
        // Send cancellation notification
        notificationService.sendAppointmentCancellation(cancelledAppointment);
        
        return convertToDto(cancelledAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse rescheduleAppointment(String appointmentId, String newSlotId, String reason) {
        UUID id = UUID.fromString(appointmentId);
        UUID slotId = UUID.fromString(newSlotId);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
                
        AppointmentSlot newSlot = appointmentSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("New appointment slot not found"));
                
        if (!newSlot.isAvailable()) {
            throw new IllegalStateException("The selected time slot is no longer available");
        }
        
        // Mark old slot as available
        AppointmentSlot oldSlot = appointment.getAppointmentSlot();
        if (oldSlot != null) {
            oldSlot.setAvailable(true);
            appointmentSlotRepository.save(oldSlot);
        }
        
        // Update appointment with new slot
        appointment.setAppointmentSlot(newSlot);
        appointment.setStartTime(newSlot.getStartTime());
        appointment.setEndTime(newSlot.getEndTime());
        appointment.setRescheduleReason(reason);
        appointment.setRescheduledAt(ZonedDateTime.now());
        
        // Mark new slot as booked
        newSlot.setAvailable(false);
        appointmentSlotRepository.save(newSlot);
        
        Appointment rescheduledAppointment = appointmentRepository.save(appointment);
        
        // Send reschedule notification
        notificationService.sendAppointmentRescheduled(rescheduledAppointment);
        
        return convertToDto(rescheduledAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(String appointmentId, String status, String notes) {
        UUID id = UUID.fromString(appointmentId);
        AppointmentStatus newStatus = AppointmentStatus.valueOf(status.toUpperCase());
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
                
        // Validate status transition
        if (!isValidStatusTransition(appointment.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + 
                    appointment.getStatus() + " to " + newStatus);
        }
        
        // Update status and notes
        appointment.setStatus(newStatus);
        
        // Set appropriate timestamps
        switch (newStatus) {
            case CHECKED_IN:
                appointment.setCheckedInAt();
                break;
            case IN_PROGRESS:
                appointment.setStartedAt();
                break;
            case COMPLETED:
                appointment.setCompletedAt();
                break;
            case NO_SHOW:
                // No specific timestamp for NO_SHOW as we don't have a setter for it
                break;
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Send status update notification if needed
        if (newStatus == AppointmentStatus.CONFIRMED || 
            newStatus == AppointmentStatus.COMPLETED ||
            newStatus == AppointmentStatus.NO_SHOW) {
            notificationService.sendAppointmentStatusUpdate(updatedAppointment);
        }
        
        return convertToDto(updatedAppointment);
    }

    @Override
    public void sendAppointmentReminder(String appointmentId) {
        UUID id = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if (appointment.getStatus() != AppointmentStatus.BOOKED && 
            appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot send reminder for an appointment with status: " + appointment.getStatus());
        }
        
        notificationService.sendAppointmentReminder(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse checkIn(String appointmentId) {
        UUID id = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if (appointment.getStatus() != AppointmentStatus.BOOKED && 
            appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot check in for an appointment with status: " + appointment.getStatus());
        }
        
        appointment.setCheckedInAt();
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        
        Appointment checkedInAppointment = appointmentRepository.save(appointment);
        notificationService.sendAppointmentCheckedIn(checkedInAppointment);
        
        return convertToDto(checkedInAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse startAppointment(String appointmentId) {
        UUID id = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if (appointment.getStatus() != AppointmentStatus.CHECKED_IN) {
            throw new IllegalStateException("Appointment must be checked in before starting");
        }
        
        appointment.setStartedAt();
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        
        Appointment startedAppointment = appointmentRepository.save(appointment);
        notificationService.sendAppointmentStarted(startedAppointment);
        
        return convertToDto(startedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(String appointmentId, String notes) {
        UUID id = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete an appointment that is not in progress");
        }
        
        appointment.setCompletedAt();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        
        if (notes != null) {
            appointment.setNotes(notes);
        }
        
        Appointment completedAppointment = appointmentRepository.save(appointment);
        notificationService.sendAppointmentCompleted(completedAppointment);
        
        return convertToDto(completedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentByReference(String bookingReference) {
        Appointment appointment = appointmentRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with reference: " + bookingReference));
        return convertToDto(appointment);
    }

    // Helper methods
    private String generateBookingReference() {
        return "APT" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private AppointmentResponse convertToDto(Appointment appointment) {
        return modelMapper.map(appointment, AppointmentResponse.class);
    }
}
