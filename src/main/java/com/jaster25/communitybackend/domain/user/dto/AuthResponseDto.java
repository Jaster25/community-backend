package com.jaster25.communitybackend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class AuthResponseDto {

     private final String username;
     private final Set<String> roles;
     private final String profileImageUrl;
     private final int level;
     private final int point;

     @Builder
    public AuthResponseDto(String username, Set<String> roles, String profileImageUrl, int level, int point) {
        this.username = username;
        this.roles = roles;
        this.profileImageUrl = profileImageUrl;
        this.level = level;
        this.point = point;
    }
}
