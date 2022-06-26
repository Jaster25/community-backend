package com.jaster25.communitybackend.config;

import com.jaster25.communitybackend.domain.user.domain.Role;
import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.config.security.UserAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

public class WithMockCustomAdminSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomAdmin> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomAdmin annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserEntity admin = UserEntity.builder()
                .id(UUID.fromString("b7b35ca2-93b6-4ae9-b505-cc32fc1674a9"))
                .username("admin1")
                .password("{bcrypt}$2a$10$bkyXPgVXuRXmRa9d34FI2OUAteBNZi0BN8EYkG.loOSK/D2CR9jYO")
                .build();
        admin.addRole(Role.ROLE_ADMIN);

        UserAdapter principal = new UserAdapter(admin);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
