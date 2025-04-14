package com.feastora.food_ordering.event.listener;

import com.feastora.food_ordering.entity.User;
import com.feastora.food_ordering.event.RegistrationControllerEvent;
import com.feastora.food_ordering.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class RegistrationControllerEventListener implements ApplicationListener<RegistrationControllerEvent> {

    private final UserService userService;
    public RegistrationControllerEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(RegistrationControllerEvent event) {
        //create verification Token For user;
        User user = event.getUser();
        long tableNumber = event.getTableNumber();
        String token = UUID.randomUUID().toString();

        userService.saveVerificationTokenForUser(user.getUserId(), tableNumber, token);

        //send mail to user.
        String url = event.getApplicationUrl() +
                "/app/verifyRegistration?token=" + token + "&tableNumber=" + tableNumber;
        log.info("Registered verification token: " + token);
        log.info("Registered url: " + url);

    }
}
