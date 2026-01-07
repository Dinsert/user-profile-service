package com.example.user.profile.service.kafka.listener;

import com.example.user.profile.service.kafka.dispatcher.UserProfileEventDispatcher;
import com.example.userprofile.api.dto.UserProfileEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProfileEventListener {

    public static final String USER_PROFILE_EVENTS = "user-profile-events";

    private final UserProfileEventDispatcher dispatcher;

    @KafkaListener(topics = USER_PROFILE_EVENTS)
    public void listen(UserProfileEvent event) {
        log.info("Received event: {}", event);
        dispatcher.dispatch(event);
    }
}