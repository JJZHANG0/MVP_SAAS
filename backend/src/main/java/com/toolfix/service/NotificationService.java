package com.toolfix.service;

import com.toolfix.domain.DiagnosisSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    
    private final JavaMailSender mailSender;
    
    @Value("${toolfix.notification.seller-email}")
    private String sellerEmail;
    
    public void sendTransferNotification(DiagnosisSession session, boolean isHazard) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(sellerEmail);
            message.setFrom("noreply@toolfix.demo");
            
            String subject = isHazard 
                ? "🚨 URGENT: Hazard Detected - Customer Transfer Required"
                : "Customer Transferred to Human Support";
            
            StringBuilder body = new StringBuilder();
            body.append(isHazard ? "⚠️ SAFETY ALERT ⚠️\n\n" : "");
            body.append("A customer has been transferred to human support.\n\n");
            body.append("Session Details:\n");
            body.append("- Session ID: ").append(session.getSessionUuid()).append("\n");
            body.append("- Customer: ").append(session.getCustomerName() != null ? session.getCustomerName() : session.getCustomerEmail()).append("\n");
            body.append("- Product: ").append(session.getProduct().getProductName()).append("\n");
            body.append("- Order: ").append(session.getShopifyOrderId()).append("\n");
            
            if (isHazard) {
                body.append("\n⚠️ HAZARD KEYWORDS DETECTED: ").append(session.getHazardKeywords()).append("\n");
                body.append("\nIMMEDIATE ACTION REQUIRED - This may involve safety risks.\n");
            }
            
            if (session.getDiagnosisSummary() != null) {
                body.append("\nDiagnosis Summary:\n").append(session.getDiagnosisSummary()).append("\n");
            }
            
            if (session.getSuspectedIssue() != null) {
                body.append("\nSuspected Issue: ").append(session.getSuspectedIssue()).append("\n");
            }
            
            body.append("\nView full conversation in admin dashboard:\n");
            body.append("http://localhost:3000/sessions/").append(session.getId()).append("\n");
            
            message.setSubject(subject);
            message.setText(body.toString());
            
            mailSender.send(message);
            log.info("Transfer notification sent for session: {}", session.getSessionUuid());
            
        } catch (Exception e) {
            log.error("Failed to send transfer notification for session: " + session.getSessionUuid(), e);
        }
    }
}
