package com.jaster25.communitybackend.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaster25.communitybackend.config.WithMockCustomUser;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    private MockMvc mvc;
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final String PREFIX_URL = "/api/v1/posts";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("게시물 생성 API 성공")
    @Test
    @WithMockCustomUser
    void createPostApi() throws Exception {
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

    @DisplayName("게시물 생성 API 실패 - 비로그인")
    @Test
    void createPostApi_notLoggedIn() throws Exception {
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

    @DisplayName("게시물 생성 API 실패 - 유효성(제목)")
    @Test
    @WithMockCustomUser
    void createPostApi_invalid_title() throws Exception {
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