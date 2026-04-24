package com.example.edu_project.utils;

import com.example.edu_project.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 用于从 SecurityContext 获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的ID
     * @return 用户ID
     * @throws BusinessException 如果用户未登录
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessException(401, "请先登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new BusinessException(401, "请先登录");
    }

    /**
     * 获取当前登录用户的ID，如果未登录返回null
     * @return 用户ID或null
     */
    public static Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (BusinessException e) {
            return null;
        }
    }
}
