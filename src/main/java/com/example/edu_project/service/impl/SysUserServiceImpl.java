package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
import com.example.edu_project.dto.AdminUserQueryRequest;
import com.example.edu_project.dto.UserSearchRequest;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.JwtUtils;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.AdminUserVO;
import com.example.edu_project.vo.UserLoginResponse;
import com.example.edu_project.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
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

        log.info("用户注册成功: username={}, userId={}", user.getUsername(), user.getId());
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
            log.warn("用户登录失败: username={}, 原因=用户不存在", request.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查账户是否被锁定
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(403, "登录失败次数过多，请稍后再试");
        }

        // 检查账号状态（先于密码验证）
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("用户登录失败: username={}, 原因=密码错误", request.getUsername());
            // 密码错误，使用原子操作增加失败计数
            handleLoginFailAtomic(user.getId());
            // 重新查询获取最新锁定状态
            SysUser updatedUser = this.getById(user.getId());
            if (updatedUser.getLockUntil() != null && updatedUser.getLockUntil().isAfter(LocalDateTime.now())) {
                log.warn("用户登录失败: username={}, 原因=账号已被锁定至{}", request.getUsername(), updatedUser.getLockUntil());
                throw new BusinessException(403, "登录失败次数过多，请稍后再试");
            }
            throw new BusinessException(401, "用户名或密码错误");
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

        log.info("用户登录成功: username={}, userId={}", user.getUsername(), user.getId());
        return new UserLoginResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new BusinessException(400, "旧密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BusinessException(400, "新密码不能为空");
        }
        if (newPassword.length() < 8) {
            throw new BusinessException(400, "新密码长度至少为8位");
        }

        // 获取用户
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("用户修改密码失败: userId={}, 原因=旧密码错误", userId);
            throw new BusinessException(400, "旧密码不正确");
        }

        // 新密码复杂度校验：至少8位，包含大小写字母、数字或特殊字符中的3种
        int categories = 0;
        if (newPassword.matches(".*[A-Z].*")) categories++;
        if (newPassword.matches(".*[a-z].*")) categories++;
        if (newPassword.matches(".*\\d.*")) categories++;
        if (newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) categories++;
        if (categories < 3) {
            throw new BusinessException(400, "新密码必须包含大小写字母、数字或特殊字符中的至少3种");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        log.info("用户修改密码成功: userId={}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<UserVO> searchUsers(UserSearchRequest request) {
        Page<SysUser> page = new Page<>(request.getPage(), request.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索：支持 username 和 nickname 模糊匹配
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().trim();
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getNickname, keyword));
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> userPage = this.page(page, wrapper);

        // 转换为 UserVO
        IPage<UserVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(userPage.getRecords().stream()
                .map(this::convertToUserVO)
                .collect(java.util.stream.Collectors.toList()));

        return result;
    }

    private UserVO convertToUserVO(SysUser user) {
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
        return userVO;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<AdminUserVO> getAdminUserList(AdminUserQueryRequest request) {
        Page<SysUser> page = new Page<>(request.getPage(), request.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索：支持 username、nickname 和 email 模糊匹配
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().trim();
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getNickname, keyword)
                    .or()
                    .like(SysUser::getEmail, keyword));
        }
        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> userPage = this.page(page, wrapper);

        // 转换为 AdminUserVO
        IPage<AdminUserVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(userPage.getRecords().stream()
                .map(this::convertToAdminUserVO)
                .collect(java.util.stream.Collectors.toList()));

        return result;
    }

    private AdminUserVO convertToAdminUserVO(SysUser user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setFollowingCount(user.getFollowingCount());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserProfile(Long userId, String nickname, String email) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new BusinessException(400, "昵称不能为空");
        }
        if (nickname.length() > 50) {
            throw new BusinessException(400, "昵称长度不能超过50字符");
        }

        // 获取用户
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 检查邮箱唯一性（如果提供了新邮箱）
        if (email != null && !email.trim().isEmpty()) {
            LambdaQueryWrapper<SysUser> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(SysUser::getEmail, email)
                    .ne(SysUser::getId, userId); // 排除当前用户
            if (this.count(emailWrapper) > 0) {
                throw new BusinessException(400, "邮箱已被使用");
            }
            user.setEmail(email);
        }

        user.setNickname(nickname.trim());
        this.updateById(user);
        log.info("用户资料更新成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(Long userId, String avatar) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        if (avatar == null || avatar.trim().isEmpty()) {
            throw new BusinessException(400, "头像URL不能为空");
        }

        // 获取用户
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setAvatar(avatar.trim());
        this.updateById(user);
        log.info("用户头像更新成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void banUser(Long userId, boolean ban) {
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 不能操作自己
        Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new BusinessException(400, "不能操作自己的账号");
        }

        // 不能操作其他管理员
        if ("admin".equals(user.getRole())) {
            throw new BusinessException(400, "不能操作管理员账号");
        }

        user.setStatus(ban ? 0 : 1);
        this.updateById(user);
        log.info("用户{}: userId={}", ban ? "已封禁" : "已解封", userId);
    }
}
