package com.example.user.profile.service.controller;

import com.example.user.profile.service.config.BaseWebMvcTest;
import com.example.user.profile.service.config.TestCacheConfig;
import com.example.user.profile.service.exception.UserProfileNotFoundException;
import com.example.user.profile.service.service.UserProfileService;
import com.example.user.profile.service.util.UserProfileFixture;
import com.example.userprofile.api.dto.UserProfileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestCacheConfig.class)
@WebMvcTest(controllers = UserProfileController.class)
class UserProfileControllerTest extends BaseWebMvcTest {

    @MockitoBean
    UserProfileService userProfileService;

    UUID userId;

    final String uri = "/api/user-profiles/{userId}";

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getUserProfile_shouldReturnProfile() throws Exception {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();

        when(userProfileService.getUserProfile(userId))
                .thenReturn(dto);

        mockMvc.perform(get(uri, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getUserProfile_shouldReturn404() throws Exception {
        when(userProfileService.getUserProfile(userId))
                .thenThrow(new UserProfileNotFoundException("User profile not found:" + userId));

        mockMvc.perform(get(uri, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserProfile_shouldReturn200() throws Exception {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();

        doNothing().when(userProfileService)
                .createUserProfile(eq(userId), any(UserProfileDto.class));

        mockMvc.perform(post(uri, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userProfileService)
                .createUserProfile(eq(userId), any(UserProfileDto.class));
    }

    @Test
    void createUserProfile_invalidBody_shouldReturn400() throws Exception {
        mockMvc.perform(post(uri, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserProfile_shouldReturn200() throws Exception {
        UserProfileDto dto = UserProfileFixture.createDtoForCreateEntity();

        doNothing().when(userProfileService)
                .updateUserProfile(eq(userId), any(UserProfileDto.class));

        mockMvc.perform(put(uri, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userProfileService)
                .updateUserProfile(eq(userId), any(UserProfileDto.class));
    }

    @Test
    void updateUserProfile_invalidBody_shouldReturn400() throws Exception {
        mockMvc.perform(put(uri, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}