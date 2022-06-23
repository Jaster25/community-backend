package com.jaster25.communitybackend.domain.user.controller;

import com.jaster25.communitybackend.domain.user.dto.UserRequestDto;
import com.jaster25.communitybackend.domain.user.dto.UserResponseDto;
import com.jaster25.communitybackend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUserApi(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserApi(@PathVariable String username) {
        UserResponseDto userResponseDto = userService.getUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }
}
