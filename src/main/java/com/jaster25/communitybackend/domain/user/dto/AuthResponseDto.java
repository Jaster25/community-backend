package com.jaster25.communitybackend.domain.user.dto;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AuthResponseDto {

    private final String username;
    private final Set<String> roles;
    private final String profileImageUrl;
    private final int level;
    private final int point;

    @Builder
    private AuthResponseDto(String username, Set<String> roles, String profileImageUrl, int level, int point) {
        this.username = username;
        this.roles = roles;
        this.profileImageUrl = profileImageUrl;
        this.level = level;
        this.point = point;
    }

    public static AuthResponseDto of(UserEntity user) {
        return AuthResponseDto.builder()
                .username(user != null ? user.getUsername() : null)
                .roles(user != null
                        ? user.getRoles().stream()
                        .map(String::valueOf)
                        .collect(Collectors.toSet())
                        : null)
                .profileImageUrl(user != null
                        ? user.getProfileImageUrl()
                        : null)
                .build();
    }
}
