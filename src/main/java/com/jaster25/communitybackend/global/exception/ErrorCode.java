package com.jaster25.communitybackend.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * C: 공통
     */
    INVALID_REQUEST("C001", "잘못된 요청입니다."),
    NONEXISTENT_AUTHENTICATION("C002", "로그인이 필요합니다."),
    NONEXISTENT_AUTHORIZATION("C003", "권한이 없습니다."),

    /**
     * U: User
     */
    NONEXISTENT_USER("U001", "존재하지 않는 사용자입니다."),
    // 유효성

    // 회원가입

    // 로그인


    ;


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
