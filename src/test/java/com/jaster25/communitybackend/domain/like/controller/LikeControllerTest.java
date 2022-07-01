package com.jaster25.communitybackend.domain.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaster25.communitybackend.config.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class LikeControllerTest {

    private static final String PREFIX_URL = "/api/v1/likes";
    private MockMvc mvc;
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("게시물 좋아요 생성 API")
    @Nested
    class CreateLikePostApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/posts/2"));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isLiked").value("true"))
                    .andExpect(jsonPath("$.likeCount").value("1"));
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        @WithMockCustomUser
        void failure_nonExistentPostId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/posts/20"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
        }

        @DisplayName("실패 - 이미 좋아요 누른 게시물")
        @Test
        @WithMockCustomUser
        void failure_alreadyLiked() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/posts/1"));

            // then
            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("L101"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/posts/1"));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("게시물 좋아요 삭제 API")
    @Nested
    class DeleteLikePostApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/posts/1"));

            // then
            result.andExpect(status().isNoContent());
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        @WithMockCustomUser
        void failure_nonExistentPostId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/posts/13"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
        }

        @DisplayName("실패 - 좋아요 누르지 않은 게시물")
        @Test
        @WithMockCustomUser
        void failure_notLikedPost() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/posts/2"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("L001"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/posts/1"));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("게시물 좋아요 조회 API")
    @Nested
    class GetLikePostApiTest {
        @DisplayName("성공 - 로그인")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/posts/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isLiked").value("true"))
                    .andExpect(jsonPath("$.likeCount").value("2"));
        }

        @DisplayName("성공 - 비로그인")
        @Test
        void success_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/posts/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isLiked").value("false"))
                    .andExpect(jsonPath("$.likeCount").value("2"));
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/posts/13"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
        }
    }

    @DisplayName("댓글 좋아요 생성 API")
    @Nested
    class CreateLikeCommentApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/comments/2"));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isLiked").value("true"))
                    .andExpect(jsonPath("$.likeCount").value("1"));
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        @WithMockCustomUser
        void failure_nonExistentPostId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/comments/20"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("C001"));
        }

        @DisplayName("실패 - 이미 좋아요 누른 댓글")
        @Test
        @WithMockCustomUser
        void failure_alreadyLiked() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/comments/1"));

            // then
            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("L102"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/comments/1"));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("댓글 좋아요 삭제 API")
    @Nested
    class DeleteLikeCommentApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/comments/1"));

            // then
            result.andExpect(status().isNoContent());
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        @WithMockCustomUser
        void failure_nonExistentPostId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/comments/13"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("C001"));
        }

        @DisplayName("실패 - 좋아요 누르지 않은 댓글")
        @Test
        @WithMockCustomUser
        void failure_notLikedPost() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/comments/2"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("L002"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/comments/1"));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("댓글 좋아요 조회 API")
    @Nested
    class GetLikeCommentApiTest {
        @DisplayName("성공 - 로그인")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/comments/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isLiked").value("true"))
                    .andExpect(jsonPath("$.likeCount").value("2"));
        }

        @DisplayName("성공 - 비로그인")
        @Test
        void success_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/comments/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isLiked").value("false"))
                    .andExpect(jsonPath("$.likeCount").value("2"));
        }

        @DisplayName("실패 - 존재하지 않는 댓글 ID")
        @Test
        void failure_nonExistentPostId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/comments/13"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("C001"));
        }
    }
}