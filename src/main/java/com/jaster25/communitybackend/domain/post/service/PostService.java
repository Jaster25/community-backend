package com.jaster25.communitybackend.domain.post.service;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.dto.PostDetailResponseDto;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthenticatedException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostDetailResponseDto createPost(UserEntity user, PostRequestDto postRequestDto) {
        PostEntity post = PostEntity.builder()
                .user(user)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .build();
        postRepository.save(post);
        return PostDetailResponseDto.of(post);
    }

    @Transactional
    public PostDetailResponseDto getPost(Long postId, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));

        post.addViewCount();
        return PostDetailResponseDto.of(post);
    }

    @Transactional
    public PostDetailResponseDto updatePost(Long postId, UserEntity user, PostRequestDto postRequestDto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));

        verifyAuthorization(post, user);
        post.update(postRequestDto.getTitle(), postRequestDto.getContent());
        return PostDetailResponseDto.of(post);
    }

    @Transactional
    public void deletePost(Long postId, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));

        verifyAuthorization(post, user);
        postRepository.delete(post);
    }

    private void verifyAuthorization(PostEntity post, UserEntity user) {
        if (Objects.isNull(user)) {
            throw new UnAuthenticatedException(ErrorCode.NONEXISTENT_AUTHENTICATION);
        } else if (!user.equals(post.getUser()) && !user.isAdmin()) {
            throw new UnAuthorizedException(ErrorCode.NONEXISTENT_AUTHORIZATION);
        }
    }
}
