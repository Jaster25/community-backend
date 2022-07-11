package com.jaster25.communitybackend.domain.user.domain;

import lombok.Getter;

@Getter
public enum Point {
    CREATE_POST(5), // 게시물 작성
    CREATE_COMMENT(1), // 댓글 작성
    GET_POST_LIKE(3), // 다른 사용자가 내가 작성한 게시물에 좋아요
    GET_COMMENT_LIKE(3), // 다른 사용자가 내가 작성한 댓글에 좋아요
    ;

    private final int point;

    Point(int point) {
        this.point = point;
    }
}
