package com.example.edu_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.example.edu_project.config.SecurityConfig;

/**
 * Spring Boot 应用启动类
 * 【说明】这是应用的入口点
 */
@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class
})
@Import(SecurityConfig.class)
public class EduProjectApplication {

    private static final Logger log = LoggerFactory.getLogger(EduProjectApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EduProjectApplication.class, args);
        log.info("========================================");
        log.info("   校园博客论坛系统启动成功！");
        log.info("   API文档地址：http://localhost:80/api/doc.html");
        log.info("========================================");
    }

}
