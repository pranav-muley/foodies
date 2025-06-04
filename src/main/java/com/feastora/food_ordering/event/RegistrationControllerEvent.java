package com.feastora.food_ordering.event;

import com.feastora.food_ordering.entity.User;
import com.feastora.food_ordering.model.UserModel;
import lombok.*;
import org.springframework.context.ApplicationEvent;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RegistrationControllerEvent extends ApplicationEvent {

    private final String token;
    private final String userName;
    private final String email;
    private final String applicationUrl;

    public RegistrationControllerEvent(String token, String userName, String email, String applicationUrl) {
        super(token);
        this.token = token;
        this.userName = userName;
        this.email = email;
        this.applicationUrl = applicationUrl;
    }

}
