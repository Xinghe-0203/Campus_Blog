package com.example.edu_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF：前后端分离项目 JWT 无状态认证不需要 CSRF 防护
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // 公开接口：用户注册、登录、文档
                .requestMatchers("/api/user/register", "/api/user/login", "/api/doc.html", "/api/swagger-ui/**", "/api/v3/api-docs/**", "/api/swagger-resources/**", "/api/webjars/**").permitAll()
                // 文章公开接口：列表、详情、阅读量
                .requestMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                // 文章管理接口需要认证（发布、更新、删除）
                .requestMatchers(HttpMethod.POST, "/api/post/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/post/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/post/**").authenticated()
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
