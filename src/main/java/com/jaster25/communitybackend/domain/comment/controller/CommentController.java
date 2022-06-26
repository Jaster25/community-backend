package com.jaster25.communitybackend.domain.comment.controller;

import com.jaster25.communitybackend.domain.comment.dto.CommentRequestDto;
import com.jaster25.communitybackend.domain.comment.dto.CommentResponseDto;
import com.jaster25.communitybackend.domain.comment.service.CommentService;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponseDto> createCommentApi(@PathVariable Long postId,
                                                               @CurrentUser UserEntity user,
                                                               @RequestParam(name = "parentId", required = false) Long parentId,
                                                               @Valid @RequestBody CommentRequestDto commentRequestDto) {
        CommentResponseDto commentResponseDto = parentId == null
                ? commentService.createComment(postId, user, commentRequestDto)
                : commentService.createComment(postId, user, parentId, commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }
}
