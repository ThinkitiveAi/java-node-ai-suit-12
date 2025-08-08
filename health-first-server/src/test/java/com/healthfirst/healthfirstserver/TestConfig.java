package com.healthfirst.healthfirstserver;

import com.healthfirst.healthfirstserver.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mockito.Mockito.mock;

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
}
