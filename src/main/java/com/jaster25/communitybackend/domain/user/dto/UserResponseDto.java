package com.jaster25.communitybackend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final String id;

    @Builder
    public UserResponseDto(String id) {
        this.id = id;
    }
}
