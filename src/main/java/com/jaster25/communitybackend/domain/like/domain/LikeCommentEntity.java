package com.jaster25.communitybackend.domain.like.domain;

import com.jaster25.communitybackend.domain.comment.domain.CommentEntity;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "like_comment")
public class LikeCommentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @Builder
    public LikeCommentEntity(Long id, UserEntity user, CommentEntity comment) {
        this.id = id;
        this.user = user;
        this.comment = comment;
    }
}