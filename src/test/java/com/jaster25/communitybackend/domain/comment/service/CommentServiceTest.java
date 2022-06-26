package com.jaster25.communitybackend.domain.comment.service;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.comment.dto.CommentRequestDto;
import com.jaster25.communitybackend.domain.comment.dto.CommentResponseDto;
import com.jaster25.communitybackend.domain.comment.dto.CommentsResponseDto;
import com.jaster25.communitybackend.domain.comment.repository.CommentRepository;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;

    private UserEntity user1;
    private UserEntity user2;
    private UserEntity admin1;
    private PostEntity post1;

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
        post1 = PostEntity.builder()
                .user(user1)
                .title("게시물 제목1")
                .content("게시물 내용1")
                .build();
    }

    @DisplayName("댓글 생성")
    @Nested
    class CreateCommentTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            CommentRequestDto commentRequestDto  = CommentRequestDto.builder()
                    .content("댓글1")
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));

            // when
            CommentResponseDto commentResponseDto = commentService.createComment(1L, user1, commentRequestDto);

            // then
            assertEquals(commentRequestDto.getContent(), commentResponseDto.getContent());
            assertEquals(user1.getUsername(), commentResponseDto.getWriter());
        }

        @DisplayName("성공 - 대댓글")
        @Test
        void success_childComment() throws Exception {
            // given
            CommentRequestDto commentRequestDto  = CommentRequestDto.builder()
                    .content("대댓글1")
                    .build();
            CommentEntity parentComment = CommentEntity.builder()
                    .id(1L)
                    .user(user1)
                    .post(post1)
                    .content("부모 댓글1")
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.of(parentComment));

            // when
            CommentResponseDto commentResponseDto = commentService.createComment(1L, user1, 1L, commentRequestDto);

            // then
            assertEquals(commentRequestDto.getContent(), commentResponseDto.getContent());
            assertEquals(user1.getUsername(), commentResponseDto.getWriter());
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            CommentRequestDto commentRequestDto  = CommentRequestDto.builder()
                    .content("대댓글1")
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    ()-> commentService.createComment(1L, user1, commentRequestDto));
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        void failure_nonExistentParentCommentId() throws Exception {
            // given
            CommentRequestDto commentRequestDto  = CommentRequestDto.builder()
                    .content("대댓글1")
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(commentRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(NonExistentException.class,
                    ()-> commentService.createComment(1L, user1, 1L, commentRequestDto));
        }
    }

    @DisplayName("댓글 목록 조회")
    @Nested
    class GetCommentsTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            CommentEntity comment1 = CommentEntity.builder()
                    .user(user1)
                    .post(post1)
                    .content("댓글 내용1")
                    .build();
            CommentEntity comment2 = CommentEntity.builder()
                    .user(user2)
                    .post(post1)
                    .content("댓글 내용2")
                    .build();
            CommentEntity comment3 = CommentEntity.builder()
                    .user(user1)
                    .post(post1)
                    .content("댓글 내용3")
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(commentRepository.findAllByPostIdAndParentIsNullOrderByIdDesc(any(Pageable.class), anyLong()))
                    .willReturn(new PageImpl<>(List.of(comment1, comment2, comment3)));

            // when
            CommentsResponseDto commentsResponseDto = commentService.getComments(1L, user1, null, 10);

            // then
            assertEquals(3, commentsResponseDto.getComments().size());
            assertTrue(commentsResponseDto.getComments().get(0).getCanDelete());
            assertFalse(commentsResponseDto.getComments().get(1).getCanDelete());
            assertTrue(commentsResponseDto.getComments().get(2).getCanDelete());
        }
        
        @DisplayName("성공 - 비로그인")
        @Test
        void success_notLoggedIn() throws Exception {
            // given
            CommentEntity comment1 = CommentEntity.builder()
                    .user(user1)
                    .post(post1)
                    .content("댓글 내용1")
                    .build();
            CommentEntity comment2 = CommentEntity.builder()
                    .user(user2)
                    .post(post1)
                    .content("댓글 내용2")
                    .build();
            CommentEntity comment3 = CommentEntity.builder()
                    .user(user1)
                    .post(post1)
                    .content("댓글 내용3")
                    .build();
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post1));
            given(commentRepository.findAllByPostIdAndParentIsNullOrderByIdDesc(any(Pageable.class), anyLong()))
                    .willReturn(new PageImpl<>(List.of(comment1, comment2, comment3)));

            // when
            CommentsResponseDto commentsResponseDto = commentService.getComments(1L, null, null, 10);

            // then
            assertEquals(3, commentsResponseDto.getComments().size());
            assertFalse(commentsResponseDto.getComments().get(0).getCanDelete());
            assertFalse(commentsResponseDto.getComments().get(1).getCanDelete());
            assertFalse(commentsResponseDto.getComments().get(2).getCanDelete());
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
                    () -> commentService.getComments(1L, user1, null, 10));
        }
    }
}