package com.example.user.profile.service.kafka.dispatcher;

import com.example.user.profile.service.kafka.handler.UserProfileEventHandler;
import com.example.userprofile.api.dto.UserProfileEvent;
import com.example.userprofile.api.dto.UserProfileEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserProfileEventDispatcher {

    private final Map<UserProfileEventType, UserProfileEventHandler> handlers;

    public UserProfileEventDispatcher(List<UserProfileEventHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        UserProfileEventHandler::getType,
                        Function.identity()
                ));
    }

    public void dispatch(UserProfileEvent event) {
        UserProfileEventHandler handler = handlers.get(event.getEventType());

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler found for event type " + event.getEventType()
            );
        }

        handler.handle(event);
    }
}