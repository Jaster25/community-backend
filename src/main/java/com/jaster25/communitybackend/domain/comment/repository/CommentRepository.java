package com.jaster25.communitybackend.domain.comment.repository;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPostIdAndParentIsNullOrderByIdDesc(Pageable pageable, Long postId);

    Page<CommentEntity> findAllByPostIdAndIdLessThanAndParentIsNullOrderByIdDesc(Pageable pageable, Long postId, Long id);
}
