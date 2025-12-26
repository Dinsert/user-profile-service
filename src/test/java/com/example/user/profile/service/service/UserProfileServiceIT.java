package com.example.user.profile.service.service;

import com.example.user.profile.service.UserProfileServiceApplication;
import com.example.user.profile.service.config.TestCacheConfig;
import com.example.user.profile.service.exception.UserProfileNotFoundException;
import com.example.user.profile.service.repository.UserProfileRepository;
import com.example.user.profile.service.util.UserProfileFixture;
import com.example.userprofile.api.dto.UserProfileDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {UserProfileServiceApplication.class, TestCacheConfig.class})
class UserProfileServiceIT extends BaseIntegrationTest {

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    CacheManager cacheManager;

    static final String CACHE = "user_profiles";

    UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        userProfileRepository.deleteAll();
        cacheManager.getCache(CACHE).clear();
    }

    @Test
    void createAndGetUserProfile_shouldCacheResult() {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();

        userProfileService.createUserProfile(userId, dto);

        UserProfileDto response = userProfileService.getUserProfile(userId);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(dto);
        assertThat(cacheManager.getCache(CACHE).get(userId)).isNotNull();
        assertThat(cacheManager.getCache(CACHE).get(userId, UserProfileDto.class)).isEqualTo(response);
    }

    @Test
    void updateUserProfile_shouldEvictCache() {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();
        userProfileService.createUserProfile(userId, dto);
        userProfileService.getUserProfile(userId);
        UserProfileDto dtoForUpdate = UserProfileFixture.createDtoForUpdateEntity();

        userProfileService.updateUserProfile(userId, dtoForUpdate);

        assertThat(cacheManager.getCache(CACHE).get(userId)).isNull();
    }

    @Test
    void getUserProfile_shouldThrowUserProfileNotFoundException() {
        UUID notExistingId = UUID.randomUUID();

        assertThatThrownBy(() -> userProfileService.getUserProfile(notExistingId))
                .isInstanceOf(UserProfileNotFoundException.class)
                .hasMessageContaining("User profile not found:")
                .hasMessageContaining(notExistingId.toString());
    }
}