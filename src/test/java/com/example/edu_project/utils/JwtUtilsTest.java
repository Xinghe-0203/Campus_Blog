package com.example.edu_project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtils 单元测试
 * 测试Token生成、验证、刷新等功能
 */
@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ROLE = "user";

    // 设置 JWT 配置（因为测试环境可能没有 .env 文件）
    private static final String TEST_SECRET = "this_is_a_very_long_secret_key_for_testing_at_least_32_chars";
    private static final Long TEST_EXPIRATION = 3600000L; // 1 hour
    private static final Long TEST_REFRESH_EXPIRATION = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        // 设置 JWT 配置
        ReflectionTestUtils.setField(jwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", TEST_EXPIRATION);
        ReflectionTestUtils.setField(jwtUtils, "refreshExpiration", TEST_REFRESH_EXPIRATION);
        // 清理黑名单
        jwtUtils.cleanExpiredTokens();
    }

    // ==================== Token生成测试 ====================

    @Test
    @DisplayName("生成访问Token成功")
    void testGenerateToken() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT 格式：header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("生成刷新Token成功")
    void testGenerateRefreshToken() {
        String refreshToken = jwtUtils.generateRefreshToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertEquals(3, refreshToken.split("\\.").length);
    }

    @Test
    @DisplayName("访问Token和刷新Token不同")
    void testAccessTokenAndRefreshTokenDifferent() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);
        String refreshToken = jwtUtils.generateRefreshToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertNotEquals(token, refreshToken);
    }

    // ==================== Token验证测试 ====================

    @Test
    @DisplayName("验证有效Token成功")
    void testValidateValidToken() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 验证 Token 有效性
        assertDoesNotThrow(() -> {
            Claims claims = jwtUtils.parseToken(token);
            assertNotNull(claims);
        });

        // 验证 Token 未过期
        assertFalse(jwtUtils.isTokenExpired(token));

        // 验证 Token 未被撤销
        assertFalse(jwtUtils.isTokenRevoked(token));
    }

    @Test
    @DisplayName("验证过期Token失败")
    void testValidateExpiredToken() {
        // 创建一个过期的 token（使用较短的有效期）
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L); // 1ms，过期
        String expiredToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 等待 token 过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证过期 token
        assertTrue(jwtUtils.isTokenExpired(expiredToken));

        // 解析过期 token 会抛出 ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.parseToken(expiredToken);
        });
    }

    @Test
    @DisplayName("验证无效Token（格式错误）")
    void testValidateInvalidToken_FormatError() {
        String invalidToken = "invalid.token.format";

        assertThrows(JwtException.class, () -> {
            jwtUtils.parseToken(invalidToken);
        });
    }

    @Test
    @DisplayName("验证伪造Token（签名错误）")
    void testValidateForgedToken() {
        // 使用不同的密钥生成token
        String differentSecret = "completely_different_secret_key_that_is_at_least_32_chars_long";
        ReflectionTestUtils.setField(jwtUtils, "secret", differentSecret);
        String forgedToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 恢复正确的密钥
        ReflectionTestUtils.setField(jwtUtils, "secret", TEST_SECRET);

        // 验证伪造的token会失败
        assertThrows(JwtException.class, () -> {
            jwtUtils.parseToken(forgedToken);
        });
    }

    @Test
    @DisplayName("验证空Token")
    void testValidateNullToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.parseToken(null);
        });
    }

    // ==================== Token黑名单测试 ====================

    @Test
    @DisplayName("Token 未撤销状态检查")
    void testIsTokenRevoked_NotRevoked() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 新生成的 token 不应在黑名单中
        assertFalse(jwtUtils.isTokenRevoked(token));
    }

    @Test
    @DisplayName("Token 已撤销状态检查")
    void testIsTokenRevoked_Revoked() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 撤销 token
        jwtUtils.revokeToken(token);

        // 验证 token 已被撤销
        assertTrue(jwtUtils.isTokenRevoked(token));
    }

    @Test
    @DisplayName("撤销已过期的Token")
    void testRevokeExpiredToken() {
        // 创建一个即将过期的 token
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L);
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 撤销过期的token（不应该加入黑名单）
        jwtUtils.revokeToken(token);

        // 过期的token不在黑名单中（因为已过期，不会被加入）
        assertTrue(jwtUtils.isTokenExpired(token));
    }

    @Test
    @DisplayName("撤销null Token")
    void testRevokeNullToken() {
        // 不应抛出异常
        assertDoesNotThrow(() -> {
            jwtUtils.revokeToken(null);
        });
    }

    @Test
    @DisplayName("清理过期Token黑名单")
    void testCleanExpiredTokens() {
        // 创建两个token，一个立即过期
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L);
        String expiredToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        ReflectionTestUtils.setField(jwtUtils, "expiration", TEST_EXPIRATION);
        String validToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 撤销两个token
        jwtUtils.revokeToken(expiredToken);
        jwtUtils.revokeToken(validToken);

        // 等待第一个token过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 清理过期token
        jwtUtils.cleanExpiredTokens();

        // 有效的token仍在黑名单中
        assertTrue(jwtUtils.isTokenRevoked(validToken));
    }

    // ==================== Token刷新测试 ====================

    @Test
    @DisplayName("刷新Token成功")
    void testRefreshToken() {
        // 生成刷新 token
        String refreshToken = jwtUtils.generateRefreshToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());

        // 验证是刷新 token
        assertTrue(jwtUtils.isRefreshToken(refreshToken));

        // 生成新的访问 token
        String newToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertNotNull(newToken);
        assertFalse(newToken.equals(refreshToken));

        // 新 token 不应该是刷新 token
        assertFalse(jwtUtils.isRefreshToken(newToken));
    }

    @Test
    @DisplayName("判断非刷新Token")
    void testIsRefreshToken_False() {
        String accessToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertFalse(jwtUtils.isRefreshToken(accessToken));
    }

    @Test
    @DisplayName("判断无效Token是否为刷新Token")
    void testIsRefreshToken_InvalidToken() {
        assertFalse(jwtUtils.isRefreshToken("invalid.token.here"));
    }

    // ==================== Token信息提取测试 ====================

    @Test
    @DisplayName("从 Token 中获取用户ID")
    void testGetUserIdFromToken() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        Long userId = jwtUtils.getUserIdFromToken(token);
        assertEquals(TEST_USER_ID, userId);
    }

    @Test
    @DisplayName("从 Token 中获取用户名")
    void testGetUsernameFromToken() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        String username = jwtUtils.getUsernameFromToken(token);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("从 Token 中获取用户角色")
    void testGetRoleFromToken() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        String role = jwtUtils.getRoleFromToken(token);
        assertEquals(TEST_ROLE, role);
    }

    @Test
    @DisplayName("从 Token 中获取管理员角色")
    void testGetRoleFromToken_Admin() {
        String adminToken = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, "admin");

        String role = jwtUtils.getRoleFromToken(adminToken);
        assertEquals("admin", role);
    }

    // ==================== Token过期检查测试 ====================

    @Test
    @DisplayName("未过期Token返回false")
    void testIsTokenExpired_False() {
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    @DisplayName("过期Token返回true")
    void testIsTokenExpired_True() {
        // 创建一个会立即过期的token
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L);
        String token = jwtUtils.generateToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(jwtUtils.isTokenExpired(token));
    }

    @Test
    @DisplayName("无效Token不视为过期")
    void testIsTokenExpired_InvalidToken() {
        assertFalse(jwtUtils.isTokenExpired("invalid.token.here"));
    }

    // ==================== 刷新Token过期时间测试 ====================

    @Test
    @DisplayName("刷新Token有效期检查（7天）")
    void testRefreshTokenExpiration() {
        String refreshToken = jwtUtils.generateRefreshToken(TEST_USER_ID, TEST_USERNAME, TEST_ROLE);

        // 刷新Token不应该立即过期
        assertFalse(jwtUtils.isTokenExpired(refreshToken));
    }

    @Test
    @DisplayName("不同用户的Token包含不同用户信息")
    void testDifferentUsersHaveDifferentTokens() {
        String token1 = jwtUtils.generateToken(1L, "user1", "user");
        String token2 = jwtUtils.generateToken(2L, "user2", "user");

        assertNotEquals(token1, token2);
        assertEquals(1L, jwtUtils.getUserIdFromToken(token1));
        assertEquals(2L, jwtUtils.getUserIdFromToken(token2));
        assertEquals("user1", jwtUtils.getUsernameFromToken(token1));
        assertEquals("user2", jwtUtils.getUsernameFromToken(token2));
    }
}
