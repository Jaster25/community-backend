package com.jaster25.communitybackend.domain.like.repository;

import com.jaster25.communitybackend.domain.like.domain.LikeCommentEntity;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeCommentEntity, Long> {

    int countByCommentId(Long commentId);

    Optional<LikeCommentEntity> findByUserAndCommentId(UserEntity user, Long commentId);
}
