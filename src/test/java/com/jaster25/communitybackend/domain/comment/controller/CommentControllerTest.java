package com.jaster25.communitybackend.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaster25.communitybackend.config.WithMockCustomAdmin;
import com.jaster25.communitybackend.config.WithMockCustomUser;
import com.jaster25.communitybackend.domain.comment.dto.CommentRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CommentControllerTest {

    private static final String PREFIX_URL = "/api/v1/comments";
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

    @DisplayName("댓글 생성 API")
    @Nested
    class CreateCommentApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.content").value("댓글 내용1"));
        }

        @DisplayName("성공 - 대댓글")
        @Test
        @WithMockCustomUser
        void success_childParent() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("대댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.content").value("대댓글 내용1"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 - 유효성(내용)")
        @Test
        @WithMockCustomUser
        void failure_invalid_content() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C101"));
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        @WithMockCustomUser
        void failure_nonExistentId() throws Exception {
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL + "/15")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
        }
    }

    @DisplayName("댓글 목록 조회 API")
    @Nested
    class GetCommentsApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/1"));
            
            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.comments.size()").value("3"))
                    .andExpect(jsonPath("$.comments[0].canDelete").value("true"))
                    .andExpect(jsonPath("$.comments[0].likeCount").value("0"))
                    .andExpect(jsonPath("$.comments[1].canDelete").value("false"))
                    .andExpect(jsonPath("$.comments[1].children.size()").value("2"))
                    .andExpect(jsonPath("$.comments[2].canDelete").value("true"))
                    .andExpect(jsonPath("$.comments[-1].likeCount").value("2"));
        }

        @DisplayName("성공 - 비로그인")
        @Test
        void success_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.comments.size()").value("3"))
                    .andExpect(jsonPath("$.comments[0].canDelete").value("false"))
                    .andExpect(jsonPath("$.comments[1].canDelete").value("false"))
                    .andExpect(jsonPath("$.comments[2].canDelete").value("false"));
        }

        @DisplayName("성공 - 관리자")
        @Test
        @WithMockCustomAdmin
        void success_admin() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.comments.size()").value("3"))
                    .andExpect(jsonPath("$.comments[0].canDelete").value("true"))
                    .andExpect(jsonPath("$.comments[1].canDelete").value("true"))
                    .andExpect(jsonPath("$.comments[2].canDelete").value("true"));
        }
    }

    @DisplayName("댓글 수정 API")
    @Nested
    class UpdateCommentApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("수정된 댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.content").value("수정된 댓글 내용1"));
        }

        @DisplayName("성공 - 관리자")
        @Test
        @WithMockCustomAdmin
        void success_admin() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("수정된 댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.content").value("수정된 댓글 내용1"));
        }

        @DisplayName("실패 - 다른 작성자")
        @Test
        @WithMockCustomUser
        void failure_otherUser() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("수정된 댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("G003"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .content("수정된 댓글 내용1")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("댓글 삭제 API")
    @Nested
    class DeleteCommentApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isNoContent());
        }

        @DisplayName("성공 - 관리자")
        @Test
        @WithMockCustomAdmin
        void success_admin() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isNoContent());
        }

        @DisplayName("실패 - 다른 작성자")
        @Test
        @WithMockCustomUser
        void failure_otherUser() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/2"));

            // then
            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("G003"));
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }
}