package com.example.user.profile.service.kafka.handler;

import com.example.user.profile.service.repository.InboxEventRepository;
import com.example.user.profile.service.service.UserProfileService;
import com.example.userprofile.api.dto.UserProfileEvent;
import com.example.userprofile.api.dto.UserProfileEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserProfileUpdatedHandler implements UserProfileEventHandler {

    private final UserProfileService userProfileService;
    private final InboxEventRepository inboxEventRepository;

    @Override
    public UserProfileEventType getType() {
        return UserProfileEventType.UPDATED;
    }

    @Transactional
    @Override
    public void handle(UserProfileEvent event) {
        int inserted = inboxEventRepository.insertIgnore(event.getEventId(), Instant.now());
        if (inserted == 0) {
            return;
        }

        userProfileService.updateUserProfile(event.getUserId(), event.getPayload());
    }
}
