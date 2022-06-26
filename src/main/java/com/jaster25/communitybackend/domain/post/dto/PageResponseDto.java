package com.jaster25.communitybackend.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResponseDto {

    private final int totalPage;
    private final long totalElement;

    @Builder
    public PageResponseDto(int totalPage, long totalElement) {
        this.totalPage = totalPage;
        this.totalElement = totalElement;
    }
}
