package com.jaster25.communitybackend.domain.post.service;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.dto.PostDetailResponseDto;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.post.dto.PostsResponseDto;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.repository.UserRepository;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

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

    @DisplayName("게시물 생성")
    @Nested
    class CreatePostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder().title("게시물 제목").content("게시물 내용").build();

            // when
            PostDetailResponseDto postDetailResponseDto = postService.createPost(user1, postRequestDto);

            // then
            assertEquals(postRequestDto.getTitle(), postDetailResponseDto.getTitle());
            assertEquals(postRequestDto.getContent(), postDetailResponseDto.getContent());
            verify(postRepository, times(1)).save(any(PostEntity.class));
        }
    }

    @DisplayName("게시물 목록 조회")
    @Nested
    class GetPostsTest {
        @DisplayName("성공 - 제목으로 검색")
        @Test
        void success() throws Exception {
            // given
            PostEntity post1 = PostEntity.builder()
                    .user(user1)
                    .title("게시물 제목1")
                    .content("게시물 내용1")
                    .build();
            PostEntity post2 = PostEntity.builder()
                    .user(user1)
                    .title("게시물 제목2")
                    .content("게시물 내용2")
                    .build();
            PostEntity post3 = PostEntity.builder()
                    .user(user2)
                    .title("게시물 제목3")
                    .content("게시물 내용3")
                    .build();
            List<PostEntity> posts = List.of(post1, post2, post3);
            Page<PostEntity> postPage = new PageImpl<>(posts);
            given(postRepository.findAllByTitleContainingIgnoreCase(any(Pageable.class), anyString()))
                    .willReturn(postPage);

            // when
            PostsResponseDto postsResponseDto = postService.getPosts(1, 10, "title", "제목");

            // then
            assertEquals(3, postsResponseDto.getPage().getTotalElement());
            assertEquals(1, postsResponseDto.getPage().getTotalPage());
        }
    }

    @DisplayName("게시물 상세 조회")
    @Nested
    class GetPostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            PostDetailResponseDto postDetailResponseDto = postService.getPost(1L, user1);

            // then
            assertEquals(post.getId(), postDetailResponseDto.getId());
            assertEquals(post.getUser().getUsername(), postDetailResponseDto.getWriter());
            assertEquals(post.getTitle(), postDetailResponseDto.getTitle());
            assertEquals(post.getContent(), postDetailResponseDto.getContent());
            verify(postRepository, times(1)).findById(anyLong());
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        void failure_nonExistentId() throws Exception {
            // given
            given(postRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class, () -> postService.getPost(1L, user1));
        }
    }

    @DisplayName("게시물 수정")
    @Nested
    class UpdatePostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder().title("수정된 게시물 제목").content("수정된 게시물 내용").build();
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            PostDetailResponseDto postDetailResponseDto = postService.updatePost(1L, user1, postRequestDto);

            // then
            assertEquals(postRequestDto.getTitle(), postDetailResponseDto.getTitle());
            assertEquals(postRequestDto.getContent(), postDetailResponseDto.getContent());
            assertNotNull(postDetailResponseDto.getUpdatedAt());
            verify(postRepository, times(1)).findById(anyLong());
        }

        @DisplayName("성공 - 관리자")
        @Test
        void success_admin() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder().title("수정된 게시물 제목").content("수정된 게시물 내용").build();
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            PostDetailResponseDto postDetailResponseDto = postService.updatePost(1L, admin1, postRequestDto);

            // then
            assertEquals(postRequestDto.getTitle(), postDetailResponseDto.getTitle());
            assertEquals(postRequestDto.getContent(), postDetailResponseDto.getContent());
            assertNotNull(postDetailResponseDto.getUpdatedAt());
            verify(postRepository, times(1)).findById(anyLong());
        }

        @DisplayName("실패 - 다른 사용자")
        @Test
        void failure_otherUser() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder().title("수정된 게시물 제목").content("수정된 게시물 내용").build();
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            // then
            assertThrows(UnAuthorizedException.class, () -> postService.updatePost(1L, user2, postRequestDto));
        }
    }

    @DisplayName("게시물 삭제")
    @Nested
    class DeletePostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            postService.deletePost(1L, user1);

            // then
            verify(postRepository, times(1)).findById(anyLong());
            verify(postRepository, times(1)).delete(any(PostEntity.class));
        }

        @DisplayName("성공 - 관리자")
        @Test
        void success_admin() throws Exception {
            // given
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            postService.deletePost(1L, admin1);

            // then
            verify(postRepository, times(1)).findById(anyLong());
            verify(postRepository, times(1)).delete(any(PostEntity.class));
        }

        @DisplayName("실패 - 다른 사용자")
        @Test
        void failure_otherUser() throws Exception {
            // given
            PostEntity post = PostEntity.builder().title("게시물 제목").content("게시물 내용").user(user1).build();
            given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

            // when
            // then
            assertThrows(UnAuthorizedException.class, () -> postService.deletePost(1L, user2));
        }
    }
}