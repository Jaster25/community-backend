package com.jaster25.communitybackend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponseDto {

    private final String accessToken;

    @Builder
    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
