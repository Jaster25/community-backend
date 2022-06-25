package com.jaster25.communitybackend.domain.post.service;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.dto.PostDetailResponseDto;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;

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

    @DisplayName("게시물 생성 성공")
    @Test
    void createPost() throws Exception {
        // given
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .build();

        // when
        PostDetailResponseDto postDetailResponseDto = postService.createPost(user1, postRequestDto);

        // then
        assertEquals(postRequestDto.getTitle(), postDetailResponseDto.getTitle());
        assertEquals(postRequestDto.getContent(), postDetailResponseDto.getContent());
        verify(postRepository, times(1))
                .save(any(PostEntity.class));
    }

    @DisplayName("게시물 상세 조회 성공")
    @Test
    void getPost() throws Exception {
        // given
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        PostDetailResponseDto postDetailResponseDto = postService.getPost(1L, user1);

        // then
        assertEquals(post.getId(), postDetailResponseDto.getId());
        assertEquals(post.getUser().getUsername(), postDetailResponseDto.getWriter());
        assertEquals(post.getTitle(), postDetailResponseDto.getTitle());
        assertEquals(post.getContent(), postDetailResponseDto.getContent());
        verify(postRepository, times(1))
                .findById(anyLong());
    }

    @DisplayName("게시물 상세 조회 실패 - 존재하지 않는 게시물 ID")
    @Test
    void getPost_nonExistentId() throws Exception {
        // given
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThrows(NonExistentException.class,
                () -> postService.getPost(1L, user1));
    }
}