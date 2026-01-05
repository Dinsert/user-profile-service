package com.example.user.profile.service.kafka.listener;

import com.example.user.profile.service.kafka.dispatcher.UserProfileEventDispatcher;
import com.example.userprofile.api.dto.UserProfileEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProfileEventListener {

    public static final String USER_PROFILE_EVENTS = "user-profile-events";

    private final ObjectMapper objectMapper;
    private final UserProfileEventDispatcher dispatcher;

    @Transactional
    @KafkaListener(topics = USER_PROFILE_EVENTS)
    public void listen(String message) throws Exception {
        log.info("Received event: {}", message);
        UserProfileEvent event = objectMapper.readValue(message, UserProfileEvent.class);
        dispatcher.dispatch(event);
    }
}