package com.example.user.profile.service.util;

import com.example.userprofile.api.dto.UserProfileDto;

import java.math.BigDecimal;

public class UserProfileFixture {

    public static UserProfileDto createDtoForCreateEntity() {
        UserProfileDto dto = new UserProfileDto();
        dto.setLoyaltyLevel("SILVER");
        dto.setExternalBalance(new BigDecimal("1.00"));
        return dto;
    }

    public static UserProfileDto createDtoForUpdateEntity() {
        UserProfileDto dto = new UserProfileDto();
        dto.setLoyaltyLevel("BRONZE");
        dto.setExternalBalance(new BigDecimal(34));
        return dto;
    }

}
