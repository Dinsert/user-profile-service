package com.example.user.profile.service.kafka.listener;

import com.example.user.profile.service.kafka.dispatcher.UserProfileEventDispatcher;
import com.example.userprofile.api.dto.UserProfileEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProfileEventListener {

    private final UserProfileEventDispatcher dispatcher;

    @KafkaListener(topics = "${app.kafka.topics.user-profile-events}")
    public void listen(UserProfileEvent event, Acknowledgment ack) {
        log.info("Received event: {}", event);
        dispatcher.dispatch(event);
        ack.acknowledge();
    }
}