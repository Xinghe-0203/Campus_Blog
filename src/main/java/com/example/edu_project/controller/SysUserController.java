package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.ChangePasswordRequest;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserProfileRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
import com.example.edu_project.dto.UserSearchRequest;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.JwtUtils;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.UserLoginResponse;
import com.example.edu_project.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@Validated
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse response = sysUserService.register(request);
        return Result.success(response);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = sysUserService.login(request);
        return Result.success(response);
    }

    /**
     * 根据ID查询用户
     */
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getUserById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        userVO.setStatus(user.getStatus());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());

        // 仅用户本人或管理员可见敏感信息（邮箱、角色）
        Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();
        boolean isOwner = currentUserId != null && currentUserId.equals(id);
        boolean isAdmin = SecurityUtils.isCurrentUserAdmin();
        if (isOwner || isAdmin) {
            userVO.setEmail(user.getEmail());
            userVO.setRole(user.getRole());
        }

        return Result.success(userVO);
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<UserLoginResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(400, "无效的Token");
        }
        String refreshToken = authHeader.substring(7);

        // 验证刷新Token
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            throw new BusinessException(401, "无效的刷新Token");
        }
        if (jwtUtils.isTokenExpired(refreshToken) || jwtUtils.isTokenRevoked(refreshToken)) {
            throw new BusinessException(401, "刷新Token已过期或已撤销");
        }

        // 生成新的AccessToken
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String role = jwtUtils.getRoleFromToken(refreshToken);

        // 校验用户当前状态：避免被禁用/锁定/删除的用户继续刷新Token
        SysUser currentUser = sysUserService.getUserById(userId);
        if (currentUser == null) {
            throw new BusinessException(401, "用户不存在或已注销");
        }
        if (currentUser.getStatus() != null && currentUser.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        if (currentUser.getLockUntil() != null
                && currentUser.getLockUntil().isAfter(java.time.LocalDateTime.now())) {
            throw new BusinessException(403, "账号已被锁定");
        }

        // 撤销旧刷新Token（实现refresh token rotation）
        jwtUtils.revokeToken(refreshToken);

        // 生成新的AccessToken
        String newToken = jwtUtils.generateToken(userId, username, role);
        // 生成新的RefreshToken（完成refresh token rotation）
        String newRefreshToken = jwtUtils.generateRefreshToken(userId, username, role);

        // 返回完整响应
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(newToken);
        response.setRefreshToken(newRefreshToken);
        return Result.success(response);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        sysUserService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功", null);
    }

    /**
     * 搜索用户
     */
    @Operation(summary = "搜索用户")
    @GetMapping("/search")
    public Result<IPage<UserVO>> searchUsers(@Valid UserSearchRequest request) {
        IPage<UserVO> result = sysUserService.searchUsers(request);
        return Result.success(result);
    }

    /**
     * 修改用户资料
     */
    @Operation(summary = "修改用户资料")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        sysUserService.updateUserProfile(userId, request.getNickname(), request.getEmail());
        return Result.success("资料修改成功", null);
    }

    /**
     * 修改头像
     */
    @Operation(summary = "修改头像")
    @PutMapping("/avatar")
    public Result<Void> updateAvatar(@RequestBody java.util.Map<String, String> request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        String avatar = request.get("avatar");
        if (avatar == null || avatar.trim().isEmpty()) {
            throw new BusinessException(400, "头像URL不能为空");
        }
        sysUserService.updateAvatar(userId, avatar);
        return Result.success("头像修改成功", null);
    }
}
