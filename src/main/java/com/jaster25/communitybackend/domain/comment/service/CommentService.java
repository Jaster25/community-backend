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
import com.jaster25.communitybackend.global.exception.custom.UnAuthenticatedException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    @Transactional
    public CommentResponseDto updateComment(Long commentId, UserEntity user, CommentRequestDto commentRequestDto) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_COMMENT));

        verifyAuthorization(comment, user);
        comment.update(commentRequestDto.getContent());
        return CommentResponseDto.of(user, comment);
    }

    @Transactional
    public void deleteComment(Long commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_COMMENT));

        verifyAuthorization(comment, user);

        comment.delete();

        // 위로 올라가면서 제거
        while (comment != null && comment.isDeleted() && comment.getChildren().size() == 0) {
            CommentEntity parent = comment.getParent();
            if (parent != null) {
                parent.getChildren().remove(comment);
            }
            commentRepository.delete(comment);
            comment = parent;
        }
    }

    private void verifyAuthorization(CommentEntity comment, UserEntity user) {
        if (Objects.isNull(user)) {
            throw new UnAuthenticatedException(ErrorCode.NONEXISTENT_AUTHENTICATION);
        } else if (!user.equals(comment.getUser()) && !user.isAdmin()) {
            throw new UnAuthorizedException(ErrorCode.NONEXISTENT_AUTHORIZATION);
        }
    }
}
