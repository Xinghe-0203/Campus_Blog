package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.JwtUtils;
import com.example.edu_project.vo.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final int MAX_LOGIN_FAIL_COUNT = 5; // 最大失败次数
    private static final int LOCK_MINUTES = 15; // 锁定时间（分钟）

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterResponse register(UserRegisterRequest request) {
        // 密码复杂度校验：至少8位，包含大小写字母、数字或特殊字符中的3种
        String password = request.getPassword();
        if (password == null || password.length() < 8) {
            throw new BusinessException(400, "密码长度至少为8位");
        }
        int categories = 0;
        if (password.matches(".*[A-Z].*")) categories++;
        if (password.matches(".*[a-z].*")) categories++;
        if (password.matches(".*\\d.*")) categories++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) categories++;
        if (categories < 3) {
            throw new BusinessException(400, "密码必须包含大小写字母、数字或特殊字符中的至少3种");
        }

        // 提前检查用户名和邮箱是否已存在（使用模糊错误信息防止用户枚举攻击）
        LambdaQueryWrapper<SysUser> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(SysUser::getUsername, request.getUsername());
        if (this.count(usernameWrapper) > 0) {
            throw new BusinessException(400, "注册失败，请稍后重试");
        }

        // 检查邮箱是否已存在（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            LambdaQueryWrapper<SysUser> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(SysUser::getEmail, request.getEmail());
            if (this.count(emailWrapper) > 0) {
                throw new BusinessException(400, "注册失败，请稍后重试");
            }
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole("user");
        user.setStatus(1);
        user.setLoginFailCount(0);

        try {
            this.save(user);
        } catch (DuplicateKeyException e) {
            // 并发注册时捕获数据库唯一约束异常
            throw new BusinessException(400, "注册失败，请稍后重试");
        }

        return new UserRegisterResponse(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginResponse login(UserLoginRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }

        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查账户是否被锁定
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(403, "登录失败次数过多，请稍后再试");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 密码错误，使用原子操作增加失败计数
            handleLoginFailAtomic(user.getId());
            // 重新查询获取最新锁定状态
            SysUser updatedUser = this.getById(user.getId());
            if (updatedUser.getLockUntil() != null && updatedUser.getLockUntil().isAfter(LocalDateTime.now())) {
                throw new BusinessException(403, "登录失败次数过多，请稍后再试");
            }
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 登录成功，重置失败计数和锁定
        if (user.getLoginFailCount() == null || user.getLoginFailCount() > 0) {
            user.setLoginFailCount(0);
            user.setLockUntil(null);
            this.updateById(user);
        }

        // 生成 Token（包含角色）
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        // 生成刷新Token（7天有效期）
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername(), user.getRole());

        return new UserLoginResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getRole(),
                token,
                refreshToken
        );
    }

    /**
     * 原子性处理登录失败（解决并发问题）
     */
    private void handleLoginFailAtomic(Long userId) {
        baseMapper.incrementLoginFailCount(userId, MAX_LOGIN_FAIL_COUNT, LOCK_MINUTES);
    }

    @Override
    @Transactional(readOnly = true)
    public SysUser getUserById(Long id) {
        return this.getById(id);
    }
}
