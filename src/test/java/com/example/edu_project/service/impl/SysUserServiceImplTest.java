package com.example.edu_project.service.impl;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.UserLoginRequest;
import com.example.edu_project.dto.UserRegisterRequest;
import com.example.edu_project.dto.UserRegisterResponse;
import com.example.edu_project.vo.UserLoginResponse;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SysUserService 单元测试
 * 测试用户注册、登录、密码强度校验等核心功能
 */
@SpringBootTest
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImplTest {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    private static final String TEST_PASSWORD = "Test1234!";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        // 清理测试数据
        sysUserMapper.delete(null);
        // 重置Mock
        reset(jwtUtils);
        // 设置Mock JWT返回值
        when(jwtUtils.generateToken(anyLong(), anyString(), anyString())).thenReturn("mock.jwt.token");
        when(jwtUtils.generateRefreshToken(anyLong(), anyString(), anyString())).thenReturn("mock.refresh.token");
    }

    // ==================== 用户注册测试 ====================

    @Test
    @DisplayName("注册成功 - 正常注册流程")
    void testRegister_Success() {
        String username = "testuser_" + System.currentTimeMillis();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setPassword(TEST_PASSWORD);
        request.setNickname("测试用户");
        request.setEmail(TEST_EMAIL);

        UserRegisterResponse response = sysUserService.register(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(username, response.getUsername());

        // 验证用户已存入数据库
        SysUser savedUser = sysUserMapper.selectById(response.getId());
        assertNotNull(savedUser);
        assertEquals(username, savedUser.getUsername());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPassword()));
        assertEquals("user", savedUser.getRole());
        assertEquals(1, savedUser.getStatus());
    }

    @Test
    @DisplayName("注册失败 - 用户名重复（提前检查）")
    void testRegister_DuplicateUsername() {
        String username = "duplicate_" + System.currentTimeMillis();

        // 先注册一个用户
        UserRegisterRequest request1 = new UserRegisterRequest();
        request1.setUsername(username);
        request1.setPassword(TEST_PASSWORD);
        sysUserService.register(request1);

        // 尝试注册相同用户名
        UserRegisterRequest request2 = new UserRegisterRequest();
        request2.setUsername(username);
        request2.setPassword("NewPass123!");
        request2.setEmail("another@example.com");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.register(request2);
        });

        assertEquals(400, exception.getCode());
        assertEquals("注册失败，请稍后重试", exception.getMessage());
    }

    @Test
    @DisplayName("注册失败 - 邮箱重复（提前检查）")
    void testRegister_DuplicateEmail() {
        String username1 = "user1_" + System.currentTimeMillis();
        String username2 = "user2_" + System.currentTimeMillis();

        // 先注册一个用户
        UserRegisterRequest request1 = new UserRegisterRequest();
        request1.setUsername(username1);
        request1.setPassword(TEST_PASSWORD);
        request1.setEmail(TEST_EMAIL);
        sysUserService.register(request1);

        // 尝试使用相同邮箱注册另一个用户名
        UserRegisterRequest request2 = new UserRegisterRequest();
        request2.setUsername(username2);
        request2.setPassword(TEST_PASSWORD);
        request2.setEmail(TEST_EMAIL);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.register(request2);
        });

        assertEquals(400, exception.getCode());
        assertEquals("注册失败，请稍后重试", exception.getMessage());
    }

    @Test
    @DisplayName("注册失败 - 密码强度不足（只有数字）")
    void testRegister_WeakPassword_DigitOnly() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser123");
        request.setPassword("12345678"); // 只有数字

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.register(request);
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("密码必须包含大小写字母、数字或特殊字符中的至少3种"));
    }

    @Test
    @DisplayName("注册失败 - 密码强度不足（只有小写字母）")
    void testRegister_WeakPassword_LowercaseOnly() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser123");
        request.setPassword("abcdefgh"); // 只有小写字母

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.register(request);
        });

        assertEquals(400, exception.getCode());
    }

    @Test
    @DisplayName("注册失败 - 密码强度不足（只有大写字母和数字）")
    void testRegister_WeakPassword_UppercaseAndDigitOnly() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser123");
        request.setPassword("ABCDEFGH1234"); // 只有大写字母和数字，缺特殊字符

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.register(request);
        });

        assertEquals(400, exception.getCode());
    }

    @Test
    @DisplayName("注册失败 - 密码长度不足（少于8位）")
    void testRegister_PasswordTooShort() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser123");
        request.setPassword("Abc1!"); // 只有5位

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.register(request);
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("密码长度至少为8位"));
    }

    @Test
    @DisplayName("注册成功 - 密码包含所有类型字符")
    void testRegister_StrongPassword() {
        String username = "strong_" + System.currentTimeMillis();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setPassword("Abcdef123!@#"); // 包含大小写字母、数字、特殊字符

        assertDoesNotThrow(() -> {
            sysUserService.register(request);
        });
    }

    @Test
    @DisplayName("注册成功 - 未提供昵称时使用用户名作为昵称")
    void testRegister_NoNickname_UsesUsername() {
        String username = "nonick_" + System.currentTimeMillis();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setPassword(TEST_PASSWORD);
        // 不设置nickname

        UserRegisterResponse response = sysUserService.register(request);
        SysUser savedUser = sysUserMapper.selectById(response.getId());

        assertEquals(username, savedUser.getNickname());
    }

    // ==================== 用户登录测试 ====================

    @Test
    @DisplayName("登录成功 - 正常登录流程")
    void testLogin_Success() {
        String username = "loginuser_" + System.currentTimeMillis();

        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        sysUserService.register(registerRequest);

        // 登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(TEST_PASSWORD);

        UserLoginResponse response = sysUserService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("mock.refresh.token", response.getRefreshToken());
        assertEquals(username, response.getUsername());

        // 验证JWT生成被调用
        verify(jwtUtils, times(1)).generateToken(anyLong(), eq(username), eq("user"));
        verify(jwtUtils, times(1)).generateRefreshToken(anyLong(), eq(username), eq("user"));
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLogin_UserNotFound() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("nonexistent_user_12345");
        loginRequest.setPassword(TEST_PASSWORD);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.login(loginRequest);
        });

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLogin_InvalidPassword() {
        String username = "pwduser_" + System.currentTimeMillis();

        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        sysUserService.register(registerRequest);

        // 使用错误密码登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword("WrongPassword123!");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.login(loginRequest);
        });

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 账户已被禁用（status=0）")
    void testLogin_AccountBanned() {
        String username = "banneduser_" + System.currentTimeMillis();

        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        UserRegisterResponse response = sysUserService.register(registerRequest);

        // 禁用账户
        SysUser user = sysUserMapper.selectById(response.getId());
        user.setStatus(0);
        sysUserMapper.updateById(user);

        // 尝试登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(TEST_PASSWORD);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.login(loginRequest);
        });

        assertEquals(403, exception.getCode());
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 账户被锁定（5次失败后）")
    void testLogin_AccountLocked() {
        String username = "lockuser_" + System.currentTimeMillis();

        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        sysUserService.register(registerRequest);

        // 使用错误密码连续登录5次
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword("WrongPassword123!");

        // 前4次应该返回用户名或密码错误
        for (int i = 0; i < 4; i++) {
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                sysUserService.login(loginRequest);
            });
            assertEquals(401, exception.getCode());
            assertEquals("用户名或密码错误", exception.getMessage());
        }

        // 第5次失败后账户应被锁定
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.login(loginRequest);
        });

        // 账户被锁定后，登录应返回403
        assertEquals(403, exception.getCode());
        assertEquals("登录失败次数过多，请稍后再试", exception.getMessage());
    }

    @Test
    @DisplayName("登录成功 - 失败计数重置")
    void testLogin_LoginFailCountReset() {
        String username = "resetuser_" + System.currentTimeMillis();

        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        sysUserService.register(registerRequest);

        // 连续失败3次
        UserLoginRequest failRequest = new UserLoginRequest();
        failRequest.setUsername(username);
        failRequest.setPassword("WrongPassword123!");

        for (int i = 0; i < 3; i++) {
            try {
                sysUserService.login(failRequest);
            } catch (BusinessException e) {
                // 忽略
            }
        }

        // 正确密码登录成功
        UserLoginRequest successRequest = new UserLoginRequest();
        successRequest.setUsername(username);
        successRequest.setPassword(TEST_PASSWORD);

        UserLoginResponse response = sysUserService.login(successRequest);
        assertNotNull(response);
        assertEquals(username, response.getUsername());
    }

    @Test
    @DisplayName("登录失败 - 用户名为空")
    void testLogin_EmptyUsername() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword(TEST_PASSWORD);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.login(loginRequest);
        });

        assertEquals(400, exception.getCode());
        assertEquals("用户名不能为空", exception.getMessage());
    }

    // ==================== 修改密码测试 ====================

    @Test
    @DisplayName("修改密码成功")
    void testChangePassword_Success() {
        String username = "pwduser_" + System.currentTimeMillis();

        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        UserRegisterResponse registerResponse = sysUserService.register(registerRequest);

        // 修改密码
        String newPassword = "NewPass456!";
        sysUserService.changePassword(registerResponse.getId(), TEST_PASSWORD, newPassword);

        // 验证新密码可以登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(newPassword);

        // 新密码应该能登录成功
        assertDoesNotThrow(() -> {
            sysUserService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("修改密码失败 - 旧密码错误")
    void testChangePassword_WrongOldPassword() {
        String username = "pwduser2_" + System.currentTimeMillis();

        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        UserRegisterResponse registerResponse = sysUserService.register(registerRequest);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.changePassword(registerResponse.getId(), "WrongOldPass1!", "NewPass123!");
        });

        assertEquals(400, exception.getCode());
        assertEquals("旧密码不正确", exception.getMessage());
    }

    @Test
    @DisplayName("修改密码失败 - 新密码强度不足")
    void testChangePassword_WeakNewPassword() {
        String username = "pwduser3_" + System.currentTimeMillis();

        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        UserRegisterResponse registerResponse = sysUserService.register(registerRequest);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.changePassword(registerResponse.getId(), TEST_PASSWORD, "newpass");
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("新密码必须包含大小写字母、数字或特殊字符中的至少3种"));
    }

    @Test
    @DisplayName("修改密码失败 - 新密码长度不足")
    void testChangePassword_NewPasswordTooShort() {
        String username = "pwduser4_" + System.currentTimeMillis();

        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(TEST_PASSWORD);
        UserRegisterResponse registerResponse = sysUserService.register(registerRequest);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.changePassword(registerResponse.getId(), TEST_PASSWORD, "Abc1!");
        });

        assertEquals(400, exception.getCode());
        assertTrue(exception.getMessage().contains("新密码长度至少为8位"));
    }
}
