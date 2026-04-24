# 校园博客论坛系统 - 后端

## 项目简介

这是一个基于 **Spring Boot 3.0.12** 开发的校园博客论坛后端项目，采用前后端分离架构，提供 RESTful API 接口。

## 技术栈

| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| **Spring Boot** | 3.0.12 | 核心框架 |
| **MyBatis Plus** | 3.5.5 | ORM 持久层框架（MyBatis 增强） |
| **MySQL** | 8.0+ | 关系型数据库（云端部署） |
| **Knife4j** | 4.5.0 | API 文档（基于 Swagger） |
| **Lombok** | 最新 | 简化 Java 代码 |
| **Hutool** | 5.8.38 | Java 工具类库 |
| **JWT (JJWT)** | 0.12.3 | JSON Web Token 认证（暂未启用） |
| **Spring Security** | 3.0.12 | 安全认证框架（暂未启用） |

## 项目结构

```
edu_project/
├── src/main/java/com/example/edu_project/
│   ├── EduProjectApplication.java              # 启动类
│   ├── config/                                  # 配置类
│   │   ├── MybatisPlusConfig.java              # MyBatis Plus 配置
│   │   ├── MyMetaObjectHandler.java           # 自动填充处理器
│   │   └── SecurityConfig.java                # Spring Security 配置（暂未启用）
│   ├── controller/                              # Controller 层（API 接口）
│   │   └── SysUserController.java              # 用户控制器
│   ├── service/                                 # Service 层（业务逻辑）
│   │   ├── SysUserService.java
│   │   ├── BlogPostService.java
│   │   ├── BlogCommentService.java
│   │   ├── BlogTagService.java
│   │   ├── BlogPostTagService.java
│   │   ├── BlogLikeService.java
│   │   ├── BlogCollectService.java
│   │   └── impl/                                # Service 实现类
│   │       ├── SysUserServiceImpl.java
│   │       ├── BlogPostServiceImpl.java
│   │       ├── BlogCommentServiceImpl.java
│   │       ├── BlogTagServiceImpl.java
│   │       ├── BlogPostTagServiceImpl.java
│   │       ├── BlogLikeServiceImpl.java
│   │       └── BlogCollectServiceImpl.java
│   ├── mapper/                                  # Mapper 层（数据库操作）
│   │   ├── SysUserMapper.java
│   │   ├── BlogPostMapper.java
│   │   ├── BlogCommentMapper.java
│   │   ├── BlogTagMapper.java
│   │   ├── BlogPostTagMapper.java
│   │   ├── BlogLikeMapper.java
│   │   └── BlogCollectMapper.java
│   ├── entity/                                  # Entity 实体类
│   │   ├── SysUser.java
│   │   ├── BlogPost.java
│   │   ├── BlogComment.java
│   │   ├── BlogTag.java
│   │   ├── BlogPostTag.java
│   │   ├── BlogLike.java
│   │   └── BlogCollect.java
│   └── common/                                  # 公共类
│       ├── result/
│       │   └── Result.java                     # 统一响应结果类
│       └── exception/
│           ├── BusinessException.java          # 业务异常类
│           └── GlobalExceptionHandler.java     # 全局异常处理器
├── src/main/resources/
│   └── application.yml                         # 应用配置文件
└── pom.xml                                     # Maven 依赖配置
```

## 数据库表结构

项目包含 7 张核心表：

1. **sys_user** - 用户表
2. **blog_post** - 文章/帖子表
3. **blog_comment** - 评论表（支持嵌套回复）
4. **blog_tag** - 标签表
5. **blog_post_tag** - 文章-标签关联表（联合主键）
6. **blog_like** - 点赞记录表（联合主键）
7. **blog_collect** - 收藏记录表（联合主键）

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- IDEA / Eclipse

### 2. 数据库初始化

在 MySQL 中执行项目根目录下的 `数据库表` SQL 脚本。

### 3. 修改配置

查看 `src/main/resources/application.yml` 配置文件，数据库已配置为云端地址（默认使用配置）。

### 4. 启动项目

运行 `EduProjectApplication.java` 的 `main` 方法。

### 5. 访问 API 文档

项目启动成功后，访问：
**http://localhost:8080/api/doc.html**

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## 当前已实现的API接口

### 用户模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/user/list` | 查询所有用户 |
| GET | `/api/user/{id}` | 根据ID查询用户 |

## 开发规范

### 1. 统一返回格式

所有 API 接口统一返回 `Result<T>` 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1234567890123
}
```

### 2. 代码分层

- **Controller 层**：接收请求、参数校验、返回响应
- **Service 层**：处理核心业务逻辑
- **Mapper 层**：数据库操作（使用 MyBatis Plus）
- **Entity 层**：数据实体（使用 Lombok 简化）

### 3. MyBatis Plus 特性

- 逻辑删除：所有表都有 `isDeleted` 字段
- 自动填充：`createTime` 和 `updateTime` 自动填充
- 分页插件：待后续配置

## 下一步开发计划

1. 实现用户注册、登录功能
2. 实现文章的增删改查接口
3. 实现评论、点赞、收藏功能
4. 启用 Spring Security + JWT 认证
5. 对接前端页面

## 更新日志

### v1.3 (2026-04-24)
- 解决 Spring Boot 与 MyBatis Plus 兼容性问题
- 确定稳定版本组合：Spring Boot 3.0.12 + MyBatis Plus 3.5.5
- 暂时注释 Spring Security 和 JWT 依赖（开发阶段）
- 注释分页插件配置（依赖问题）
- 项目成功启动并正常运行
- 完善所有实体类配置（逻辑删除、自动填充）

### v1.2 (2026-04-21)
- 修复联合主键实体类配置
- 添加逻辑删除字段
- 创建 MetaObjectHandler 自动填充处理器
- 创建完整的 Service 层
- 创建全局异常处理器
- 添加 Spring Security 和 JWT 依赖

### v1.1 (2026-04-21)
- 初始化项目骨架
- 数据库设计完成
- 实现基础 CRUD 接口

## 作者

刘畅
