package com.jaster25.communitybackend.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentsResponseDto {

    public final List<CommentResponseDto> comments;

    @Builder
    public CommentsResponseDto(List<CommentResponseDto> comments) {
        this.comments = comments;
    }
}
