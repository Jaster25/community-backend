package com.jaster25.communitybackend.domain.like.repository;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.comment.repository.CommentRepository;
import com.jaster25.communitybackend.domain.like.domain.LikeCommentEntity;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LikeCommentRepositoryTest {

    @Autowired
    private LikeCommentRepository likeCommentRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private UserEntity user1;
    private UserEntity user2;

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
        UserEntity user3 = UserEntity.builder()
                .username("user3")
                .password("1234")
                .build();
        PostEntity post1 = PostEntity.builder()
                .user(user1)
                .title("게시물 제목1")
                .content("게시물 내용1")
                .build();
        CommentEntity comment1 = CommentEntity.builder()
                .id(1L)
                .user(user2)
                .post(post1)
                .content("댓글 내용1")
                .build();
        LikeCommentEntity like1 = LikeCommentEntity.builder()
                .user(user2)
                .comment(comment1)
                .build();
        LikeCommentEntity like2 = LikeCommentEntity.builder()
                .user(user3)
                .comment(comment1)
                .build();
        userRepository.saveAll(List.of(user1, user2, user3));
        postRepository.save(post1);
        commentRepository.save(comment1);
        likeCommentRepository.saveAll(List.of(like1, like2));
    }

    @DisplayName("댓글 좋아요 개수 조회")
    @Nested
    class CountByCommentIdTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            // when
            int count = likeCommentRepository.countByCommentId(1L);

            // then
            assertEquals(4, count);
        }
    }

    @DisplayName("댓글 좋아요 존재 확인")
    @Nested
    class FindByUserAndCommentIdTest {
        @Test
        @DisplayName("성공 - 아직 누르지 않은 사람")
        void success_yet() throws Exception {
            // given
            // when
            Optional<LikeCommentEntity> optionalLikeCommentEntity =
                    likeCommentRepository.findByUserAndCommentId(user1, 1L);

            // then
            assertTrue(optionalLikeCommentEntity.isEmpty());
        }

        @DisplayName("성공 - 이미 누른 사람")
        @Test
        void success_already() throws Exception {
            // given
            // when
            Optional<LikeCommentEntity> optionalLikeCommentEntity =
                    likeCommentRepository.findByUserAndCommentId(user2, 1L);

            // then
            assertTrue(optionalLikeCommentEntity.isPresent());
        }
    }
}