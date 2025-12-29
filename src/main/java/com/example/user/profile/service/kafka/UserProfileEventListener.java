package com.example.user.profile.service.kafka;

import com.example.user.profile.service.service.UserProfileService;
import com.example.userprofile.api.dto.UserProfileCreatedEvent;
import com.example.userprofile.api.dto.UserProfileDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProfileEventListener {

    private final UserProfileService userProfileService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-profile-events")
    public void handle(String message) throws Exception {
        log.info("Was invoke method handle with message:{}", message);
        UserProfileCreatedEvent event =
                objectMapper.readValue(message, UserProfileCreatedEvent.class);

        userProfileService.createUserProfile(event.getUserId(), new UserProfileDto(event.getLoyaltyLevel(), event.getExternalBalance()));
    }
}