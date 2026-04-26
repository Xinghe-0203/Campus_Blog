package com.example.edu_project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT 工具类
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Token黑名单：存储已撤销的Token
     * TODO [高优先级]: 生产环境应使用 Redis 实现分布式 Token 黑名单
     * 当前内存存储方案仅适用于单实例部署，多实例部署时 Token revocation 无效
     * 建议方案：使用 Redis SET 存储 token，设置与 token 剩余有效期一致的 TTL 自动过期
     */
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    /**
     * 生成 Token（包含角色）
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return createToken(claims, username, expiration);
    }

    /**
     * 生成刷新Token（用于获取新的访问Token）
     */
    public String generateRefreshToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "refresh"); // 标记为刷新Token
        return createToken(claims, username, refreshExpiration);
    }

    /**
     * 验证刷新Token是否有效
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    private String createToken(Map<String, Object> claims, String subject, Long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * 将Token加入黑名单（撤销Token）
     */
    public void revokeToken(String token) {
        if (token == null) {
            return;
        }
        try {
            // 必须先验证 Token 签名，确保是有效的 token 才能加入黑名单
            parseToken(token);
            if (!isTokenExpired(token)) {
                tokenBlacklist.add(token);
            }
        } catch (Exception e) {
            // 无效 token 不加入黑名单
        }
    }

    /**
     * 检查Token是否已被撤销
     */
    public boolean isTokenRevoked(String token) {
        return tokenBlacklist.contains(token);
    }

    /**
     * 清理过期Token黑名单
     * 注意：生产环境应使用Redis并设置TTL自动过期
     */
    public void cleanExpiredTokens() {
        tokenBlacklist.removeIf(token -> {
            try {
                return isTokenExpired(token);
            } catch (Exception e) {
                // 无效Token直接移除
                return true;
            }
        });
    }

    /**
     * 从请求中提取 Token
     * @param request HTTP请求
     * @return Token字符串，如果不存在返回null
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true; // 已过期
        } catch (io.jsonwebtoken.JwtException e) {
            // 其他 JWT 解析错误（格式错误、签名验证失败等）不算过期，而是无效
            // 这里返回 false 让调用方通过 parseToken 的结果判断
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取用户角色
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
