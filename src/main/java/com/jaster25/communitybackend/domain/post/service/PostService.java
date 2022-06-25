package com.jaster25.communitybackend.domain.post.service;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.dto.PostDetailResponseDto;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
