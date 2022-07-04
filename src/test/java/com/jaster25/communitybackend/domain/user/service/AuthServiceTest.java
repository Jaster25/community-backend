package com.jaster25.communitybackend.domain.user.service;

import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.AuthResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

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


    @DisplayName("사용자 정보 조회")
    @Nested
    class GetAuthApiTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            // when
            AuthResponseDto authResponseDto = authService.getAuth(user1);

            // then
            assertEquals("user1", authResponseDto.getUsername());
            assertEquals(Set.of(Role.ROLE_USER.toString()), authResponseDto.getRoles());
        }

        @DisplayName("성공 - 비로그인")
        @Test
        void success_notLoggedIn() throws Exception {
            // given
            // when
            AuthResponseDto authResponseDto = authService.getAuth(null);

            // then
            assertNull(authResponseDto.getUsername());
            assertNull(authResponseDto.getRoles());
        }

        @DisplayName("성공 - 관리자")
        @Test
        void success_admin() throws Exception {
            // given
            // when
            AuthResponseDto authResponseDto = authService.getAuth(admin1);

            // then
            assertEquals("admin1", authResponseDto.getUsername());
            assertEquals(Set.of(Role.ROLE_USER.toString(), Role.ROLE_ADMIN.toString()), authResponseDto.getRoles());
        }
    }
}

