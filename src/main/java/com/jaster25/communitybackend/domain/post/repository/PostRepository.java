package com.jaster25.communitybackend.domain.post.repository;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
