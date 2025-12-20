package com.example.user.profile.service.mapper;

import com.example.user.profile.service.model.UserProfile;
import com.example.userprofile.api.dto.UserProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {

    UserProfileDto toDto(UserProfile userProfile);

    @Mapping(target = "userId", ignore = true)
    UserProfile toEntity(UserProfileDto dto);

}
