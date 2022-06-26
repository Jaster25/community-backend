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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @Builder
    public PostResponseDto(Long id, String writer, String title, int viewCount, LocalDateTime createdAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
    }

    public static PostResponseDto of(PostEntity post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .writer(post.getUser().getUsername())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
