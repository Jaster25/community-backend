package com.jaster25.communitybackend.global.config;


import com.jaster25.communitybackend.global.config.security.CustomAccessDeniedHandler;
import com.jaster25.communitybackend.global.config.security.CustomAuthenticationEntryPoint;
import com.jaster25.communitybackend.global.config.security.jwt.JwtSecurityConfig;
import com.jaster25.communitybackend.global.config.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final String PREFIX_URL = "/api/v1";

        http.cors();
        http.csrf().disable();
        http.httpBasic().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.formLogin().disable()
                .logout().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);

        http.apply(new JwtSecurityConfig(tokenProvider));

        http.authorizeRequests()
                // User
                .antMatchers(PREFIX_URL + "/auth/logout").authenticated()
                .antMatchers(PREFIX_URL + "/users/me/profile-image").authenticated()
                // Post
                .antMatchers(HttpMethod.POST, PREFIX_URL + "/posts").authenticated()
                .antMatchers(HttpMethod.PUT, PREFIX_URL + "/posts/**").authenticated()
                .antMatchers(HttpMethod.DELETE, PREFIX_URL + "/posts/**").authenticated()
                // Comment
                .antMatchers(HttpMethod.POST, PREFIX_URL + "/comments").authenticated()
                .antMatchers(HttpMethod.PUT, PREFIX_URL + "/comments/**").authenticated()
                .antMatchers(HttpMethod.DELETE, PREFIX_URL + "/comments/**").authenticated()
                // Like
                .antMatchers(HttpMethod.POST, PREFIX_URL + "/likes/**").authenticated()
                .antMatchers(HttpMethod.DELETE, PREFIX_URL + "/likes/**").authenticated()
                .anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
