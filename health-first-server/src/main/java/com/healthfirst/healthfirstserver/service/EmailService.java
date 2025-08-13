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
import java.util.Map;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String appBaseUrl;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, 
                       TemplateEngine templateEngine,
                       @Value("${app.base-url:http://localhost:8080}") String appBaseUrl,
                       @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.appBaseUrl = appBaseUrl;
        this.fromEmail = fromEmail;
    }

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
    
    /**
     * Sends a templated email using Thymeleaf templates
     * 
     * @param fromEmail The sender's email address
     * @param toEmail The recipient's email address
     * @param subject The email subject
     * @param templateName The name of the Thymeleaf template (without .html extension)
     * @param templateVars A map of template variables to be used in the template
     */
    @Async
    public void sendTemplatedEmail(String fromEmail, String toEmail, String subject, 
                                 String templateName, Map<String, Object> templateVars) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            log.error("Cannot send email: recipient email is null or empty");
            return;
        }
        
        try {
            // Prepare the evaluation context
            final Context ctx = new Context(Locale.getDefault());
            
            // Add all template variables to the context
            if (templateVars != null) {
                for (Map.Entry<String, Object> entry : templateVars.entrySet()) {
                    ctx.setVariable(entry.getKey(), entry.getValue());
                }
            }
            
            // Ensure baseUrl is available in all templates
            if (!ctx.containsVariable("baseUrl")) {
                ctx.setVariable("baseUrl", appBaseUrl != null ? appBaseUrl : "http://localhost:8080");
            }
            
            // Create HTML body using Thymeleaf
            String htmlContent = templateEngine.process(templateName, ctx);
            
            // Prepare email
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            message.setSubject(subject);
            message.setFrom(fromEmail != null ? fromEmail : this.fromEmail);
            message.setTo(toEmail);
            message.setText(htmlContent, true); // true = isHtml
            
            // Send email
            mailSender.send(mimeMessage);
            log.info("Email sent to: {} with template: {}", toEmail, templateName);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", toEmail, e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}", toEmail, e);
        }
    }
}
