package com.feastora.food_ordering.event;

import com.feastora.food_ordering.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationControllerEvent extends ApplicationEvent {

    private User user;
    private Long tableNumber;
    private String applicationUrl;

    public RegistrationControllerEvent(User user, long tableNumber,String applicationUrl) {
        super(user);
        this.user = user;
        this.tableNumber = tableNumber;
        this.applicationUrl = applicationUrl;
    }

}
