package com.jaster25.communitybackend.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaster25.communitybackend.config.WithMockCustomAdmin;
import com.jaster25.communitybackend.config.WithMockCustomUser;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    private static final String PREFIX_URL = "/api/v1/posts";
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

    @DisplayName("게시물 생성 API")
    @Nested
    class CreatePostApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .title("게시물 제목")
                    .content("게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.title").value("게시물 제목"))
                    .andExpect(jsonPath("$.content").value("게시물 내용"))
                    .andExpect(jsonPath("$.viewCount").value("0"))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .title("게시물 제목")
                    .content("게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 - 유효성(제목)")
        @Test
        @WithMockCustomUser
        void failure_invalid_title() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .content("게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(post(PREFIX_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("P101"));
        }
    }

    @DisplayName("게시물 목록 조회 API")
    @Nested
    class GetPostsApiTest {
        @DisplayName("성공")
        @Test
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.totalPage").value("1"))
                    .andExpect(jsonPath("$.page.totalElement").value("3"))
                    .andExpect(jsonPath("$.posts").exists())
                    .andExpect(jsonPath("$.posts.size()").value("3"));
        }

        @DisplayName("성공 - 제목으로 검색")
        @Test
        void success_byTitle() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "?searchType=title&keyword=제목1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.totalPage").value("1"))
                    .andExpect(jsonPath("$.page.totalElement").value("1"))
                    .andExpect(jsonPath("$.posts").exists())
                    .andExpect(jsonPath("$.posts[0].title").value("게시물 제목1"));
        }

        @DisplayName("성공 - 내용으로 검색")
        @Test
        void success_byContent() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "?searchType=content&keyword=내용2"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.totalPage").value("1"))
                    .andExpect(jsonPath("$.page.totalElement").value("1"))
                    .andExpect(jsonPath("$.posts").exists())
                    .andExpect(jsonPath("$.posts.size()").value("1"));
        }

        @DisplayName("성공 - 제목+내용으로 검색")
        @Test
        void success_byTitleAndContent() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "?searchType=titleAndContent&keyword=내용3"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.totalPage").value("1"))
                    .andExpect(jsonPath("$.page.totalElement").value("1"))
                    .andExpect(jsonPath("$.posts").exists())
                    .andExpect(jsonPath("$.posts[0].title").value("게시물 제목3"));
        }

        @DisplayName("성공 - 작성자로 검색")
        @Test
        void success_byWriter() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "?searchType=writer&keyword=user1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.totalPage").value("1"))
                    .andExpect(jsonPath("$.page.totalElement").value("2"))
                    .andExpect(jsonPath("$.posts").exists())
                    .andExpect(jsonPath("$.posts.size()").value("2"));
        }
    }

    @DisplayName("게시물 상세 조회 API")
    @Nested
    class GetPostApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.title").value("게시물 제목1"))
                    .andExpect(jsonPath("$.content").value("게시물 내용1"))
                    .andExpect(jsonPath("$.viewCount").value("26"))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @DisplayName("실패 - 존재하지 않는 게시물 ID")
        @Test
        @WithMockCustomUser
        void failure_nonExistentId() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL + "/15"));

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
        }
    }

    @DisplayName("게시물 수정 API")
    @Nested
    class UpdatePostApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .title("수정된 게시물 제목")
                    .content("수정된 게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.title").value("수정된 게시물 제목"))
                    .andExpect(jsonPath("$.content").value("수정된 게시물 내용"))
                    .andExpect(jsonPath("$.viewCount").exists())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @DisplayName("성공 - 관리자")
        @Test
        @WithMockCustomAdmin
        void success_admin() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .title("수정된 게시물 제목")
                    .content("수정된 게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.writer").value("user1"))
                    .andExpect(jsonPath("$.title").value("수정된 게시물 제목"))
                    .andExpect(jsonPath("$.content").value("수정된 게시물 내용"))
                    .andExpect(jsonPath("$.viewCount").exists())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .title("수정된 게시물 제목")
                    .content("수정된 게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 - 다른 사용자")
        @Test
        @WithMockCustomUser
        void failure_otherUser() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .title("수정된 게시물 제목")
                    .content("수정된 게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isForbidden());
        }

        @DisplayName("실패 - 유효성(제목)")
        @Test
        @WithMockCustomUser
        void failure_invalid_title() throws Exception {
            // given
            PostRequestDto postRequestDto = PostRequestDto.builder()
                    .content("수정된 게시물 내용")
                    .build();

            // when
            ResultActions result = mvc.perform(put(PREFIX_URL + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(postRequestDto)));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("P101"));
        }
    }

    @DisplayName("게시물 삭제 API")
    @Nested
    class DeletePostApiTest {
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

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/1"));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 - 다른 사용자")
        @Test
        @WithMockCustomUser
        void failure_otherUser() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(delete(PREFIX_URL + "/3"));

            // then
            result.andExpect(status().isForbidden());
        }
    }
}