package com.jaster25.communitybackend.domain.like.service;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.comment.repository.CommentRepository;
import com.jaster25.communitybackend.domain.like.domain.LikeCommentEntity;
import com.jaster25.communitybackend.domain.like.domain.LikePostEntity;
import com.jaster25.communitybackend.domain.like.dto.LikeResponseDto;
import com.jaster25.communitybackend.domain.like.repository.LikeCommentRepository;
import com.jaster25.communitybackend.domain.like.repository.LikePostRepository;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.custom.DuplicatedValueException;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikePostRepository likePostRepository;
    @Mock
    private LikeCommentRepository likeCommentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;

    private UserEntity user1;
    private PostEntity post1;
    private CommentEntity comment1;

    @BeforeEach
    public void setup() {
        user1 = UserEntity.builder()
                .username("user1")
                .password("1234")
                .build();
        post1 = PostEntity.builder()
                .id(1L)
                .user(user1)
                .title("게시물 제목1")
                .content("게시물 내용1")
                .build();
        comment1 = CommentEntity.builder()
                .id(1L)
                .user(user1)
                .post(post1)
                .content("댓글 내용1")
                .build();
    }

    @DisplayName("게시물 좋아요 생성")
    @Nested
    class CreateLikePostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(likePostRepository.findByUserAndPostId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.empty());
            given(likePostRepository.countByPostId(anyLong()))
                    .willReturn(1);

            // when
            LikeResponseDto result = likeService.createLikePost(1L, user1);

            // then
            assertTrue(result.getIsLiked());
            assertEquals(1, result.getLikeCount());
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.createLikePost(1L, user1));
        }

        @DisplayName("실패 - 이미 좋아요 누른 게시물")
        @Test
        void failure_already() throws Exception {
            // given
            LikePostEntity like1 = LikePostEntity.builder()
                    .user(user1)
                    .post(post1)
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(likePostRepository.findByUserAndPostId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.of(like1));

            // when
            // then
            assertThrows(DuplicatedValueException.class,
                    () -> likeService.createLikePost(1L, user1));
        }
    }

    @DisplayName("게시물 좋아요 삭제")
    @Nested
    class DeleteLikePostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            LikePostEntity like1 = LikePostEntity.builder()
                    .user(user1)
                    .post(post1)
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(likePostRepository.findByUserAndPostId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.of(like1));

            // when
            likeService.deleteLikePost(1L, user1);

            // then
            verify(likePostRepository, times(1))
                    .delete(any(LikePostEntity.class));
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.deleteLikePost(1L, user1));
        }

        @DisplayName("실패 - 존재하지 않는 좋아요 ID")
        @Test
        void failure_nonExistentLikeId() throws Exception {
            // given
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(likePostRepository.findByUserAndPostId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.deleteLikePost(1L, user1));
        }
    }

    @DisplayName("게시물 좋아요 조회")
    @Nested
    class GetLikePostTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(likePostRepository.countByPostId(anyLong()))
                    .willReturn(3);
            given(likePostRepository.findByUserAndPostId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.empty());

            // when
            LikeResponseDto result = likeService.getLikePostCount(1L, user1);

            // then
            assertEquals(3, result.getLikeCount());
            assertFalse(result.getIsLiked());
        }

        @DisplayName("성공 - 좋아요 누른 게시물")
        @Test
        void success_already() throws Exception {
            // given
            LikePostEntity like1 = LikePostEntity.builder()
                    .user(user1)
                    .post(post1)
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(likePostRepository.countByPostId(anyLong()))
                    .willReturn(3);
            given(likePostRepository.findByUserAndPostId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.of(like1));

            // when
            LikeResponseDto result = likeService.getLikePostCount(1L, user1);

            // then
            assertEquals(3, result.getLikeCount());
            assertTrue(result.getIsLiked());
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.getLikePostCount(1L, user1));
        }
    }

    @DisplayName("댓글 좋아요 생성")
    @Nested
    class CreateLikeCommentTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(comment1));
            given(likeCommentRepository.findByUserAndCommentId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.empty());
            given(likeCommentRepository.countByCommentId(anyLong()))
                    .willReturn(1);

            // when
            LikeResponseDto result = likeService.createLikeComment(1L, user1);

            // then
            assertTrue(result.getIsLiked());
            assertEquals(1, result.getLikeCount());
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        void failure_nonExistentCommentId() throws Exception {
            // given
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.createLikeComment(1L, user1));
        }

        @DisplayName("실패 - 이미 좋아요 누른 댓글")
        @Test
        void failure_already() throws Exception {
            // given
            LikeCommentEntity like1 = LikeCommentEntity.builder()
                    .user(user1)
                    .comment(comment1)
                    .build();
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(comment1));
            given(likeCommentRepository.findByUserAndCommentId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.of(like1));

            // when
            // then
            assertThrows(DuplicatedValueException.class,
                    () -> likeService.createLikeComment(1L, user1));
        }
    }


    @DisplayName("댓글 좋아요 삭제")
    @Nested
    class DeleteLikeCommentTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            LikeCommentEntity like1 = LikeCommentEntity.builder()
                    .user(user1)
                    .comment(comment1)
                    .build();
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(comment1));
            given(likeCommentRepository.findByUserAndCommentId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.of(like1));

            // when
            likeService.deleteLikeComment(1L, user1);

            // then
            verify(likeCommentRepository, times(1))
                    .delete(any(LikeCommentEntity.class));
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        void failure_nonExistentCommentId() throws Exception {
            // given
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.deleteLikeComment(1L, user1));
        }

        @DisplayName("실패 - 존재하지 않는 좋아요 ID")
        @Test
        void failure_nonExistentLikeId() throws Exception {
            // given
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(comment1));
            given(likeCommentRepository.findByUserAndCommentId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.deleteLikeComment(1L, user1));
        }
    }

    @DisplayName("댓글 좋아요 조회")
    @Nested
    class GetLikeCommentTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(comment1));
            given(likeCommentRepository.countByCommentId(anyLong()))
                    .willReturn(3);
            given(likeCommentRepository.findByUserAndCommentId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.empty());

            // when
            LikeResponseDto result = likeService.getLikeCommentCount(1L, user1);

            // then
            assertEquals(3, result.getLikeCount());
            assertFalse(result.getIsLiked());
        }

        @DisplayName("성공 - 좋아요 누른 댓글")
        @Test
        void success_already() throws Exception {
            // given
            LikeCommentEntity like1 = LikeCommentEntity.builder()
                    .user(user1)
                    .comment(comment1)
                    .build();
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(comment1));
            given(likeCommentRepository.countByCommentId(anyLong()))
                    .willReturn(3);
            given(likeCommentRepository.findByUserAndCommentId(any(UserEntity.class), anyLong()))
                    .willReturn(Optional.of(like1));

            // when
            LikeResponseDto result = likeService.getLikeCommentCount(1L, user1);

            // then
            assertEquals(3, result.getLikeCount());
            assertTrue(result.getIsLiked());
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    () -> likeService.getLikeCommentCount(1L, user1));
        }
    }
}