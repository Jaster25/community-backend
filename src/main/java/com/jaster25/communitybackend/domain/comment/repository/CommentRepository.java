package com.jaster25.communitybackend.domain.comment.repository;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
