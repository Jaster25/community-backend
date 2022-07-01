package com.jaster25.communitybackend.domain.like.controller;

import com.jaster25.communitybackend.domain.like.dto.LikeResponseDto;
import com.jaster25.communitybackend.domain.like.service.LikeService;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 게시물 좋아요
    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeResponseDto> createLikePostApi(@PathVariable Long postId,
                                                             @CurrentUser UserEntity user) {
        LikeResponseDto likeResponseDto = likeService.createLikePost(postId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDto);
    }

    // 게시물 좋아요 취소
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deleteLikePostApi(@PathVariable Long postId,
                                                  @CurrentUser UserEntity user) {
        likeService.deleteLikePost(postId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 게시물 좋아요 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<LikeResponseDto> getLikePostCountApi(@PathVariable Long postId,
                                                               @CurrentUser UserEntity user) {
        LikeResponseDto likeResponseDto = likeService.getLikePostCount(postId, user);
        return ResponseEntity.status(HttpStatus.OK).body(likeResponseDto);
    }

    // 댓글 좋아요
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<LikeResponseDto> createLikeCommentApi(@PathVariable Long commentId,
                                                                @CurrentUser UserEntity user) {
        LikeResponseDto likeResponseDto = likeService.createLikeComment(commentId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDto);
    }


    // 댓글 좋아요 취소
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteLikeCommentApi(@PathVariable Long commentId,
                                                     @CurrentUser UserEntity user) {
        likeService.deleteLikeComment(commentId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 댓글 좋아요 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<LikeResponseDto> getLikeCommentCountApi(@PathVariable Long commentId,
                                                                  @CurrentUser UserEntity user) {
        LikeResponseDto likeResponseDto = likeService.getLikeCommentCount(commentId, user);
        return ResponseEntity.status(HttpStatus.OK).body(likeResponseDto);
    }
}
