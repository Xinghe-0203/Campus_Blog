# 校园博客论坛系统 - 后端

## 项目简介

这是一个基于 **Spring Boot 3.x** 开发的校园博客论坛后端项目，采用前后端分离架构，提供 RESTful API 接口。

## 技术栈

| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| **Spring Boot** | 4.0.5 | 核心框架 |
| **MyBatis Plus** | 3.5.9 | ORM 持久层框架（MyBatis 增强） |
| **MySQL** | 8.x | 关系型数据库 |
| **Knife4j** | 4.5.0 | API 文档（基于 Swagger） |
| **Lombok** | 最新 | 简化 Java 代码 |
| **Hutool** | 5.8.38 | Java 工具类库 |

## 项目结构

```
edu_project/
├── src/main/java/com/example/edu_project/
│   ├── EduProjectApplication.java    # 启动类
│   ├── config/                        # 配置类
│   │   └── MybatisPlusConfig.java    # MyBatis Plus 配置
│   ├── controller/                    # Controller 层（API 接口）
│   │   └── SysUserController.java    # 用户控制器
│   ├── service/                       # Service 层（业务逻辑）
│   │   └── impl/                      # Service 实现类
│   ├── mapper/                        # Mapper 层（数据库操作）
│   │   ├── SysUserMapper.java
│   │   ├── BlogPostMapper.java
│   │   ├── BlogCommentMapper.java
│   │   ├── BlogTagMapper.java
│   │   ├── BlogPostTagMapper.java
│   │   ├── BlogLikeMapper.java
│   │   └── BlogCollectMapper.java
│   ├── entity/                        # Entity 实体类
│   │   ├── SysUser.java
│   │   ├── BlogPost.java
│   │   ├── BlogComment.java
│   │   ├── BlogTag.java
│   │   ├── BlogPostTag.java
│   │   ├── BlogLike.java
│   │   └── BlogCollect.java
│   └── common/                        # 公共类
│       └── result/
│           └── Result.java             # 统一响应结果类
├── src/main/resources/
│   └── application.yml                 # 应用配置文件
└── pom.xml                             # Maven 依赖配置
```

## 数据库表结构

项目包含 7 张核心表：

1.  **sys_user** - 用户表
2.  **blog_post** - 文章/帖子表
3.  **blog_comment** - 评论表
4.  **blog_tag** - 标签表
5.  **blog_post_tag** - 文章-标签关联表
6.  **blog_like** - 点赞记录表
7.  **blog_collect** - 收藏记录表

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- IDEA / Eclipse

### 2. 数据库初始化

1.  在 MySQL 中创建数据库 `campus_blog`
2.  运行项目根目录下的 `数据库表` 文件

### 3. 修改配置

打开 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_blog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root      # 改成你的 MySQL 用户名
    password: root      # 改成你的 MySQL 密码
```

### 4. 启动项目

运行 `EduProjectApplication.java` 的 `main` 方法。

### 5. 访问 API 文档

项目启动成功后，访问：
**http://localhost:8080/api/doc.html**

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## 开发规范

### 1. 统一返回格式

所有 API 接口统一返回 `Result<T>` 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1234567890
}
```

### 2. 代码分层

- **Controller 层**：接收请求、参数校验、返回响应
- **Service 层**：处理核心业务逻辑
- **Mapper 层**：数据库操作
- **Entity 层**：数据实体

## 下一步开发

1.  实现用户登录注册功能（Spring Security + JWT）
2.  实现文章的增删改查接口
3.  实现评论、点赞、收藏功能
4.  对接前端项目

## 作者

刘畅
