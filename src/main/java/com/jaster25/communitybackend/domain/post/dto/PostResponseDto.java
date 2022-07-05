package com.jaster25.communitybackend.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {

    private final Long id;
    private final String writer;
    private final String title;
    private final int viewCount;
    private final int likeCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @Builder
    public PostResponseDto(Long id, String writer, String title, int viewCount, int likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    public static PostResponseDto of(PostEntity post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .writer(post.getUser().getUsername())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikes().size())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
