package com.jaster25.communitybackend.global.exception.custom;


import com.jaster25.communitybackend.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UnAuthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnAuthorizedException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
