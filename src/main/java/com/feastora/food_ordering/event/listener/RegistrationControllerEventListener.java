package com.feastora.food_ordering.event.listener;

import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.event.RegistrationControllerEvent;
import com.feastora.food_ordering.model.UserModel;
import com.feastora.food_ordering.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegistrationControllerEventListener implements ApplicationListener<RegistrationControllerEvent> {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public RegistrationControllerEventListener(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onApplicationEvent(RegistrationControllerEvent event) {
        //create verification Token For user;
        String token = event.getToken();
        Claims claim = jwtUtil.validateToken(token);
        userService.saveUserEntity(jwtUtil.getUserModelFromToken(token));
        String userId = claim.get("userId", String.class);
        userService.saveVerificationTokenForUser(userId, token);

        //send mail to user.
        String url = event.getApplicationUrl() +
                "/app/verifyRegistration?token=" + token;

        log.info("Registered verification token: " + token);
        log.info("Registered url: " + url);
    }
}
