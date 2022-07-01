package com.jaster25.communitybackend.domain.like.domain;

import com.jaster25.communitybackend.domain.post.domain.PostEntity;
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
@Table(name = "like_post")
public class LikePostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Builder
    public LikePostEntity(Long id, UserEntity user, PostEntity post) {
        this.id = id;
        this.user = user;
        this.post = post;
    }
}
