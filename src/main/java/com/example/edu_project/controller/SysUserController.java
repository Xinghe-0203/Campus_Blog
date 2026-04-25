package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.UserLoginResponse;
import com.example.edu_project.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
