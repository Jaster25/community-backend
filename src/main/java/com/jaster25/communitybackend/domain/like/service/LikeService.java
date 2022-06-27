package com.jaster25.communitybackend.domain.like.service;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.comment.repository.CommentRepository;
import com.jaster25.communitybackend.domain.like.domain.LikeCommentEntity;
import com.jaster25.communitybackend.domain.like.domain.LikePostEntity;
import com.jaster25.communitybackend.domain.like.dto.LikeResponseDto;
import com.jaster25.communitybackend.domain.like.repository.LikeCommentRepository;
import com.jaster25.communitybackend.domain.like.repository.LikePostRepository;
import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import com.jaster25.communitybackend.domain.post.repository.PostRepository;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.DuplicatedValueException;
import com.jaster25.communitybackend.global.exception.custom.NonExistentException;
import com.jaster25.communitybackend.global.exception.custom.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikePostRepository likePostRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeResponseDto createLikePost(Long postId, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));
        if (likePostRepository.findByUserAndPost_Id(user, postId).orElse(null) != null) {
            throw new DuplicatedValueException(ErrorCode.DUPLICATED_LIKE_POST);
        }

        LikePostEntity likePost = LikePostEntity.builder()
                .user(user)
                .post(post)
                .build();
        likePostRepository.save(likePost);
        int likeCount = likePostRepository.countByPostId(postId);
        return LikeResponseDto.builder()
                .likeCount(likeCount)
                .isLiked(true)
                .build();
    }

    @Transactional
    public void deleteLikePost(Long postId, UserEntity user) {
        postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));
        LikePostEntity like = likePostRepository.findByUserAndPost_Id(user, postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_LIKE));

        if (!user.equals(like.getUser())) {
            throw new UnAuthorizedException(ErrorCode.NONEXISTENT_AUTHORIZATION);
        }

        likePostRepository.delete(like);
    }

    public LikeResponseDto getLikePostCount(Long postId, UserEntity user) {
        postRepository.findById(postId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_POST));

        int likeCount = likePostRepository.countByPostId(postId);
        boolean isLiked = likePostRepository.findByUserAndPost_Id(user, postId).orElse(null) != null;

        return LikeResponseDto.builder()
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }

    @Transactional
    public LikeResponseDto createLikeComment(Long commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_COMMENT));
        if (likeCommentRepository.findByUserAndComment_Id(user, commentId).orElse(null) != null) {
            throw new DuplicatedValueException(ErrorCode.DUPLICATED_LIKE_COMMENT);
        }

        LikeCommentEntity likeComment = LikeCommentEntity.builder()
                .user(user)
                .comment(comment)
                .build();
        likeCommentRepository.save(likeComment);
        int likeCount = likeCommentRepository.countByCommentId(commentId);
        return LikeResponseDto.builder()
                .likeCount(likeCount)
                .isLiked(true)
                .build();
    }

    @Transactional
    public void deleteLikeComment(Long commentId, UserEntity user) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_COMMENT));
        LikeCommentEntity like = likeCommentRepository.findByUserAndComment_Id(user, commentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_LIKE));

        if (!user.equals(like.getUser())) {
            throw new UnAuthorizedException(ErrorCode.NONEXISTENT_AUTHORIZATION);
        }

        likeCommentRepository.delete(like);
    }

    public LikeResponseDto getLikeCommentCount(Long commentId, UserEntity user) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NonExistentException(ErrorCode.NONEXISTENT_COMMENT));

        int likeCount = likeCommentRepository.countByCommentId(commentId);
        boolean isLiked = likeCommentRepository.findByUserAndComment_Id(user, commentId).orElse(null) != null;

        return LikeResponseDto.builder()
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}
