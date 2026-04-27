package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.EmailService;
import com.example.edu_project.utils.StringMaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Value("${spring.mail.username:noreply@campusblog.com}")
    private String fromEmail;

    @Value("${mail.verification.expire-minutes:5}")
    private int expireMinutes;

    @Value("${mail.verification.max-verify-attempts:3}")
    private int maxVerifyAttempts;

    @Value("${mail.verification.send-interval-seconds:60}")
    private int sendIntervalSeconds;

    // 验证码存储: email -> {code, expireTime, attempts}
    private final Map<String, VerificationData> verificationStore = new ConcurrentHashMap<>();

    // 发送时间记录: email -> lastSendTime
    private final Map<String, Long> sendTimeStore = new ConcurrentHashMap<>();

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码数据结构
     */
    private static class VerificationData {
        String code;
        long expireTime;
        AtomicInteger attempts;

        VerificationData(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
            this.attempts = new AtomicInteger(0);
        }
    }

    @Override
    public boolean sendVerificationCode(String to) {
        // 参数校验
        if (to == null || to.trim().isEmpty()) {
            throw new BusinessException(400, "邮箱地址不能为空");
        }

        // 检查邮箱是否已注册
        if (!isEmailRegistered(to)) {
            throw new BusinessException(404, "该邮箱未注册");
        }

        // 检查发送频率限制
        Long lastSendTime = sendTimeStore.get(to);
        if (lastSendTime != null) {
            long elapsed = (System.currentTimeMillis() - lastSendTime) / 1000;
            if (elapsed < sendIntervalSeconds) {
                throw new BusinessException(429, "发送太频繁，请" + (sendIntervalSeconds - elapsed) + "秒后再试");
            }
        }

        // 生成6位随机验证码
        String code = generateSecureCode();
        long expireTime = System.currentTimeMillis() + expireMinutes * 60 * 1000L;

        // 存储验证码
        verificationStore.put(to, new VerificationData(code, expireTime));
        sendTimeStore.put(to, System.currentTimeMillis());

        // 发送邮件
        try {
            sendHtmlEmail(to, code);
            log.info("验证码已发送至: {}", StringMaskUtils.maskEmail(to));
            return true;
        } catch (Exception e) {
            log.error("发送验证码失败: {}", e.getMessage());
            // 发送失败时移除存储的验证码
            verificationStore.remove(to);
            throw new BusinessException(500, "邮件发送失败，请稍后重试");
        }
    }

    @Override
    public boolean verifyCode(String email, String code) {
        // 参数校验
        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            throw new BusinessException(400, "邮箱和验证码不能为空");
        }

        VerificationData data = verificationStore.get(email);
        if (data == null) {
            throw new BusinessException(400, "验证码已失效，请重新获取");
        }

        // 检查是否过期
        if (System.currentTimeMillis() > data.expireTime) {
            verificationStore.remove(email);
            throw new BusinessException(400, "验证码已过期，请重新获取");
        }

        // 增加验证尝试次数（原子操作）
        int currentAttempts = data.attempts.incrementAndGet();

        // 检查是否超过最大尝试次数
        if (currentAttempts > maxVerifyAttempts) {
            verificationStore.remove(email);
            throw new BusinessException(400, "验证失败次数过多，请重新获取验证码");
        }

        // 验证验证码
        if (data.code.equals(code)) {
            // 验证成功，移除验证码
            verificationStore.remove(email);
            return true;
        }

        // 验证码错误
        if (currentAttempts >= maxVerifyAttempts) {
            verificationStore.remove(email);
            throw new BusinessException(400, "验证失败次数过多，请重新获取验证码");
        }

        throw new BusinessException(400, "验证码错误，剩余" + (maxVerifyAttempts - currentAttempts) + "次尝试机会");
    }

    @Override
    public boolean isEmailRegistered(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email.trim());
        return sysUserMapper.selectCount(wrapper) > 0;
    }

    /**
     * 生成安全的6位随机验证码
     */
    private String generateSecureCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(SECURE_RANDOM.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送HTML格式邮件
     */
    private void sendHtmlEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("校园博客论坛 - 密码找回验证码");

            String htmlContent = buildEmailTemplate(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            // 如果HTML邮件发送失败，尝试发送纯文本邮件
            log.warn("HTML邮件发送失败，尝试发送纯文本邮件: {}", e.getMessage());
            try {
                SimpleMailMessage simpleMessage = new SimpleMailMessage();
                simpleMessage.setFrom(fromEmail);
                simpleMessage.setTo(to);
                simpleMessage.setSubject("校园博客论坛 - 密码找回验证码");
                simpleMessage.setText("您的验证码是: " + code + "\n验证码 " + expireMinutes + " 分钟内有效，请勿泄露给他人。\n\n如非本人操作，请忽略此邮件。");
                mailSender.send(simpleMessage);
            } catch (Exception ex) {
                log.error("纯文本邮件发送也失败: {}", ex.getMessage());
                throw new BusinessException(500, "邮件发送失败，请稍后重试");
            }
        }
    }

    /**
     * 构建邮件模板
     */
    private String buildEmailTemplate(String code) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: 'Microsoft YaHei', Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }\n" +
                "        .container { max-width: 500px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }\n" +
                "        .header h1 { margin: 0; font-size: 24px; font-weight: 500; }\n" +
                "        .content { padding: 40px 30px; text-align: center; }\n" +
                "        .code-box { background: #f8f9fa; border: 2px dashed #667eea; border-radius: 8px; padding: 20px 40px; display: inline-block; margin: 20px 0; }\n" +
                "        .code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 8px; }\n" +
                "        .tip { color: #666; font-size: 14px; line-height: 1.6; }\n" +
                "        .footer { background: #f5f5f5; color: #999; font-size: 12px; text-align: center; padding: 15px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>校园博客论坛</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>您好，</p>\n" +
                "            <p>您正在进行密码找回操作，请使用以下验证码：</p>\n" +
                "            <div class=\"code-box\">\n" +
                "                <span class=\"code\">" + code + "</span>\n" +
                "            </div>\n" +
                "            <p class=\"tip\">验证码在 <strong>5 分钟</strong> 内有效，请勿泄露给他人。</p>\n" +
                "            <p class=\"tip\">如非本人操作，请忽略此邮件。</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>此邮件由系统自动发出，请勿回复。</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}