package com.jaster25.communitybackend.domain.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponseDto {

    private final int likeCount;
    private final Boolean isLiked;

    @Builder
    public LikeResponseDto(int likeCount, Boolean isLiked) {
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }
}
