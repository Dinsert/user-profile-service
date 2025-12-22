package com.example.user.profile.service.controller;

import com.example.user.profile.service.service.UserProfileService;
import com.example.userprofile.api.UserProfileApi;
import com.example.userprofile.api.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class UserProfileController implements UserProfileApi {

    private final UserProfileService userProfileService;

    @Override
    public ResponseEntity<UserProfileDto> getUserProfile(UUID userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @Override
    public ResponseEntity<Void> createUserProfile(UUID userId, UserProfileDto userProfileDto) {
        userProfileService.createUserProfile(userId, userProfileDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateUserProfile(UUID userId, UserProfileDto userProfileDto) {
        userProfileService.updateUserProfile(userId, userProfileDto);
        return ResponseEntity.ok().build();
    }
}
