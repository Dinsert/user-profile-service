package com.example.user.profile.service.service;

import com.example.userprofile.api.dto.UserProfileDto;

import java.util.UUID;

public interface UserProfileService {

    UserProfileDto getUserProfile(UUID userId);

    void createUserProfile(UUID userId, UserProfileDto userProfileDto);

    void updateUserProfile(UUID userId, UserProfileDto userProfileDto);
}
