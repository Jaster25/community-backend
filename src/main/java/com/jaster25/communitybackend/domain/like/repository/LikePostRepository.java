package com.jaster25.communitybackend.domain.like.repository;

import com.jaster25.communitybackend.domain.like.domain.LikePostEntity;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePostEntity, Long> {

    int countByPostId(Long postId);

    Optional<LikePostEntity> findByUserAndPost_Id(UserEntity user, Long postId);
}
