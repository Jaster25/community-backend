package com.jaster25.communitybackend.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDetailResponseDto {

    private final Long id;
    private final String writer;
    private final String title;
    private final String content;
    private final int viewCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    private PostDetailResponseDto(Long id, String writer, String title, String content, int viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostDetailResponseDto of(PostEntity post) {
        return PostDetailResponseDto.builder()
                .id(post.getId())
                .writer(post.getUser().getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
