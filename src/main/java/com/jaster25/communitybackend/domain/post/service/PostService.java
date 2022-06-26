package com.jaster25.communitybackend.domain.post.service;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.dto.*;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthenticatedException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public PostsResponseDto getPosts(int page, int size, String searchType, String keyword) {
        Pageable pageable = PageRequest.ofSize(size).withPage(page - 1).withSort(Sort.by(Sort.Order.desc("id")));
        Page<PostEntity> postPage;

        if (Objects.equals(searchType, "title")) {
            postPage = postRepository.findAllByTitleContainingIgnoreCase(pageable, keyword);
        } else if (Objects.equals(searchType, "content")) {
            postPage = postRepository.findAllByContentContainingIgnoreCase(pageable, keyword);
        } else if (Objects.equals(searchType, "writer")) {
            postPage = postRepository.findAllByUser_Username(pageable, keyword);
        } else {
            postPage = postRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(pageable, keyword, keyword);
        }

        System.out.println("postPage.getContent() = " + postPage.getContent());

        List<PostResponseDto> postResponseDtoList = postPage.stream()
                .map(PostResponseDto::of)
                .collect(Collectors.toList());
        PageResponseDto pageResponseDto = PageResponseDto.builder()
                .totalPage(postPage.getTotalPages())
                .totalElement(postPage.getTotalElements())
                .build();

        return PostsResponseDto.builder()
                .page(pageResponseDto)
                .posts(postResponseDtoList)
                .build();
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
