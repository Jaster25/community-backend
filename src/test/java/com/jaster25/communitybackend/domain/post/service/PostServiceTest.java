package com.jaster25.communitybackend.domain.post.service;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.dto.PostDetailResponseDto;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private UserEntity user2;
    private UserEntity admin1;

    @BeforeEach
    public void setup() {
        user1 = UserEntity.builder()
                .username("user1")
                .password("1234")
                .build();
        user2 = UserEntity.builder()
                .username("user2")
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

    @DisplayName("게시물 수정 성공")
    @Test
    void updatePost() throws Exception {
        // given
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("수정된 게시물 제목")
                .content("수정된 게시물 내용")
                .build();
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        PostDetailResponseDto postDetailResponseDto = postService.updatePost(1L, user1, postRequestDto);

        // then
        assertEquals(postRequestDto.getTitle(), postDetailResponseDto.getTitle());
        assertEquals(postRequestDto.getContent(), postDetailResponseDto.getContent());
        assertNotNull(postDetailResponseDto.getUpdatedAt());
        verify(postRepository, times(1))
                .findById(anyLong());
    }

    @DisplayName("게시물 수정 성공 - 관리자")
    @Test
    void updatePost_admin() throws Exception {
        // given
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("수정된 게시물 제목")
                .content("수정된 게시물 내용")
                .build();
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        PostDetailResponseDto postDetailResponseDto = postService.updatePost(1L, admin1, postRequestDto);

        // then
        assertEquals(postRequestDto.getTitle(), postDetailResponseDto.getTitle());
        assertEquals(postRequestDto.getContent(), postDetailResponseDto.getContent());
        assertNotNull(postDetailResponseDto.getUpdatedAt());
        verify(postRepository, times(1))
                .findById(anyLong());
    }

    @DisplayName("게시물 수정 실패 - 다른 사용자")
    @Test
    void updatePost_otherUser() throws Exception {
        // given
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title("수정된 게시물 제목")
                .content("수정된 게시물 내용")
                .build();
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        // then
        assertThrows(UnAuthorizedException.class,
                () -> postService.updatePost(1L, user2, postRequestDto));
    }

    @DisplayName("게시물 삭제 성공")
    @Test
    void deletePost() throws Exception {
        // given
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        postService.deletePost(1L, user1);

        // then
        verify(postRepository, times(1))
                .findById(anyLong());
        verify(postRepository, times(1))
                .delete(any(PostEntity.class));
    }

    @DisplayName("게시물 삭제 성공 - 관리자")
    @Test
    void deletePost_admin() throws Exception {
        // given
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        postService.deletePost(1L, admin1);

        // then
        verify(postRepository, times(1))
                .findById(anyLong());
        verify(postRepository, times(1))
                .delete(any(PostEntity.class));
    }

    @DisplayName("게시물 삭제 실패 - 다른 사용자")
    @Test
    void deletePost_otherUser() throws Exception {
        // given
        PostEntity post = PostEntity.builder()
                .title("게시물 제목")
                .content("게시물 내용")
                .user(user1)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));

        // when
        // then
        assertThrows(UnAuthorizedException.class,
                () -> postService.deletePost(1L, user2));
    }
}