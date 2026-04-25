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
│   │   ├── JwtUtils.java                       # JWT 工具类
│   │   ├── SecurityUtils.java                  # 安全工具类
│   │   └── UserContext.java                    # 用户上下文对象
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

### 3. 环境变量配置（必需）

使用 `.env` 文件配置所有敏感信息：

```bash
# 1. 复制环境变量模板
cp .env.example .env

# 2. 修改 .env 文件，填入实际配置值

# 3. 启动项目（.env 会自动加载）
mvn spring-boot:run
```

**注意**：`.env` 文件包含敏感信息，已添加到 `.gitignore` 不会提交到 Git。

### 4. 启动项目

运行 `EduProjectApplication.java` 的 `main` 方法。

### 5. 访问 API 文档

项目启动成功后，访问：
**http://localhost/api/doc.html**

## 默认账号

- 用户名：`admin`
- 密码：`admin123`（需确保数据库中已初始化该用户）

## 当前已实现的API接口

### 用户模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/user/register` | 用户注册（密码复杂度校验 + 用户名/邮箱唯一性校验） |
| POST | `/api/user/login` | 用户登录（返回JWT Token + 刷新Token） |
| POST | `/api/user/refresh` | 刷新Token（使用刷新Token获取新Token） |
| GET | `/api/user/{id}` | 根据ID查询用户（管理员可查所有，普通用户仅查自己） |

### 文章模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/post` | 发布文章（需登录） |
| PUT | `/api/post/{id}` | 更新文章（仅作者或管理员可操作） |
| DELETE | `/api/post/{id}` | 删除文章（仅作者或管理员可操作） |
| GET | `/api/post/{id}` | 获取文章详情（公开访问） |
| GET | `/api/post/list` | 获取文章列表（支持关键词/分类/标签筛选） |
| PUT | `/api/post/{id}/view` | 增加阅读量（按用户/IP防刷） |

### 点赞模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/like/{postId}` | 点赞/取消点赞（需登录） |
| GET | `/api/like/check/{postId}` | 检查是否已点赞（公开访问） |

### 收藏模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/collect/{postId}` | 收藏/取消收藏（需登录） |
| GET | `/api/collect/check/{postId}` | 检查是否已收藏（公开访问） |
| GET | `/api/collect/my` | 获取我的收藏列表（需登录） |

### 评论模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/comment` | 发表评论/回复（需登录） |
| GET | `/api/comment/post/{postId}` | 获取文章评论列表（公开访问，树形结构） |
| DELETE | `/api/comment/{commentId}` | 删除评论（仅作者或管理员可操作） |

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
4. ~~代码安全加固与Bug修复~~ ✅ 已完成
5. ~~实现评论、点赞、收藏功能~~ ✅ 已完成
6. ~~实现登录限流与账号锁定机制~~ ✅ 已完成
7. ~~实现XSS防护过滤器~~ ✅ 已完成（HtmlSanitizer + Jsoup）
8. 对接前端页面

## 更新日志

### v1.17 (2026-04-25)
- 🎨 新增标签查询功能：添加 BlogTagService 接口和 BlogTagController GET /tag/list
- 🔧 BlogPost 新增 collectCount 字段，BlogCollectServiceImpl.toggleCollect() 正确更新收藏数
- 🔧 getPostDetail 未发布文章返回"文章未发布"（区分不存在）

### v1.16 (2026-04-25)
- 🔧 BlogPost 新增 collectCount 字段，BlogCollectServiceImpl.toggleCollect() 正确更新收藏数
- 🔧 getPostDetail 未发布文章返回"文章未发布"（区分不存在）
- 🔧 移除 JwtUtils.getUserIdFromRequest() 和 SecurityUtils.getCurrentUserRole() 死代码
- 🔧 移除 BlogCollectServiceImpl 未使用的 ConcurrentHashMap import

### v1.15 (2026-04-25)
- 🔒 修复 JWT 黑名单验证绕过漏洞：revokeToken() 先验证签名再加入黑名单
- 🔒 修复 isTokenExpired() 异常处理：只有 ExpiredJwtException 算过期
- 🔒 修复 JwtAuthenticationFilter：先验证签名再检查黑名单，role 为 null 时使用默认 ROLE_USER
- 🔧 修复 refresh token rotation：刷新时返回新的 refresh token
- 🔧 JwtAuthenticationFilter logger.warn 格式修复

### v1.14 (2026-04-25)
- 🔒 修复点赞/收藏锁内存泄漏：getLock/getCollectLock 添加主动清理过期锁
- 🔒 修复阅读量增加 TOCTOU 竞态条件：使用 CAS 操作替代直接 set
- 🔧 统一密码最小长度：UserRegisterRequest DTO 密码最小长度从 6 改为 8
- 🔧 移除 DotenvConfig 硬编码路径 "D:/MyCode/edu_project"
- 🔧 添加 category 字段 XSS 防护：在 BlogPostServiceImpl 中对 category 进行 HTML 过滤
- 🔧 移除所有 Controller 的 @CrossOrigin 注解（使用全局 CORS 配置）

