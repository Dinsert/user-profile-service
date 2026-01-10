package com.example.user.profile.service.kafka.handler;

import com.example.userprofile.api.dto.UserProfileEvent;
import com.example.userprofile.api.dto.UserProfileEventType;

public interface UserProfileEventHandler {

    UserProfileEventType getType();

    void handle(UserProfileEvent event);
}