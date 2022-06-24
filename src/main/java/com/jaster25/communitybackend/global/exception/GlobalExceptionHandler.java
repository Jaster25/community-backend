package com.jaster25.communitybackend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HTTP status: 400
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentNotValidException e, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.valueOf(e.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception exception, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
