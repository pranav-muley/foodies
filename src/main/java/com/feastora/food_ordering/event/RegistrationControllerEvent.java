package com.feastora.food_ordering.event;

import com.feastora.food_ordering.entity.User;
import com.feastora.food_ordering.model.UserModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationControllerEvent extends ApplicationEvent {

    private final String token;
    private final String applicationUrl;

    public RegistrationControllerEvent(String token, String applicationUrl) {
        super(token);
        this.token = token;
        this.applicationUrl = applicationUrl;
    }

}
