package com.example.edu_project.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 * 【功能说明】
 *   1. 配置 Mapper 接口扫描路径
 *   2. 分页插件待后续配置（解决依赖问题后）
 */
@Configuration
@MapperScan("com.example.edu_project.mapper")
public class MybatisPlusConfig {

}
