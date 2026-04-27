package com.example.edu_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置
 * 配置拦截器、静态资源映射和上传文件访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.base-path:uploads}")
    private String uploadBasePath;

    @Value("${upload.root-path:#{systemProperties['user.dir']}}")
    private String uploadRootPath;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/user/login", "/user/register",
                        "/user/send-code", "/user/reset-password");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 可通过 upload.root-path 属性指定上传目录的根路径，默认使用 user.dir
        String uploadPath = Paths.get(uploadRootPath, uploadBasePath).toString();

        // 静态资源映射：/uploads/** -> 本地 uploads 目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
