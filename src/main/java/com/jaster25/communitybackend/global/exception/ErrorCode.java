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
    // 유효성 확인
    NOT_NULL_USER_ID("U101", "아이디는 필수입니다."),
    NOT_NULL_USER_PASSWORD("U102", "비밀번호는 필수입니다."),
    // 회원가입
    DUPLICATED_USER_ID("U201", "이미 존재하는 사용자 아이디입니다."),
    // 로그인
    LOGIN_FAILURE("U301", "아이디 또는 비밀번호가 잘못 입력되었습니다.")
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
