package com.example.edu_project.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 环境变量校验器
 * 启动时检查必需的环境变量是否已配置
 * 仅在 env.validation.enabled=true 时激活（默认开启）
 */
@ConditionalOnProperty(name = "env.validation.enabled", havingValue = "true", matchIfMissing = true)
@Component
public class EnvValidationConfig {

    private static final Logger log = LoggerFactory.getLogger(EnvValidationConfig.class);

    @Value("${DB_HOST:#{null}}")
    private String dbHost;

    @Value("${DB_PORT:#{null}}")
    private String dbPort;

    @Value("${DB_NAME:#{null}}")
    private String dbName;

    @Value("${DB_USERNAME:#{null}}")
    private String dbUsername;

    @Value("${DB_PASSWORD:#{null}}")
    private String dbPassword;

    @Value("${JWT_SECRET:#{null}}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION:#{null}}")
    private String jwtExpiration;

    @Value("${JWT_REFRESH_EXPIRATION:#{null}}")
    private String jwtRefreshExpiration;

    @PostConstruct
    public void validateEnvironment() {
        log.info("========================================");
        log.info("   检查环境变量配置...");

        boolean hasError = false;

        // 检查数据库配置
        if (isBlank(dbHost)) {
            log.error("   ✗ DB_HOST 未配置");
            hasError = true;
        } else {
            log.info("   ✓ DB_HOST: {}", maskValue(dbHost));
        }

        if (isBlank(dbPort)) {
            log.error("   ✗ DB_PORT 未配置");
            hasError = true;
        } else {
            log.info("   ✓ DB_PORT: {}", dbPort);
        }

        if (isBlank(dbName)) {
            log.error("   ✗ DB_NAME 未配置");
            hasError = true;
        } else {
            log.info("   ✓ DB_NAME: {}", dbName);
        }

        if (isBlank(dbUsername)) {
            log.error("   ✗ DB_USERNAME 未配置");
            hasError = true;
        } else {
            log.info("   ✓ DB_USERNAME: {}", maskValue(dbUsername));
        }

        if (isBlank(dbPassword)) {
            log.error("   ✗ DB_PASSWORD 未配置");
            hasError = true;
        } else {
            log.info("   ✓ DB_PASSWORD: ********");
        }

        // 检查 JWT 配置
        if (isBlank(jwtSecret)) {
            log.error("   ✗ JWT_SECRET 未配置");
            hasError = true;
        } else {
            log.info("   ✓ JWT_SECRET: ********");
        }

        if (isBlank(jwtExpiration)) {
            log.error("   ✗ JWT_EXPIRATION 未配置");
            hasError = true;
        } else {
            log.info("   ✓ JWT_EXPIRATION: {} ms", jwtExpiration);
        }

        if (isBlank(jwtRefreshExpiration)) {
            log.error("   ✗ JWT_REFRESH_EXPIRATION 未配置");
            hasError = true;
        } else {
            log.info("   ✓ JWT_REFRESH_EXPIRATION: {} ms", jwtRefreshExpiration);
        }

        log.info("========================================");

        if (hasError) {
            log.error("环境变量配置不完整！请检查 .env 文件是否正确配置。");
            log.error("提示：复制 .env.example 为 .env 并填入实际值");
            throw new IllegalStateException("环境变量配置不完整，启动失败");
        }

        log.info("环境变量检查通过！");
        log.info("========================================");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String maskValue(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }
}
