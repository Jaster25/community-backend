package com.jaster25.communitybackend.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaster25.communitybackend.config.WithMockCustomAdmin;
import com.jaster25.communitybackend.config.WithMockCustomUser;
import com.jaster25.communitybackend.domain.user.domain.Role;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String PREFIX_URL = "/api/v1/auth";
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

    @DisplayName("로그인 한 사용자 조회 API")
    @Nested
    class GetAuthApiTest {
        @DisplayName("성공")
        @Test
        @WithMockCustomUser
        void success() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("user1"))
                    .andExpect(jsonPath("$.roles").value(Role.ROLE_USER.toString()));
        }

        @DisplayName("성공 - 관리자")
        @Test
        @WithMockCustomAdmin
        void success_admin() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("admin1"))
                    .andExpect(jsonPath("$.roles.size()").value(2))
                    .andExpect(jsonPath("$.roles[0]").value(Role.ROLE_USER.toString()))
                    .andExpect(jsonPath("$.roles[1]").value(Role.ROLE_ADMIN.toString()));
        }

        @DisplayName("성공 - 비로그인")
        @Test
        void success_notLoggedIn() throws Exception {
            // given
            // when
            ResultActions result = mvc.perform(get(PREFIX_URL));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").doesNotExist())
                    .andExpect(jsonPath("$.roles").doesNotExist());
        }
    }
}