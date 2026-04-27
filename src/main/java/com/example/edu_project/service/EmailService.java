package com.example.edu_project.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送验证码到指定邮箱
     * @param to 收件人邮箱
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String to);

    /**
     * 验证验证码是否正确
     * @param email 邮箱
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyCode(String email, String code);

    /**
     * 检查邮箱是否已注册
     * @param email 邮箱
     * @return 是否已注册
     */
    boolean isEmailRegistered(String email);
}