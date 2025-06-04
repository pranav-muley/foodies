package com.feastora.food_ordering.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String userName, String token, String applicationUrl) {
        String url = applicationUrl + "/app/verifyRegistration?token=" + token;
        String subject = "Verify your email for QR Scanner Food System";
        String message = "Welcome, " + userName + " to QR Scanner Food System!\n\n"
                + "Click on the URL below to verify yourself:\n"
                + url;
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailMessage.setFrom("muleypranav649@gmail.com");
            mailSender.send(mailMessage);
        }catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("Registered verification token: " + token);
        log.info("Registered url: " + url);
    }
}