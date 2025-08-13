package com.healthfirst.healthfirstserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthfirst.healthfirstserver.domain.dto.AppointmentBookingRequest;
import com.healthfirst.healthfirstserver.domain.dto.AppointmentResponse;
import com.healthfirst.healthfirstserver.domain.entity.Appointment;
import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import com.healthfirst.healthfirstserver.service.AppointmentService;
import com.healthfirst.healthfirstserver.service.impl.MockSmsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
@Import(TestConfig.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;
    
    @Autowired
    private MockSmsServiceImpl mockSmsService;

    private Appointment testAppointment;
    private AppointmentResponse testAppointmentResponse;

    @BeforeEach
    void setUp() {
        // Create test data
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhoneNumber("+1234567890");
        
        Provider provider = new Provider();
        provider.setId(1L);
        provider.setFirstName("Jane");
        provider.setLastName("Smith");
        provider.setEmail("jane.smith@healthcare.com");
        
        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setBookingReference("ABC123");
        testAppointment.setPatient(patient);
        testAppointment.setProvider(provider);
        testAppointment.setAppointmentType("GENERAL_CHECKUP");
        testAppointment.setStartTime(ZonedDateTime.now().plusDays(1));
        testAppointment.setEndTime(ZonedDateTime.now().plusDays(1).plusHours(1));
        testAppointment.setStatus(AppointmentStatus.BOOKED);
        testAppointment.setReason("Annual checkup");
        
        testAppointmentResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("BOOKED")
                .build();
        
        // Reset mock
        Mockito.reset(appointmentService);
        mockSmsService.clearSentMessages();
    }

    @Test
    void bookAppointment_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        AppointmentBookingRequest request = new AppointmentBookingRequest();
        request.setPatientId("1");
        request.setProviderId("1");
        request.setSlotId("slot-123");
        request.setAppointmentType("GENERAL_CHECKUP");
        
        when(appointmentService.bookAppointment(any(AppointmentBookingRequest.class)))
                .thenReturn(testAppointmentResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingReference", is("ABC123")))
                .andExpect(jsonPath("$.status", is("BOOKED")));
    }

    @Test
    void getAppointment_ValidId_ReturnsAppointment() throws Exception {
        // Given
        when(appointmentService.getAppointment("1"))
                .thenReturn(testAppointmentResponse);
        
        // When/Then
        mockMvc.perform(get("/api/v1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.bookingReference", is("ABC123")));
    }

    @Test
    void cancelAppointment_ValidRequest_ReturnsOk() throws Exception {
        // Given
        AppointmentResponse cancelledResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("CANCELLED")
                .build();
        
        when(appointmentService.cancelAppointment("1", "Patient request"))
                .thenReturn(cancelledResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments/1/cancel")
                .param("reason", "Patient request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void rescheduleAppointment_ValidRequest_ReturnsOk() throws Exception {
        // Given
        AppointmentResponse rescheduledResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("RESCHEDULED")
                .build();
        
        when(appointmentService.rescheduleAppointment("1", "slot-456", "Better time"))
                .thenReturn(rescheduledResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments/1/reschedule")
                .param("newSlotId", "slot-456")
                .param("reason", "Better time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESCHEDULED")));
    }

    @Test
    void updateAppointmentStatus_ValidRequest_ReturnsOk() throws Exception {
        // Given
        AppointmentResponse updatedResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("COMPLETED")
                .build();
        
        when(appointmentService.updateAppointmentStatus("1", "COMPLETED", "All done"))
                .thenReturn(updatedResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments/1/status")
                .param("status", "COMPLETED")
                .param("notes", "All done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    void checkIn_ValidRequest_ReturnsOk() throws Exception {
        // Given
        AppointmentResponse checkedInResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("CHECKED_IN")
                .build();
        
        when(appointmentService.checkIn("1"))
                .thenReturn(checkedInResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments/1/check-in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CHECKED_IN")));
    }

    @Test
    void startAppointment_ValidRequest_ReturnsOk() throws Exception {
        // Given
        AppointmentResponse inProgressResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("IN_PROGRESS")
                .build();
        
        when(appointmentService.startAppointment("1"))
                .thenReturn(inProgressResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void completeAppointment_ValidRequest_ReturnsOk() throws Exception {
        // Given
        AppointmentResponse completedResponse = AppointmentResponse.builder()
                .id("1")
                .bookingReference("ABC123")
                .status("COMPLETED")
                .build();
        
        when(appointmentService.completeAppointment("1", "All done"))
                .thenReturn(completedResponse);
        
        // When/Then
        mockMvc.perform(post("/api/v1/appointments/1/complete")
                .param("notes", "All done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }
}
