package com.allanweber.candidatescareer.authentication.user.registration.event;

import com.allanweber.candidatescareer.authentication.user.dto.UserDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegistrationEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6315888887126291018L;
    private final UserDto user;

    public UserRegistrationEvent(UserDto user) {
        super(user);
        this.user = user;
    }
}
