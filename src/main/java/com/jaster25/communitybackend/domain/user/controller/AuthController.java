package com.jaster25.communitybackend.domain.user.controller;


import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.LogInRequestDto;
import com.jaster25.communitybackend.domain.user.dto.TokenResponseDto;
import com.jaster25.communitybackend.domain.user.service.AuthService;
import com.jaster25.communitybackend.global.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> logInApi(@RequestBody @Valid LogInRequestDto logInRequestDto) {
        TokenResponseDto tokenResponseDto = authService.logIn(logInRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponseDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logOutApi(@CurrentUser UserEntity user) {
        // TODO
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
