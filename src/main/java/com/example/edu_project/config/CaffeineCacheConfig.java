package com.example.edu_project.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置类
 *
 * 缓存策略：
 * - hotTagsCache: 热门标签缓存，5分钟过期
 * - categoryCache: 分类缓存，10分钟过期
 * - userCache: 用户缓存，5分钟过期
 * - trendingCache: 趋势缓存，1分钟过期
 */
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    /**
     * 热门标签缓存名称
     */
    public static final String HOT_TAGS_CACHE = "hotTagsCache";

    /**
     * 分类缓存名称
     */
    public static final String CATEGORY_CACHE = "categoryCache";

    /**
     * 用户缓存名称
     */
    public static final String USER_CACHE = "userCache";

    /**
     * 趋势缓存名称
     */
    public static final String TRENDING_CACHE = "trendingCache";

    /**
     * 配置 Caffeine 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 全局配置：最大1000条缓存，写入后5分钟过期
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 记录命中统计
                .recordStats());
        return cacheManager;
    }

    /**
     * 热门标签缓存配置（5分钟过期）
     */
    @Bean(HOT_TAGS_CACHE)
    public Caffeine<Object, Object> hotTagsCache() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * 分类缓存配置（10分钟过期）
     */
    @Bean(CATEGORY_CACHE)
    public Caffeine<Object, Object> categoryCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * 用户缓存配置（5分钟过期）
     */
    @Bean(USER_CACHE)
    public Caffeine<Object, Object> userCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * 趋势缓存配置（1分钟过期）
     */
    @Bean(TRENDING_CACHE)
    public Caffeine<Object, Object> trendingCache() {
        return Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .recordStats();
    }
}
