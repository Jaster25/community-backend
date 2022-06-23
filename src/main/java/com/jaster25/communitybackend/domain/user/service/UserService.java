package com.jaster25.communitybackend.domain.user.service;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.UserRequestDto;
import com.jaster25.communitybackend.domain.user.dto.UserResponseDto;
import com.jaster25.communitybackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        UserEntity userEntity = UserEntity.builder()
                .username(userRequestDto.getId())
                .password(userRequestDto.getPassword())
                .build();

        userRepository.save(userEntity);

        return UserResponseDto.builder()
                .id(userEntity.getUsername())
                .build();
    }

    public UserResponseDto getUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 사용자입니다."));

        return UserResponseDto.builder()
                .id(userEntity.getUsername())
                .build();
    }
}
