package com.healthfirst.healthfirstserver.service.impl;

import com.healthfirst.healthfirstserver.service.SmsService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mock implementation of SmsService for testing purposes.
 * This implementation doesn't send real SMS messages but logs them instead.
 */
@Slf4j
public class MockSmsServiceImpl implements SmsService {

    private final Map<String, String> sentMessages = new HashMap<>();
    private final Map<String, SmsDeliveryStatus> messageStatuses = new HashMap<>();

    @Override
    public boolean sendSms(String from, String to, String message) {
        String messageId = "MOCK" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        log.info("Mock SMS sent - From: {}, To: {}, Message: {}", 
                from != null ? from : "[DEFAULT]", to, message);
        
        sentMessages.put(messageId, message);
        messageStatuses.put(messageId, SmsDeliveryStatus.DELIVERED);
        
        return true;
    }

    @Override
    public boolean sendTemplatedSms(String from, String to, String templateId, Map<String, String> templateParams) {
        String message = String.format("Mock templated SMS - Template: %s, Params: %s", 
                templateId, templateParams);
        return sendSms(from, to, message);
    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        // Simple validation - check if it looks like a phone number
        return phoneNumber != null && 
               phoneNumber.matches("^\\+?[0-9]{10,15}$");
    }

    @Override
    public SmsDeliveryStatus getMessageStatus(String messageId) {
        return messageStatuses.getOrDefault(messageId, SmsDeliveryStatus.UNKNOWN);
    }

    // Test helper methods
    
    /**
     * Get the number of messages sent to a specific phone number.
     */
    public int getMessageCount(String to) {
        return (int) sentMessages.entrySet().stream()
                .filter(entry -> entry.getKey().contains(to))
                .count();
    }
    
    /**
     * Clear all sent messages (useful for test cleanup).
     */
    public void clearSentMessages() {
        sentMessages.clear();
        messageStatuses.clear();
    }
    
    /**
     * Get the last message sent to a specific phone number.
     */
    public String getLastMessage(String to) {
        return sentMessages.entrySet().stream()
                .filter(entry -> entry.getKey().contains(to))
                .map(Map.Entry::getValue)
                .reduce((first, second) -> second) // Get last element
                .orElse(null);
    }
}
