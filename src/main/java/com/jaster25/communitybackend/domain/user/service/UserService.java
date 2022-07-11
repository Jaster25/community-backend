package com.jaster25.communitybackend.domain.user.service;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.AuthResponseDto;
import com.jaster25.communitybackend.domain.user.dto.UserRequestDto;
import com.jaster25.communitybackend.domain.user.repository.UserRepository;
import com.jaster25.communitybackend.global.config.security.UserAdapter;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.DuplicatedValueException;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import com.jaster25.communitybackend.global.util.AmazonS3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final AmazonS3Util amazonS3Util;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByUsername(userRequestDto.getId()).orElse(null) != null) {
            throw new DuplicatedValueException(ErrorCode.DUPLICATED_USER_ID);
        }

        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .username(userRequestDto.getId())
                .password(encodedPassword)
                .build();

        userRepository.save(userEntity);

        return AuthResponseDto.of(userEntity);
    }

    @Transactional
    public AuthResponseDto updateProfileImage(MultipartFile multipartFile, UserEntity user) {
        String url = amazonS3Util.upload(multipartFile);
        user.updateProfileImageUrl(url);
        userRepository.save(user);
        return AuthResponseDto.of(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_USER));

        return new UserAdapter(user);
    }
}
