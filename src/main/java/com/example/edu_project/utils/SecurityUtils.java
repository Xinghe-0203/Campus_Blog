package com.example.edu_project.utils;

import com.example.edu_project.entity.SysUser;
import com.example.edu_project.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    private SysUserService sysUserService;

    public Long getCurrentUserId() {
        SysUser user = getCurrentUser();
        if (user == null) {
            throw new com.example.edu_project.common.exception.BusinessException(401, "请先登录");
        }
        return user.getId();
    }

    public Long getCurrentUserIdOrNull() {
        SysUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public SysUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SysUser) {
            return (SysUser) principal;
        }
        return null;
    }

    public boolean isAdmin() {
        SysUser user = getCurrentUser();
        return user != null && "admin".equals(user.getRole());
    }

    public boolean isCurrentUserAdmin() {
        SysUser user = getCurrentUser();
        return user != null && "admin".equals(user.getRole());
    }

    public boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserIdOrNull();
        return currentUserId != null && currentUserId.equals(userId);
    }
}