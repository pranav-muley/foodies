package com.feastora.food_ordering.event.listener;

import com.feastora.food_ordering.event.RegistrationControllerEvent;
import com.feastora.food_ordering.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationControllerEventListener implements ApplicationListener<RegistrationControllerEvent> {
    private final EmailService emailService;

    public RegistrationControllerEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(RegistrationControllerEvent event) {
        String token = event.getToken();
        String userName = event.getUserName();
        String email = event.getEmail();
        String applicationUrl = event.getApplicationUrl();

        emailService.sendVerificationEmail(email, userName, token, applicationUrl);
    }
}
