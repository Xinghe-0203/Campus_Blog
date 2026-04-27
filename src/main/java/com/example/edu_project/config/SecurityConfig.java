package com.example.edu_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * CORS 允许的来源列表
     * 可通过环境变量 CORS_ALLOWED_ORIGINS 配置，多个来源用逗号分隔
     * 示例: http://localhost:8080,http://127.0.0.1:8080,https://campus-blog.com
     */
    @Value("${cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080}")
    private String allowedOrigins;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 从环境变量读取 CORS 来源配置
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        List<String> validatedOrigins = new ArrayList<>();
        for (String origin : origins) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                // 验证 origin 格式，拒绝过于宽泛的 pattern
                if (trimmed.endsWith("*")) {
                    log.warn("CORS 配置包含通配符，已被拒绝: {}", trimmed);
                    continue; // 拒绝通配符，只允许具体来源
                }
                validatedOrigins.add(trimmed);
                configuration.addAllowedOriginPattern(trimmed);
            }
        }
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Refresh-Token");
        configuration.addAllowedHeader("X-Requested-With");
        configuration.addAllowedMethod(HttpMethod.GET);
        configuration.addAllowedMethod(HttpMethod.POST);
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.addAllowedMethod(HttpMethod.DELETE);
        configuration.addAllowedMethod(HttpMethod.OPTIONS);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable())
                .frameOptions(frameOptions -> frameOptions.deny())
                .xssProtection(xss -> xss.disable())
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/user/register", "/user/login", "/user/refresh", "/user/send-code", "/user/reset-password").permitAll()
                .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                .requestMatchers("/admin/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/post/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/comment/post/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/like/check/**", "/collect/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tag/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/follow/check/**").permitAll()
                .requestMatchers("/follow/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/post/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/post/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/post/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
