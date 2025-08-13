package com.healthfirst.healthfirstserver;

import com.healthfirst.healthfirstserver.repository.AppointmentRepository;
import com.healthfirst.healthfirstserver.security.JwtTokenProvider;
import com.healthfirst.healthfirstserver.service.NotificationService;
import com.healthfirst.healthfirstserver.service.ProviderAvailabilityService;
import com.healthfirst.healthfirstserver.service.TimeZoneService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mockito.Mockito.mock;

/**
 * Test configuration for unit tests.
 * Provides mock beans for testing purposes.
 */
@TestConfiguration
public class TestConfig {
    
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }
    
    @Bean
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }
    
    @Bean
    public SpringTemplateEngine templateEngine() {
        return mock(SpringTemplateEngine.class);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    public AppointmentRepository appointmentRepository() {
        return mock(AppointmentRepository.class);
    }
    
    @Bean
    public NotificationService notificationService() {
        return mock(NotificationService.class);
    }
    
    @Bean
    public ProviderAvailabilityService providerAvailabilityService() {
        return mock(ProviderAvailabilityService.class);
    }
    
    @Bean
    public TimeZoneService timeZoneService() {
        return mock(TimeZoneService.class);
    }
}
