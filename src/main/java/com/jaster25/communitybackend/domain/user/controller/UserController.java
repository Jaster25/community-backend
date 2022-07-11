package com.jaster25.communitybackend.domain.user.controller;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.AuthResponseDto;
import com.jaster25.communitybackend.domain.user.dto.UserRequestDto;
import com.jaster25.communitybackend.domain.user.service.UserService;
import com.jaster25.communitybackend.global.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<AuthResponseDto> createUserApi(@Valid @RequestBody UserRequestDto userRequestDto) {
        AuthResponseDto authResponseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    @PutMapping("/me/profile-image")
    public ResponseEntity<AuthResponseDto> updateUserProfileImageApi(@RequestPart("file") MultipartFile multipartFile,
                                                                     @CurrentUser UserEntity user) {
        AuthResponseDto authResponseDto = userService.updateProfileImage(multipartFile, user);
        return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
    }
}
