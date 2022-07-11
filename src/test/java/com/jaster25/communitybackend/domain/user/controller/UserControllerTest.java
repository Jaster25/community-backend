package com.jaster25.communitybackend.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaster25.communitybackend.config.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String PREFIX_URL = "/api/v1/users";
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

    @DisplayName("프로필 이미지 업데이트 API")
    @Nested
    class UpdateProfileImageApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-image.png",
                    "image/png",
                    "test-image".getBytes());

            MockMultipartHttpServletRequestBuilder builder =
                    multipart(PREFIX_URL + "/me/profile-image");

            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            // when
            ResultActions result = mvc.perform(builder
                    .file(mockFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("user1"))
                    .andExpect(jsonPath("$.profileImageUrl").exists());
        }

        @DisplayName("실패 - 비로그인")
        @Test
        void failure_notLoggedIn() throws Exception {
            // given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-image.png",
                    "image/png",
                    "test-image".getBytes());

            MockMultipartHttpServletRequestBuilder builder =
                    multipart(PREFIX_URL + "/me/profile-image");

            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            // when
            ResultActions result = mvc.perform(builder
                    .file(mockFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @DisplayName("실패 - 파일 미첨부")
        @Test
        @WithMockCustomUser
        void failure_emptyFile() throws Exception {
            // given
            MockMultipartHttpServletRequestBuilder builder =
                    multipart(PREFIX_URL + "/me/profile-image");

            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            // when
            ResultActions result = mvc.perform(builder
                    .contentType(MediaType.MULTIPART_FORM_DATA));

            // then
            result.andExpect(status().isBadRequest());
        }
    }
}