package com.jaster25.communitybackend.domain.user.service;

import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.AuthResponseDto;
import com.jaster25.communitybackend.domain.user.repository.UserRepository;
import com.jaster25.communitybackend.global.exception.custom.FileUploadException;
import com.jaster25.communitybackend.global.util.AmazonS3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private AmazonS3Util amazonS3Util;
    @Mock
    private UserRepository userRepository;

    private UserEntity user1;
    private UserEntity admin1;

    @BeforeEach
    public void setup() {
        user1 = UserEntity.builder()
                .username("user1")
                .password("1234")
                .build();
        admin1 = UserEntity.builder()
                .username("admin1")
                .password("1234")
                .build();
        admin1.addRole(Role.ROLE_ADMIN);
    }

    @DisplayName("프로필 사진 업데이트")
    @Nested
    class UpdateProfileImageTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            MockMultipartFile mockMainImageFile = new MockMultipartFile(
                    "profileImage",
                    "profileImage",
                    "image/png",
                    "profileImage".getBytes());

            given(amazonS3Util.upload(any(MultipartFile.class)))
                    .willReturn("test-url.com");

            // when
            AuthResponseDto authResponseDto = userService.updateProfileImage(mockMainImageFile, user1);

            // then
            assertEquals(authResponseDto.getId(), user1.getUsername());
            assertNotNull(authResponseDto.getProfileImageUrl());
            verify(userRepository, times(1))
                    .save(any(UserEntity.class));
        }

        @DisplayName("실패 - 파일 미첨부")
        @Test
        void failure_emptyFile() throws Exception {
            // given
            given(amazonS3Util.upload(null))
                    .willThrow(FileUploadException.class);

            // when
            // then
            assertThrows(FileUploadException.class,
                    () -> userService.updateProfileImage(null, user1));
        }
    }
}

