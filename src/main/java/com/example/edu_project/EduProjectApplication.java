package com.example.edu_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用启动类
 * 【说明】这是应用的入口点
 */
@SpringBootApplication
public class EduProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduProjectApplication.class, args);
        System.out.println("========================================");
        System.out.println("   校园博客论坛系统启动成功！");
        System.out.println("   API文档地址：http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }

}
