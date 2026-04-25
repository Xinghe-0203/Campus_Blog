package com.example.edu_project;

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

    public static void main(String[] args) {
        SpringApplication.run(EduProjectApplication.class, args);
        System.out.println("========================================");
        System.out.println("   校园博客论坛系统启动成功！");
        System.out.println("   API文档地址：http://localhost:80/api/doc.html");
        System.out.println("========================================");
    }

}
