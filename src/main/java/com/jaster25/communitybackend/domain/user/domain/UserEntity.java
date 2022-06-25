package com.jaster25.communitybackend.domain.user.domain;

import com.jaster25.communitybackend.global.common.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id", "username", "password"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private final Set<Role> roles = new HashSet<>();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles.add(Role.ROLE_USER);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }
}
