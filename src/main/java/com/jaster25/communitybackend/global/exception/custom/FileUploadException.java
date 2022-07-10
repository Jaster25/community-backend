package com.jaster25.communitybackend.global.exception.custom;

import com.jaster25.communitybackend.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class FileUploadException extends RuntimeException {

    private final ErrorCode errorCode;

    public FileUploadException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
