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
| **JWT (JJWT)** | 0.12.3 | JSON Web Token 认证 |
| **Spring Security** | 3.0.12 | 安全认证框架 |

## 项目结构

```
edu_project/
├── src/main/java/com/example/edu_project/
│   ├── EduProjectApplication.java              # 启动类
│   ├── config/                                  # 配置类
│   │   ├── MybatisPlusConfig.java              # MyBatis Plus 配置
│   │   ├── MyMetaObjectHandler.java           # 自动填充处理器
│   │   └── SecurityConfig.java                # Spring Security 配置
│   ├── controller/                              # Controller 层（API 接口）
│   │   ├── SysUserController.java              # 用户控制器
│   │   └── BlogPostController.java             # 文章控制器
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
│   ├── dto/                                     # 数据传输对象
│   │   ├── UserRegisterRequest.java
│   │   ├── UserLoginRequest.java
│   │   ├── PostCreateRequest.java
│   │   └── PostQueryRequest.java
│   ├── vo/                                      # 视图对象
│   │   ├── UserLoginResponse.java
│   │   ├── PostDetailResponse.java
│   │   └── PostListResponse.java
│   ├── utils/                                   # 工具类
│   │   └── JwtUtils.java                       # JWT 工具类
│   └── entity/                                  # Entity 实体类
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
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/login` | 用户登录 |
| GET | `/api/user/list` | 查询所有用户 |
| GET | `/api/user/{id}` | 根据ID查询用户 |

### 文章模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/post` | 发布文章 |
| PUT | `/api/post/{id}` | 更新文章 |
| DELETE | `/api/post/{id}` | 删除文章 |
| GET | `/api/post/{id}` | 获取文章详情 |
| GET | `/api/post/list` | 获取文章列表 |
| PUT | `/api/post/{id}/view` | 增加阅读量 |

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

1. ~~实现用户注册、登录功能~~ ✅ 已完成
2. ~~实现文章的增删改查接口~~ ✅ 已完成
3. ~~启用 Spring Security + JWT 认证~~ ✅ 已完成
4. 实现评论、点赞、收藏功能
5. 对接前端页面

## 更新日志

### v1.6 (2026-04-24)
- 全面安全加固与代码质量修复
- SysUser添加@JsonIgnore防密码泄露
- getById返回UserVO替代SysUser实体
- 敏感信息（DB密码、JWT密钥）改为环境变量
- 添加防刷机制和权限校验
- Entity联合主键和逻辑删除修复

### v1.5 (2026-04-24)
- 实现文章管理模块完整功能
- 新增 BlogPostController（文章 CRUD 接口）
- 新增 PostCreateRequest、PostQueryRequest DTO
- 新增 PostDetailResponse、PostListResponse VO
- 实现文章标签关联管理（BlogPostTag）
- 实现文章分页查询
- 实现阅读量统计

### v1.4 (2026-04-24)
- 实现用户注册功能（用户名/邮箱唯一性校验、BCrypt 密码加密）
- 实现用户登录功能（密码校验、JWT Token 生成）
- 启用 Spring Security + JWT 依赖
- 新增 JwtUtils 工具类
- 新增 DTO/VO 层（UserRegisterRequest、UserLoginRequest、UserLoginResponse）
- SecurityConfig 配置 JWT 认证过滤器

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
