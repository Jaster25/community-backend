package com.jaster25.communitybackend.domain.post.repository;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    // 제목 검색
    Page<PostEntity> findAllByTitleContainingIgnoreCase(Pageable pageable, String keyword);

    // 내용 검색
    Page<PostEntity> findAllByContentContainingIgnoreCase(Pageable pageable, String keyword);

    // 제목+내용 검색
    Page<PostEntity> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(Pageable pageable, String title, String content);

    // 작성자 검색
    Page<PostEntity> findAllByUser_Username(Pageable pageable, String username);
}
