package com.jaster25.communitybackend.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final String method;
    private final String path;
    private final String message;
    private final String code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    @Builder
    public ErrorResponse(String method, String path, String message, String code) {
        this.method = method;
        this.path = path;
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(ErrorCode errorCode, HttpServletRequest request) {
        return ErrorResponse.builder()
                .method(request.getMethod())
                .path(request.getRequestURI())
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();
    }
}
