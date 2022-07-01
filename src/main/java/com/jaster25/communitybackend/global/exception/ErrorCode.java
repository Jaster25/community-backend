package com.jaster25.communitybackend.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * G: Global
     */
    INVALID_REQUEST("G001", "잘못된 요청입니다."),
    NONEXISTENT_AUTHENTICATION("G002", "로그인이 필요합니다."),
    NONEXISTENT_AUTHORIZATION("G003", "권한이 없습니다."),

    /**
     * U: User
     */
    NONEXISTENT_USER("U001", "존재하지 않는 사용자입니다."),
    // 유효성 확인
    NOT_NULL_USER_ID("U101", "아이디는 필수입니다."),
    NOT_NULL_USER_PASSWORD("U102", "비밀번호는 필수입니다."),
    // 회원가입
    DUPLICATED_USER_ID("U201", "이미 존재하는 사용자 아이디입니다."),
    // 로그인
    LOGIN_FAILURE("U301", "아이디 또는 비밀번호가 잘못 입력되었습니다."),

    /**
     * P: Post
     */
    NONEXISTENT_POST("P001", "존재하지 않는 게시물입니다."),
    // 유효성 확인
    NOT_NULL_POST_TITLE("P101", "게시물 제목은 필수입니다."),
    NOT_NULL_POST_CONTENT("P102", "게시물 내용은 필수입니다."),

    /**
     * C: Comment
     */
    NONEXISTENT_COMMENT("C001", "존재하지 않는 댓글입니다."),
    // 유효성 확인
    NOT_NULL_COMMENT_CONTENT("C101", "댓글 내용은 필수입니다."),

    /**
     * L: Like
     */
    NONEXISTENT_LIKE_POST("L001", "좋아요를 누르지 않은 게시물입니다."),
    NONEXISTENT_LIKE_COMMENT("L002", "좋아요를 누르지 않은 댓글입니다."),
    // 중복
    DUPLICATED_LIKE_POST("L101", "이미 좋아요를 누른 게시물입니다."),
    DUPLICATED_LIKE_COMMENT("L102", "이미 좋아요를 누른 댓글입니다."),

    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
