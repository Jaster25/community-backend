package com.jaster25.communitybackend.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogInRequestDto {

    @NotEmpty(message = "NOT_NULL_USER_ID")
    private String id;
    @NotEmpty(message = "NOT_NULL_USER_PASSWORD")
    private String password;

    @Builder
    public LogInRequestDto(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
