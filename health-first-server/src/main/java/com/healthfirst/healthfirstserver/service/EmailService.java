package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.domain.entity.VerificationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${app.base-url}")
    private final String appBaseUrl;
    @Value("${spring.mail.username}")
    private final String fromEmail;

    @Async
    public void sendVerificationEmail(Patient patient, String token) {
        if (patient == null) {
            log.error("Cannot send verification email: patient is null");
            return;
        }
        
        if (token == null || token.trim().isEmpty()) {
            log.error("Cannot send verification email: token is null or empty");
            return;
        }
        
        try {
            String verificationUrl = String.format("%s/api/v1/patient/verify-email?token=%s", 
                appBaseUrl != null ? appBaseUrl : "http://localhost:8080", 
                token);
            
            // Prepare the evaluation context
            final Context ctx = new Context(Locale.getDefault());
            ctx.setVariable("name", patient.getFirstName() != null ? patient.getFirstName() : "User");
            ctx.setVariable("verificationUrl", verificationUrl);
            
            // Create HTML body using Thymeleaf
            String htmlContent = templateEngine.process("email/verification-email", ctx);
            
            // Prepare email
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            message.setSubject("Verify your email address");
            message.setFrom(fromEmail);
            message.setTo(patient.getEmail());
            message.setText(htmlContent, true); // true = isHtml
            
            // Send email
            mailSender.send(mimeMessage);
            log.info("Verification email sent to: {}", patient.getEmail());
            
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", patient.getEmail(), e);
            // In a production environment, you might want to handle this differently,
            // e.g., by retrying or logging to a monitoring system
        } catch (Exception e) {
            log.error("Unexpected error while sending verification email to: {}", 
                patient.getEmail() != null ? patient.getEmail() : "unknown", e);
        }
    }
}
