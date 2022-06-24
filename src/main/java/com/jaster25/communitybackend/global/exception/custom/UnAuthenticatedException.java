package com.jaster25.communitybackend.global.exception.custom;


import com.jaster25.communitybackend.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UnAuthenticatedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnAuthenticatedException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
