package com.jaster25.communitybackend.domain.post.controller;

import com.jaster25.communitybackend.domain.post.dto.PostDetailResponseDto;
import com.jaster25.communitybackend.domain.post.dto.PostRequestDto;
import com.jaster25.communitybackend.domain.post.dto.PostsResponseDto;
import com.jaster25.communitybackend.domain.post.service.PostService;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetailResponseDto> createPostApi(@CurrentUser UserEntity user,
                                                               @Valid @RequestBody PostRequestDto postRequestDto) {
        PostDetailResponseDto postDetailResponseDto = postService.createPost(user, postRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDetailResponseDto);
    }

    @GetMapping
    public ResponseEntity<PostsResponseDto> getPostsApi(@RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @RequestParam(value = "searchType", defaultValue = "title") String searchType,
                                                        @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        PostsResponseDto postsResponseDto = postService.getPosts(page, size, searchType, keyword);
        return ResponseEntity.status(HttpStatus.OK).body(postsResponseDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPostApi(@PathVariable Long postId,
                                                            @CurrentUser UserEntity user) {
        PostDetailResponseDto postDetailResponseDto = postService.getPost(postId, user);
        return ResponseEntity.status(HttpStatus.OK).body(postDetailResponseDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDto> updatePostApi(@PathVariable Long postId,
                                                               @CurrentUser UserEntity user,
                                                               @Valid @RequestBody PostRequestDto postRequestDto) {
        PostDetailResponseDto postDetailResponseDto = postService.updatePost(postId, user, postRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(postDetailResponseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePostApi(@PathVariable Long postId,
                                              @CurrentUser UserEntity user) {
        postService.deletePost(postId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
