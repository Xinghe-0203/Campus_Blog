package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
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
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@CrossOrigin
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
        Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();
        if (currentUserId == null) {
            throw new BusinessException(401, "请先登录");
        }
        // 非管理员只能查看自己
        if (!SecurityUtils.isCurrentUserAdmin() && !currentUserId.equals(id)) {
            throw new BusinessException(403, "无权限查看其他用户信息");
        }
        SysUser user = sysUserService.getUserById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        userVO.setEmail(user.getEmail());
        userVO.setRole(user.getRole());
        userVO.setStatus(user.getStatus());
        userVO.setCreateTime(user.getCreateTime());
        userVO.setUpdateTime(user.getUpdateTime());
        return Result.success(userVO);
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authHeader) {
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

        // 撤销旧刷新Token（实现refresh token rotation）
        jwtUtils.revokeToken(refreshToken);

        // 生成新的AccessToken
        String newToken = jwtUtils.generateToken(userId, username, role);
        return Result.success(newToken);
    }
}
