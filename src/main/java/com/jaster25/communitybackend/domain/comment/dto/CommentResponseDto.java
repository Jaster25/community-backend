package com.jaster25.communitybackend.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentResponseDto {

    private final Long id;
    private final String writer;
    private final String profileImageUrl;
    private final String content;
    private final Boolean isDeleted;
    private final Boolean canDelete;
    private final int likeCount;
    private final List<CommentResponseDto> children;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    private CommentResponseDto(Long id, String writer, String profileImageUrl, String content, Boolean isDeleted, Boolean canDelete, int likeCount, List<CommentResponseDto> children, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.writer = writer;
        this.profileImageUrl = profileImageUrl;
        this.content = content;
        this.isDeleted = isDeleted;
        this.canDelete = canDelete;
        this.likeCount = likeCount;
        this.children = children;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponseDto of(UserEntity user, CommentEntity comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .writer(comment.getUser().getUsername())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .content(comment.getContent())
                .isDeleted(comment.isDeleted())
                .canDelete(user != null && (user.equals(comment.getUser()) || user.isAdmin()))
                .likeCount(comment.getLikes().size())
                .children(comment.getChildren().stream()
                        .map(c -> CommentResponseDto.of(user, c))
                        .collect(Collectors.toList()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
