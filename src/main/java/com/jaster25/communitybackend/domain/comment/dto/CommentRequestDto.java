package com.jaster25.communitybackend.domain.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequestDto {

    @NotEmpty(message = "NOT_NULL_COMMENT_CONTENT")
    private String content;

    @Builder
    public CommentRequestDto(String content) {
        this.content = content;
    }
}
