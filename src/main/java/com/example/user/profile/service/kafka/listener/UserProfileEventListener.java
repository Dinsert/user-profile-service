package com.example.user.profile.service.kafka.listener;

import com.example.user.profile.service.kafka.dispatcher.UserProfileEventDispatcher;
import com.example.userprofile.api.dto.UserProfileEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProfileEventListener {

    private final ObjectMapper objectMapper;
    private final UserProfileEventDispatcher dispatcher;

    @KafkaListener(topics = "user-profile-events")
    public void listen(String message,
                       Acknowledgment ack) throws Exception {
        log.info("Received event: {}", message);

        UserProfileEvent event =
                objectMapper.readValue(message, UserProfileEvent.class);

        dispatcher.dispatch(event);

        ack.acknowledge();
    }
}