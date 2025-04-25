package com.feastora.food_ordering.event;

import com.feastora.food_ordering.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationControllerEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    public RegistrationControllerEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }

}
