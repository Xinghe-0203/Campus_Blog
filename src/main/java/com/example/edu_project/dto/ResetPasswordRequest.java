package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求 DTO
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordRequest {

    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    @Schema(description = "邮箱地址", required = true)
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码", required = true, example = "123456")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "密码长度至少为8位")
    @Schema(description = "新密码", required = true)
    private String newPassword;
}