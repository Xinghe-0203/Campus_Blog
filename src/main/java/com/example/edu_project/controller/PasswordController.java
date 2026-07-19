package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.ResetPasswordRequest;
import com.example.edu_project.dto.SendCodeRequest;
import com.example.edu_project.service.EmailService;
import com.example.edu_project.service.SysUserService;
import com.example.edu_project.utils.StringMaskUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 密码找回控制器
 */
@Slf4j
@Tag(name = "密码找回", description = "密码找回相关接口")
@RestController
@RequestMapping("/user")
@Validated
public class PasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 发送验证码到邮箱
     */
    @Operation(summary = "发送验证码", description = "发送密码找回验证码到指定邮箱")
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        // 检查邮箱是否已注册
        if (!emailService.isEmailRegistered(request.getEmail())) {
            // 为了防止用户枚举攻击，返回成功但实际不发送
            log.info("密码找回尝试：邮箱未注册 - {}", StringMaskUtils.maskEmail(request.getEmail()));
            return Result.success("验证码已发送", null);
        }

        emailService.sendVerificationCode(request.getEmail());
        return Result.success("验证码已发送", null);
    }

    /**
     * 验证验证码并重置密码
     */
    @Operation(summary = "重置密码", description = "验证验证码后重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // 验证验证码
        if (!emailService.verifyCode(request.getEmail(), request.getCode())) {
            throw new BusinessException(400, "验证码验证失败");
        }

        // 重置密码
        sysUserService.resetPassword(request.getEmail(), request.getNewPassword());

        log.info("密码重置成功: {}", StringMaskUtils.maskEmail(request.getEmail()));
        return Result.success("密码重置成功", null);
    }
}