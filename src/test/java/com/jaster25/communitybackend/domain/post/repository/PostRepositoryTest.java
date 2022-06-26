package com.jaster25.communitybackend.domain.post.repository;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
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
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private UserEntity user1;
    private UserEntity user2;
    private PostEntity post1;
    private PostEntity post2;
    private PostEntity post3;
    private PostEntity post4;

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
        post1 = PostEntity.builder()
                .id(1L)
                .user(user1)
                .title("게시물 제목1")
                .content("게시물 내용1")
                .build();
        post2 = PostEntity.builder()
                .id(2L)
                .user(user1)
                .title("게시물 제목2")
                .content("게시물 내용2")
                .build();
        post3 = PostEntity.builder()
                .id(3L)
                .user(user1)
                .title("게시물 제목3")
                .content("게시물 내용3")
                .build();
        post4 = PostEntity.builder()
                .id(4L)
                .user(user2)
                .title("게시물 제목4")
                .content("게시물 내용4")
                .build();
        userRepository.saveAll(List.of(user1, user2));
        postRepository.saveAll(List.of(post1, post2, post3, post4));
    }

    @DisplayName("게시물 엔티티 목록 조회")
    @Nested
    class FindAllTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            Pageable pageable = PageRequest.ofSize(10).withPage(0).withSort(Sort.by(Sort.Order.desc("id")));

            // when
            Page<PostEntity> postPage = postRepository.findAllByTitleContainingIgnoreCase(pageable, "");

            // then
            assertEquals(4, postPage.getTotalElements());
            assertEquals(1, postPage.getTotalPages());
        }

        @DisplayName("성공 - 제목으로 검색")
        @Test
        void success_byTitle() throws Exception {
            // given
            String keyword = "제목1";
            Pageable pageable = PageRequest.ofSize(10).withPage(0).withSort(Sort.by(Sort.Order.desc("id")));

            // when
            Page<PostEntity> postPage = postRepository.findAllByTitleContainingIgnoreCase(pageable, keyword);

            // then
            assertEquals(1, postPage.getTotalElements());
            assertEquals(1, postPage.getTotalPages());
            assertTrue(postPage.getContent().get(0).getTitle().contains(keyword));
        }

        @DisplayName("성공 - 내용으로 검색")
        @Test
        void success_byContent() throws Exception {
            // given
            String keyword = "내용3";
            Pageable pageable = PageRequest.ofSize(10).withPage(0).withSort(Sort.by(Sort.Order.desc("id")));

            // when
            Page<PostEntity> postPage = postRepository.findAllByContentContainingIgnoreCase(pageable, keyword);

            // then
            assertEquals(1, postPage.getTotalElements());
            assertEquals(1, postPage.getTotalPages());
            assertTrue(postPage.getContent().get(0).getContent().contains(keyword));
        }

        @DisplayName("성공 - 제목+내용으로 검색")
        @Test
        void success_byTitleAndContent() throws Exception {
            // given
            String keyword = "내용2";
            Pageable pageable = PageRequest.ofSize(10).withPage(0).withSort(Sort.by(Sort.Order.desc("id")));

            // when
            Page<PostEntity> postPage = postRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(pageable, keyword, keyword);

            // then
            assertEquals(1, postPage.getTotalElements());
            assertEquals(1, postPage.getTotalPages());
            assertTrue(postPage.getContent().get(0).getContent().contains(keyword));
        }

        @DisplayName("성공 - 작성자로 검색")
        @Test
        void success_byWriter() throws Exception {
            // given
            String keyword = user1.getUsername();
            Pageable pageable = PageRequest.ofSize(10).withPage(0).withSort(Sort.by(Sort.Order.desc("id")));

            // when
            Page<PostEntity> postPage = postRepository.findAllByUser_Username(pageable, keyword);

            // then
            assertEquals(3, postPage.getTotalElements());
            assertEquals(1, postPage.getTotalPages());
            for (PostEntity post : postPage) {
                assertEquals(user1, post.getUser());
            }
        }
    }
}