package com.jaster25.communitybackend.config;

import com.jaster25.communitybackend.domain.user.domain.UserEntity;
import com.jaster25.communitybackend.global.config.security.UserAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserAdapter principal =
                new UserAdapter(UserEntity.builder()
                        .id(UUID.fromString("b5368610-f48d-49a2-945c-74fc17890b14"))
                        .username("user1")
                        .password("{bcrypt}$2a$10$sak6HBjaDTzojNAqoyWN5uk/h6futsWbUUSYOttdkATBlTzbYyj6O")
                        .build());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
