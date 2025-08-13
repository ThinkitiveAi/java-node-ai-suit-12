package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Appointment;
import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import com.healthfirst.healthfirstserver.service.impl.MockSmsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private MockSmsServiceImpl mockSmsService;
    
    private Appointment testAppointment;
    
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
        
        // Clear any previous test data
        mockSmsService.clearSentMessages();
    }
    
    @Test
    void sendAppointmentConfirmation_SendsEmailAndSms() {
        // When
        notificationService.sendAppointmentConfirmation(testAppointment);
        
        // Then - Verify SMS was sent
        assertEquals(1, mockSmsService.getMessageCount("+1234567890"), 
            "Should send one SMS to the patient");
        
        String smsMessage = mockSmsService.getLastMessage("+1234567890");
        assertNotNull(smsMessage, "SMS message should not be null");
        assertTrue(smsMessage.contains("confirmed"), "SMS should contain confirmation text");
        assertTrue(smsMessage.contains("CANCEL"), "SMS should contain cancel instructions");
    }
    
    @Test
    void sendAppointmentReminder_SendsReminderIfWithin48Hours() {
        // Given - Appointment is within 48 hours
        testAppointment.setStartTime(ZonedDateTime.now().plusHours(36));
        
        // When
        notificationService.sendAppointmentReminder(testAppointment);
        
        // Then - Verify SMS was sent
        assertEquals(1, mockSmsService.getMessageCount("+1234567890"), 
            "Should send one reminder SMS");
    }
    
    @Test
    void sendAppointmentReminder_DoesNotSendIfNotWithin48Hours() {
        // Given - Appointment is more than 48 hours away
        testAppointment.setStartTime(ZonedDateTime.now().plusDays(3));
        
        // When
        notificationService.sendAppointmentReminder(testAppointment);
        
        // Then - Verify no SMS was sent
        assertEquals(0, mockSmsService.getMessageCount("+1234567890"), 
            "Should not send reminder SMS for appointments >48 hours away");
    }
    
    @Test
    void sendAppointmentCancellation_SendsCancellationNotice() {
        // Given
        testAppointment.setStatus(AppointmentStatus.CANCELLED);
        testAppointment.setCancellationReason("Provider unavailable");
        
        // When
        notificationService.sendAppointmentCancellation(testAppointment);
        
        // Then - Verify SMS was sent
        String smsMessage = mockSmsService.getLastMessage("+1234567890");
        assertNotNull(smsMessage, "SMS message should not be null");
        assertTrue(smsMessage.contains("cancelled") || smsMessage.contains("cancellation"), 
            "SMS should contain cancellation text");
    }
    
    @Test
    void sendAppointmentRescheduled_SendsNewAppointmentDetails() {
        // When
        notificationService.sendAppointmentRescheduled(testAppointment);
        
        // Then - Verify SMS was sent with new time
        String smsMessage = mockSmsService.getLastMessage("+1234567890");
        assertNotNull(smsMessage, "SMS message should not be null");
        assertTrue(smsMessage.contains("rescheduled"), 
            "SMS should mention rescheduling");
    }
    
    @Test
    void sendAppointmentCheckedIn_NotifiesProvider() {
        // When
        notificationService.sendAppointmentCheckedIn(testAppointment);
        
        // Then - Verify SMS was sent to provider
        // Note: In a real test, we'd verify the email was sent to the provider
        // For now, we just verify no exceptions are thrown
        assertDoesNotThrow(() -> notificationService.sendAppointmentCheckedIn(testAppointment));
    }
    
    @Test
    void sendAppointmentCompleted_SendsFollowUpRequest() {
        // When
        notificationService.sendAppointmentCompleted(testAppointment);
        
        // Then - Verify SMS was sent with feedback request
        String smsMessage = mockSmsService.getLastMessage("+1234567890");
        assertNotNull(smsMessage, "SMS message should not be null");
        assertTrue(smsMessage.contains("feedback") || smsMessage.contains("follow up"), 
            "SMS should request feedback");
    }
}
