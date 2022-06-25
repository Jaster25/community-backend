package com.jaster25.communitybackend.domain.post.domain;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString(exclude = {"user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public PostEntity(UserEntity user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        updatedAt = LocalDateTime.now();
    }

    public void addViewCount() {
        viewCount++;
    }
}