### v1.13 (2026-04-25)
- 🔧 JwtUtils: 移除未使用的 generateToken(Long, String) 重载方法
- 🔧 HtmlSanitizer: 移除未使用的 containsDangerousTags 方法

### v1.12 (2026-04-25)
- 🔒 移除 JWT/Database 密码硬编码默认值（安全加固）
- 🔒 BlogLikeServiceImpl: 添加 updatedPost 空指针检查
- 🔧 BlogCommentServiceImpl: 添加评论递归深度限制（MAX_RECURSION_DEPTH=100）
- 🔧 BlogPostServiceImpl: 移除未使用的 convertToDetailResponse 死代码
- 📝 README.md: XSS防护过滤器标记为已完成
- 📝 前端开发文档.md: 版本号更新至 v1.11

### v1.11 (2026-04-25)
- 🔒 JwtAuthenticationFilter: 添加 Token 撤销检查 + 修复权限列表
- 🔒 HtmlSanitizer: 移除 img 标签 data: 协议防止 XSS bypass
- 🔒 SysUserServiceImpl: 登录锁定信息通用化防用户枚举
- 🔧 CommentCreateRequest: postId 添加 @NotNull 校验
- 🔧 BlogCommentServiceImpl: 修复 O(n²) 父评论查找为 O(n)
- 🔧 EduProjectApplication: System.out.println 改为 SLF4J 日志

### v1.10 (2026-04-25)
- 🔒 修复用户枚举漏洞：注册时使用通用错误信息防止用户名/邮箱枚举攻击
- 🔒 修复点赞/收藏竞态条件：使用细粒度锁 + DuplicateKeyException 处理保证并发安全
- 🔒 新增 XSS 防护：使用 Jsoup 过滤文章和评论内容
- 🔧 完善@Transactional注解：所有只读方法添加readOnly=true
- 🔧 修复batchInsertPostTags缺少@Transactional注解问题
- 📝 更新数据库表结构文档（blog_post_tag、blog_like、blog_collect）
- 📝 修复访问端口（改为80而非8080）
- 📝 所有md文档同步更新至v1.10

### v1.9 (2026-04-25)
- 🔒 修复IP伪造漏洞：未登录用户指纹改为IP+User-Agent哈希组合
- 🔒 修复点赞竞态条件：使用try-catch处理DuplicateKeyException
- 🔒 修复评论删除级联问题：递归删除子评论，正确更新评论计数
- 🔒 新增登录失败锁定机制：连续5次失败锁定15分钟（原子更新，并发安全）
- 🔒 提升BCrypt强度：10轮提升至12轮
- 🔒 新增JWT Token黑名单机制：支持主动撤销Token
- 🔒 新增JWT刷新Token机制：支持生成7天有效期的refreshToken
- 🔒 实现Refresh Token Rotation：使用后自动撤销旧Token
- 🔒 新增JWT黑名单定时清理：每小时清理过期Token
- 🔧 新增SysUser字段：loginFailCount、lockUntil
- 🔧 新增BlogPostMapper重载方法：decrementCommentCount支持批量减数
- 🔧 新增用户登录返回refreshToken字段
- 🔧 新增刷新Token接口：POST /api/user/refresh
- 🔧 新增JwtSchedulerConfig定时任务配置类
- 🔧 新增SysUserMapper.incrementLoginFailCount原子更新方法
- 📝 更新文档：所有md文档同步更新

### v1.8 (2026-04-25)
- ✨ 新增点赞模块：支持点赞/取消点赞，自动更新文章点赞数
- ✨ 新增收藏模块：支持收藏/取消收藏，分页查看我的收藏列表
- ✨ 新增评论模块：支持发表评论/回复，树形结构展示评论列表
- 🔧 优化BlogLike和BlogCollect实体：新增自增id字段
- 🔧 更新SecurityConfig：添加新接口的权限控制
- 🔧 新增多个DTO/VO类：LikeStatusVO、LikeResultVO、CollectStatusVO、CollectResultVO、CollectItemVO、CommentCreateRequest、CommentVO
- 📝 完善项目文档：更新README.md和CLAUDE.md
- 🔒 新增管理员权限支持：管理员可删除任意评论

### v1.7 (2026-04-25)
- 🔧 修复SecurityConfig路径匹配错误（添加/api前缀）
- 🔧 修复阅读量防刷逻辑（按用户/IP区分，不再按文章ID全局限制）
- 🔧 修复BlogPostTag联合主键配置（新增自增id字段）
- 🔧 修复阅读量重复增加问题（移除getPostDetail中的自动增加）
- 🔧 完善用户注册体验（提前检查用户名/邮箱存在性）
- ✨ 新增管理员权限支持（admin可修改/删除所有文章）
- ✨ JWT Token新增角色信息，SecurityUtils支持权限检查
- ✨ 新增UserContext用户上下文对象
- ⚡ 优化SecurityUtils性能（避免不必要的异常开销）
- 📝 完善配置文件说明（明确生产环境环境变量要求）
- 🔒 SQL日志支持环境变量配置控制

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
