package com.jaster25.communitybackend.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class PostsResponseDto {

    private final PageResponseDto page;
    private final List<PostResponseDto> posts;

    @Builder
    public PostsResponseDto(PageResponseDto page, List<PostResponseDto> posts) {
        this.page = page;
        this.posts = posts;
    }
}
