package com.example.edu_project.controller;

import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.vo.UserLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<SysUser> register(@RequestBody UserRegisterRequest request) {
        SysUser user = sysUserService.register(request);
        return Result.success(user);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = sysUserService.login(request);
        return Result.success(response);
    }

    /**
     * 查询所有用户列表
     */
    @Operation(summary = "查询所有用户")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        List<SysUser> list = sysUserMapper.selectList(null);
        return Result.success(list);
    }

    /**
     * 根据ID查询用户
     */
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        return Result.success(user);
    }
}
