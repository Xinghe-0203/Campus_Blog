package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.AdminUserQueryRequest;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
import com.example.edu_project.dto.UserSearchRequest;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.vo.AdminUserVO;
import com.example.edu_project.vo.UserLoginResponse;
import com.example.edu_project.vo.UserVO;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册响应
     */
    UserRegisterResponse register(UserRegisterRequest request);

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    UserLoginResponse login(UserLoginRequest request);

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    SysUser getUserById(Long id);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 搜索用户（支持用户名和昵称模糊匹配）
     * @param request 搜索请求
     * @return 分页用户列表
     */
    IPage<UserVO> searchUsers(UserSearchRequest request);

    /**
     * 获取管理员用户列表（支持状态筛选和关键词搜索）
     * @param request 查询请求
     * @return 分页用户列表
     */
    IPage<AdminUserVO> getAdminUserList(AdminUserQueryRequest request);

    /**
     * 修改用户资料
     * @param userId 用户ID
     * @param nickname 昵称
     * @param email 邮箱
     */
    void updateUserProfile(Long userId, String nickname, String email);

    /**
     * 修改用户头像
     * @param userId 用户ID
     * @param avatar 头像URL
     */
    void updateAvatar(Long userId, String avatar);

    /**
     * 封禁/解封用户（切换状态）
     * @param userId 用户ID
     * @param ban true=封禁，false=解封
     */
    void banUser(Long userId, boolean ban);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户信息
     */
    SysUser getUserByEmail(String email);

    /**
     * 重置密码（通过邮箱验证码）
     * @param email 邮箱
     * @param newPassword 新密码
     */
    void resetPassword(String email, String newPassword);
}
