package com.jaster25.communitybackend.domain.user.service;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.domain.user.dto.AuthResponseDto;
import com.jaster25.communitybackend.domain.user.dto.LogInRequestDto;
import com.jaster25.communitybackend.domain.user.dto.TokenResponseDto;
import com.jaster25.communitybackend.global.config.security.UserAdapter;
import com.jaster25.communitybackend.global.config.security.jwt.TokenProvider;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthResponseDto getAuth(UserEntity user) {
        return AuthResponseDto.builder()
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(String::valueOf)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    public TokenResponseDto logIn(LogInRequestDto logInRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = null;
        Authentication authentication = null;
        UserEntity user = null;
        try {
            user = UserEntity.builder()
                    .username(logInRequestDto.getId())
                    .password(logInRequestDto.getPassword())
                    .build();
            authenticationToken = new UsernamePasswordAuthenticationToken(new UserAdapter(user), logInRequestDto.getPassword());
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new InvalidValueException(ErrorCode.LOGIN_FAILURE);
        }

        String accessToken = tokenProvider.createAccessToken(authentication);
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }

}
