package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.AdminUserQueryRequest;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.AdminUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

/**
 * 管理员用户管理控制器
 */
@Slf4j
@Tag(name = "管理员-用户管理", description = "管理员用户管理接口")
@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 获取用户列表（管理员）
     */
    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole(''admin'')")
    public Result<IPage<AdminUserVO>> getUserList(@Valid AdminUserQueryRequest request) {
        IPage<AdminUserVO> result = sysUserService.getAdminUserList(request);
        return Result.success(result);
    }

    /**
     * 修改用户状态（封禁/解封）
     */
    @Operation(summary = "修改用户状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole(''admin'')")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {
        // 获取当前管理员ID
        Long adminId = SecurityUtils.getCurrentUserId();

        // 不能封禁自己
        if (id.equals(adminId)) {
            throw new BusinessException(400, "不能修改自己的账号状态");
        }

        // 不能封禁其他管理员
        var user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            throw new BusinessException(400, "不能修改其他管理员的账号状态");
        }

        // 更新状态
        int previousStatus = user.getStatus();
        user.setStatus(request.getStatus());
        sysUserService.updateById(user);

        log.info("管理员修改用户状态: adminId={}, targetUserId={}, previousStatus={}, newStatus={}",
                adminId, id, previousStatus, request.getStatus());

        return Result.success(null);
    }

    /**
     * 重置用户密码（管理员）
     * 注意：生成的临时密码会返回给管理员，应由管理员通过安全渠道转交给用户
     * 建议用户登录后立即修改密码
     */
    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole(''admin'')")
    public Result<String> resetUserPassword(@PathVariable Long id) {
        // 不能重置自己
        Long adminId = SecurityUtils.getCurrentUserId();
        if (id.equals(adminId)) {
            throw new BusinessException(400, "不能重置自己的密码");
        }

        // 检查用户是否存在
        var user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 生成8位随机密码（包含大小写字母和数字）
        String newPassword = generateRandomPassword(8);

        // 加密并更新
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserService.updateById(user);

        log.info("管理员重置用户密码: adminId={}, targetUserId={}, targetUsername={}",
                adminId, id, user.getUsername());

        // 返回临时密码（生产环境建议通过邮件发送）
        return Result.success(newPassword);
    }

    /**
     * 封禁/解封用户
     */
    @Operation(summary = "封禁/解封用户")
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole(''admin'')")
    public Result<Void> banUser(
            @PathVariable Long id,
            @Valid @RequestBody BanRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        sysUserService.banUser(id, request.getBan());

        log.info("管理员{}用户: adminId={}, targetUserId={}, ban={}",
                request.getBan() ? "封禁" : "解封", adminId, id, request.getBan());

        return Result.success(null);
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(PASSWORD_CHARS.charAt(SECURE_RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    /**
     * 状态修改请求
     */
    @Validated
    public static class StatusRequest {
        @jakarta.validation.constraints.NotNull(message = "状态不能为空")
        @jakarta.validation.constraints.Min(value = 0, message = "状态值无效")
        @jakarta.validation.constraints.Max(value = 1, message = "状态值无效")
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    /**
     * 封禁/解封用户请求
     */
    @Validated
    public static class BanRequest {
        @jakarta.validation.constraints.NotNull(message = "ban参数不能为空")
        private Boolean ban;

        public Boolean getBan() {
            return ban;
        }

        public void setBan(Boolean ban) {
            this.ban = ban;
        }
    }
}