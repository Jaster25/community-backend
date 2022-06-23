package com.jaster25.communitybackend.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestDto {

    @NotEmpty
    private String id;
    @NotEmpty
    private String password;

    @Builder
    public UserRequestDto(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
