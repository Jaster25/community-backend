package com.jaster25.communitybackend.domain.comment.service;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.comment.dto.CommentRequestDto;
import com.jaster25.communitybackend.domain.comment.dto.CommentResponseDto;
import com.jaster25.communitybackend.domain.comment.dto.CommentsResponseDto;
import com.jaster25.communitybackend.domain.comment.repository.CommentRepository;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(Long postId, UserEntity user, CommentRequestDto commentRequestDto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));

        CommentEntity comment = CommentEntity.builder()
                .user(user)
                .post(post)
                .content(commentRequestDto.getContent())
                .build();
        commentRepository.save(comment);
        return CommentResponseDto.of(user, comment);
    }

    @Transactional
    public CommentResponseDto createComment(Long postId, UserEntity user, Long parentId, CommentRequestDto commentRequestDto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));
        CommentEntity parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_COMMENT));

        CommentEntity comment = CommentEntity.builder()
                .user(user)
                .post(post)
                .parent(parentComment)
                .content(commentRequestDto.getContent())
                .build();
        commentRepository.save(comment);
        return CommentResponseDto.of(user, comment);
    }

    public CommentsResponseDto getComments(Long postId, UserEntity user, Long lastCommentId, int size) {
        postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));

        Pageable pageable = PageRequest.ofSize(size).withPage(0);
        Page<CommentEntity> commentPage = lastCommentId == null
                ? commentRepository.findAllByPostIdAndParentIsNullOrderByIdDesc(pageable, postId)
                : commentRepository.findAllByPostIdAndIdLessThanAndParentIsNullOrderByIdDesc(pageable, postId, lastCommentId);

        List<CommentResponseDto> commentResponseDtoList = commentPage.stream()
                .map(c -> CommentResponseDto.of(user, c))
                .collect(Collectors.toList());
        return CommentsResponseDto.builder()
                .comments(commentResponseDtoList)
                .build();
    }
}
