package com.jaster25.communitybackend.domain.post.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRequestDto {

    @NotEmpty(message = "NOT_NULL_POST_TITLE")
    private String title;
    @NotEmpty(message = "NOT_NULL_POST_CONTENT")
    private String content;

    @Builder
    public PostRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
