package com.healthfirst.healthfirstserver.controller;

import com.healthfirst.healthfirstserver.domain.dto.AppointmentBookingRequest;
import com.healthfirst.healthfirstserver.domain.dto.AppointmentResponse;
import com.healthfirst.healthfirstserver.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "APIs for managing patient appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(
        summary = "Book a new appointment",
        description = "Book a new appointment with a healthcare provider"
    )
    @ApiResponse(responseCode = "201", description = "Appointment booked successfully")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentBookingRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{appointmentId}")
    @Operation(
        summary = "Get appointment by ID",
        description = "Retrieve details of a specific appointment"
    )
    @ApiResponse(responseCode = "200", description = "Appointment found")
    public ResponseEntity<AppointmentResponse> getAppointment(
            @Parameter(description = "ID of the appointment to retrieve")
            @PathVariable String appointmentId) {
        return ResponseEntity.ok(appointmentService.getAppointment(appointmentId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient's appointments",
        description = "Retrieve a list of appointments for a specific patient"
    )
    @ApiResponse(responseCode = "200", description = "List of appointments")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(
            @Parameter(description = "ID of the patient")
            @PathVariable String patientId,
            
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(appointmentService.getPatientAppointments(
                patientId, startDate, endDate));
    }

    @GetMapping("/provider/{providerId}")
    @Operation(
        summary = "Get provider's appointments",
        description = "Retrieve a paginated list of appointments for a specific provider"
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of appointments")
    public ResponseEntity<Page<AppointmentResponse>> getProviderAppointments(
            @Parameter(description = "ID of the provider")
            @PathVariable String providerId,
            
            @Parameter(description = "Date to filter appointments (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            
            Pageable pageable) {
        
        return ResponseEntity.ok(appointmentService.getProviderAppointments(
                providerId, date, pageable));
    }

    @PostMapping("/{appointmentId}/cancel")
    @Operation(
        summary = "Cancel an appointment",
        description = "Cancel an existing appointment"
    )
    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @Parameter(description = "ID of the appointment to cancel")
            @PathVariable String appointmentId,
            
            @Parameter(description = "Reason for cancellation")
            @RequestParam String reason) {
        
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId, reason));
    }

    @PostMapping("/{appointmentId}/reschedule")
    @Operation(
        summary = "Reschedule an appointment",
        description = "Reschedule an existing appointment to a new time slot"
    )
    @ApiResponse(responseCode = "200", description = "Appointment rescheduled successfully")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @Parameter(description = "ID of the appointment to reschedule")
            @PathVariable String appointmentId,
            
            @Parameter(description = "ID of the new time slot")
            @RequestParam String newSlotId,
            
            @Parameter(description = "Reason for rescheduling")
            @RequestParam(required = false) String reason) {
        
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(
                appointmentId, newSlotId, reason));
    }

    @PostMapping("/{appointmentId}/status")
    @Operation(
        summary = "Update appointment status",
        description = "Update the status of an appointment (e.g., CONFIRMED, COMPLETED, NOSHOW)"
    )
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @Parameter(description = "ID of the appointment")
            @PathVariable String appointmentId,
            
            @Parameter(description = "New status (e.g., CONFIRMED, COMPLETED, NOSHOW)")
            @RequestParam String status,
            
            @Parameter(description = "Additional notes about the status change")
            @RequestParam(required = false) String notes) {
        
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(
                appointmentId, status, notes));
    }

    @PostMapping("/{appointmentId}/reminder")
    @Operation(
        summary = "Send appointment reminder",
        description = "Send a reminder for an upcoming appointment"
    )
    @ApiResponse(responseCode = "200", description = "Reminder sent successfully")
    public ResponseEntity<Void> sendReminder(
            @Parameter(description = "ID of the appointment")
            @PathVariable String appointmentId) {
        
        appointmentService.sendAppointmentReminder(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{appointmentId}/check-in")
    @Operation(
        summary = "Check in for an appointment",
        description = "Check in for a scheduled appointment"
    )
    @ApiResponse(responseCode = "200", description = "Check-in successful")
    public ResponseEntity<AppointmentResponse> checkIn(
            @Parameter(description = "ID of the appointment")
            @PathVariable String appointmentId) {
        
        return ResponseEntity.ok(appointmentService.checkIn(appointmentId));
    }

    @PostMapping("/{appointmentId}/start")
    @Operation(
        summary = "Start an appointment",
        description = "Mark an appointment as in progress (for providers)"
    )
    @ApiResponse(responseCode = "200", description = "Appointment started")
    public ResponseEntity<AppointmentResponse> startAppointment(
            @Parameter(description = "ID of the appointment")
            @PathVariable String appointmentId) {
        
        return ResponseEntity.ok(appointmentService.startAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/complete")
    @Operation(
        summary = "Complete an appointment",
        description = "Mark an in-progress appointment as completed"
    )
    @ApiResponse(responseCode = "200", description = "Appointment completed")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @Parameter(description = "ID of the appointment")
            @PathVariable String appointmentId,
            
            @Parameter(description = "Notes about the completed appointment")
            @RequestParam(required = false) String notes) {
        
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId, notes));
    }

    @GetMapping("/reference/{bookingReference}")
    @Operation(
        summary = "Get appointment by booking reference",
        description = "Retrieve appointment details using the booking reference number"
    )
    @ApiResponse(responseCode = "200", description = "Appointment found")
    public ResponseEntity<AppointmentResponse> getAppointmentByReference(
            @Parameter(description = "Booking reference number")
            @PathVariable String bookingReference) {
        
        return ResponseEntity.ok(appointmentService.getAppointmentByReference(bookingReference));
    }
}
