package com.jaster25.communitybackend.domain.comment.repository;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        UserEntity user1 = UserEntity.builder()
                .username("user1")
                .password("1234")
                .build();
        UserEntity user2 = UserEntity.builder()
                .username("user2")
                .password("1234")
                .build();
        PostEntity post1 = PostEntity.builder()
                .id(1L)
                .user(user1)
                .title("게시물 제목1")
                .content("게시물 내용1")
                .build();
        CommentEntity comment1 = CommentEntity.builder()
                .id(1L)
                .user(user1)
                .post(post1)
                .content("댓글 내용1")
                .build();
        CommentEntity comment2 = CommentEntity.builder()
                .id(2L)
                .user(user1)
                .post(post1)
                .content("댓글 내용2")
                .build();
        CommentEntity comment3 = CommentEntity.builder()
                .id(3L)
                .user(user2)
                .post(post1)
                .content("댓글 내용3")
                .build();
        userRepository.saveAll(List.of(user1, user2));
        postRepository.save(post1);
        commentRepository.saveAll(List.of(comment1, comment2, comment3));
    }

    @DisplayName("댓글 엔티티 목록 조회")
    @Nested
    class FindAllTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            Pageable pageable = PageRequest.ofSize(10).withPage(0);

            // when
            Page<CommentEntity> commentPage = commentRepository.findAllByPostIdAndParentIsNullOrderByIdDesc(pageable, 1L);

            // then
            assertEquals(1, commentPage.getTotalPages());
            assertEquals(3, commentPage.getTotalElements());
        }

        @DisplayName("성공 - 특정 ID 밑으로")
        @Test
        void success_less() throws Exception {
            // given
            Pageable pageable = PageRequest.ofSize(10).withPage(0);

            // when
            Page<CommentEntity> commentPage = commentRepository.findAllByPostIdAndIdLessThanAndParentIsNullOrderByIdDesc(pageable, 1L, 2L);

            // then
            assertEquals(1, commentPage.getTotalPages());
            assertEquals(1, commentPage.getTotalElements());
        }
    }
}