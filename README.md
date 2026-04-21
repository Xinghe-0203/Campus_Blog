# 校园博客论坛系统 (Campus Blog Forum)

一个基于 Spring Boot 的校园博客论坛系统，用于校技能大赛。

## 📋 项目简介

校园博客论坛系统是一个前后端分离的 Web 应用，提供文章发布、评论互动、点赞收藏等功能。

## 🛠️ 技术栈

### 后端
- **Spring Boot**: 3.3.5
- **MyBatis Plus**: 3.5.7
- **MySQL**: 8.0+
- **Knife4j**: 4.4.0 (API 文档)
- **Lombok**: 代码简化
- **Hutool**: 工具类库

### 前端
- **HTML5 + CSS3 + JavaScript**
- **Bootstrap 5** (UI 框架)

## 📁 项目结构

```
edu_project/
├── edu_project/                    # 后端项目
│   ├── src/main/java/com/example/edu_project/
│   │   ├── EduProjectApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── mapper/
│   │   └── common/
│   └── pom.xml
├── 数据库表                          # 数据库初始化脚本
└── README.md
```

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### 数据库初始化

1. 创建数据库 `campus_blog`
2. 运行 `数据库表` 文件中的 SQL 脚本

### 修改配置

编辑 `edu_project/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_blog...
    username: root      # 你的 MySQL 用户名
    password: root      # 你的 MySQL 密码
```

### 运行项目

```bash
cd edu_project
mvn spring-boot:run
```

### 访问地址

- **应用地址**: http://localhost:8080
- **API 文档**: http://localhost:8080/api/doc.html

## 📊 数据库设计

项目包含 7 张核心数据表：

1. **sys_user** - 用户表
2. **blog_post** - 文章/帖子表
3. **blog_comment** - 评论表
4. **blog_tag** - 标签表
5. **blog_post_tag** - 文章-标签关联表
6. **blog_like** - 点赞记录表
7. **blog_collect** - 收藏记录表

## 👨‍💻 开发人员

- **刘畅**

## 📝 更新日志

### v1.0 (2026-04-21)

- 初始化项目结构
- 完成数据库设计
- 搭建 Spring Boot 后端骨架
- 配置 MyBatis Plus
- 集成 Knife4j API 文档
