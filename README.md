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
│   ├── controller/                              # Controller 层（13个）
│   │   ├── SysUserController.java              # 用户控制器
│   │   ├── BlogPostController.java             # 文章控制器
│   │   ├── BlogCommentController.java          # 评论控制器
│   │   ├── BlogLikeController.java             # 点赞控制器
│   │   ├── BlogCollectController.java          # 收藏控制器
│   │   ├── BlogTagController.java              # 标签控制器
│   │   ├── FollowController.java               # 关注控制器
│   │   ├── NotificationController.java         # 通知控制器
│   │   ├── TrendingController.java             # 热门控制器
│   │   ├── ReportController.java               # 举报控制器
│   │   ├── AdminReportController.java          # 管理员举报控制器
│   │   ├── CircleController.java               # 校友圈控制器
│   │   └── MediaController.java               # 媒体控制器
│   ├── service/                                 # Service 层（13个）
│   │   ├── SysUserService.java
│   │   ├── BlogPostService.java
│   │   ├── BlogCommentService.java
│   │   ├── BlogTagService.java
│   │   ├── BlogPostTagService.java
│   │   ├── BlogLikeService.java
│   │   ├── BlogCollectService.java
│   │   ├── FollowService.java
│   │   ├── NotificationService.java
│   │   ├── TrendingService.java
│   │   ├── ReportService.java
│   │   ├── CircleService.java
│   │   ├── MediaService.java
│   │   └── impl/                                # Service 实现类（13个）
│   │       ├── SysUserServiceImpl.java
│   │       ├── BlogPostServiceImpl.java
│   │       ├── BlogCommentServiceImpl.java
│   │       ├── BlogTagServiceImpl.java
│   │       ├── BlogPostTagServiceImpl.java
│   │       ├── BlogLikeServiceImpl.java
│   │       ├── BlogCollectServiceImpl.java
│   │       ├── FollowServiceImpl.java
│   │       ├── NotificationServiceImpl.java
│   │       ├── TrendingServiceImpl.java
│   │       ├── ReportServiceImpl.java
│   │       ├── CircleServiceImpl.java
│   │       └── MediaServiceImpl.java
│   ├── mapper/                                  # Mapper 层（18个）
│   │   ├── SysUserMapper.java
│   │   ├── BlogPostMapper.java
│   │   ├── BlogCommentMapper.java
│   │   ├── BlogTagMapper.java
│   │   ├── BlogPostTagMapper.java
│   │   ├── BlogLikeMapper.java
│   │   ├── BlogCollectMapper.java
│   │   ├── BlogFollowMapper.java
│   │   ├── BlogNotificationMapper.java
│   │   ├── BlogTrendingMapper.java
│   │   ├── BlogDraftMapper.java
│   │   ├── BlogReportMapper.java
│   │   ├── CirclePostMapper.java
│   │   ├── CircleLikeMapper.java
│   │   ├── CircleCommentMapper.java
│   │   ├── CircleRepostMapper.java
│   │   ├── MediaMapper.java
│   │   └── BlogPostMediaMapper.java
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
│   └── entity/                                  # Entity 实体类（18个）
│       ├── SysUser.java
│       ├── BlogPost.java
│       ├── BlogComment.java
│       ├── BlogTag.java
│       ├── BlogPostTag.java
│       ├── BlogLike.java
│       ├── BlogCollect.java
│       ├── BlogFollow.java
│       ├── BlogNotification.java
│       ├── BlogTrending.java
│       ├── BlogDraft.java
│       ├── BlogReport.java
│       ├── CirclePost.java
│       ├── CircleLike.java
│       ├── CircleComment.java
│       ├── CircleRepost.java
│       ├── Media.java
│       └── BlogPostMedia.java
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

项目包含 **18 张数据表**：

### 核心表（7张）
1. **sys_user** - 用户表（含 follower_count、following_count）
2. **blog_post** - 文章/帖子表（含 view_count、like_count、comment_count、collect_count）
3. **blog_comment** - 评论表（支持嵌套回复）
4. **blog_tag** - 标签表（含 post_count）
5. **blog_post_tag** - 文章-标签关联表（自增主键）
6. **blog_like** - 点赞记录表（自增主键）
7. **blog_collect** - 收藏记录表（自增主键）

### 增强功能表（11张）
8. **blog_follow** - 关注关系表
9. **blog_notification** - 通知表
10. **blog_trending** - 热度统计表
11. **blog_draft** - 文章草稿表
12. **blog_report** - 内容举报表
13. **circle_post** - 校友圈动态表
14. **circle_like** - 校友圈点赞表
15. **circle_comment** - 校友圈评论表
16. **circle_repost** - 校友圈转发表
17. **media** - 媒体资源表
18. **blog_post_media** - 文章媒体关联表

## 快速开始

### 1. 环境要求

- JDK 21+ （**必须**，项目使用 Spring Boot 3.0.12 需要 JDK 21）
- Maven 3.8+ （构建工具）
- MySQL 8.0+ （数据库）
- IDEA 2024+ / Eclipse 2024+ （推荐 IntelliJ IDEA）

### 2. IDE 环境配置（详细步骤）

#### 2.1 IntelliJ IDEA 配置

**① 安装 Lombok 插件**
```
Settings → Plugins → 搜索 "Lombok" → Install → 重启 IDEA
```

**② 启用注解处理器**
```
Settings → Build, Execution, Deployment → Compiler → Annotation Processors
→ 勾选 "Enable annotation processing" → Apply
```

**③ 配置 JDK**
```
Settings → Build, Execution, Deployment → Build Tools → Maven → Runner
→ JRE 选择 JDK 21

Settings → Project → SDK → 添加 JDK 21 → 设置为 Project SDK
```

**④ 导入项目**
```
File → Open → 选择项目根目录 → 打开为 Maven 项目
IDEA 会自动识别 pom.xml 并下载依赖
```

**⑤ 配置运行环境**
```
Run → Edit Configurations → 添加新配置
→ Spring Boot → 选择 EduProjectApplication.java
→ 配置 VM options: -Dspring.profiles.active=local
```

**⑥ 配置数据库连接（可选）**
```
右侧 Database 面板 → 添加 MySQL 连接
填写 DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
```

#### 2.2 Eclipse 配置

**① 安装 Lombok**
```
下载 lombok.jar → 右键 "Open With" → Eclipse JDT Compiler
或命令行: java -jar lombok.jar install
```

**② 配置 JDK**
```
Window → Preferences → Java → Installed JREs → 添加 JDK 21
Window → Preferences → Java → Compiler → 选择 JDK 21
```

**③ 导入 Maven 项目**
```
File → Import → Maven → Existing Maven Projects
→ 选择项目根目录 → Finish
```

**④ 启用注解处理**
```
Project → Properties → Java Compiler → Annotation Processing
→ 勾选 "Enable project specific settings"
```

### 3. 数据库初始化

**① 创建数据库**
```sql
CREATE DATABASE IF NOT EXISTS campus_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**② 执行 SQL 脚本**
```bash
# 方式1: 命令行
mysql -u root -p campus_blog < 数据库表.sql

# 方式2: IDE 导入
在 MySQL Workbench 或 IDE Database 面板中打开并执行 数据库表.sql
```

### 4. 环境变量配置（必需）

项目使用 `.env` 文件管理所有敏感配置：

```bash
# 1. 复制环境变量模板
cp .env.example .env

# 2. 编辑 .env 文件，填入实际配置
# Windows PowerShell:
# copy .env.example .env

# 3. .env 文件内容示例:
DB_HOST=localhost
DB_PORT=3306
DB_NAME=campus_blog
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your-secret-key-at-least-32-characters-long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
SERVER_PORT=8825
```

**⚠️ 注意**：
- `.env` 文件已添加到 `.gitignore`，**不会提交到 Git**
- `JWT_SECRET` 至少需要 32 个字符
- 生产环境务必使用强密码

### 5. 启动项目

**方式1: IDE 启动**
```
运行 EduProjectApplication.java 的 main 方法
```

**方式2: Maven 命令行**
```bash
# 开发模式（热编译）
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/edu_project-0.0.1-SNAPSHOT.jar
```

### 6. 验证启动成功

**访问 API 文档**：
**http://localhost:8825/api/doc.html**

**默认管理员账号**：
- 用户名：`admin`
- 密码：`admin123`

### 7. 常见问题排查

| 问题 | 解决方案 |
|------|---------|
| "Java version mismatch" | 确保使用 JDK 21+，IDEA Project SDK 设置正确 |
| "Lombok not working" | 检查是否安装并启用了 Lombok 插件，注解处理器是否开启 |
| "Cannot connect to DB" | 检查 .env 配置，确认 MySQL 服务运行中 |
| "Port 8825 already in use" | 修改 SERVER_PORT 环境变量或停止占用端口的应用 |
| "Module not found" | 右键 pom.xml → Add as Maven Project |
| "Dependencies red" | Maven 面板 → Reload All Maven Projects |

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

### 标签模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/tag/list` | 获取标签列表（公开访问） |
| POST | `/api/tag` | 创建标签（需登录，body: `{"name": "标签名"}`） |

### 关注模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/follow` | 关注用户（需登录，body: `{"targetUserId": Long}`） |
| DELETE | `/api/follow/{targetUserId}` | 取消关注（需登录） |
| GET | `/api/follow/check/{targetUserId}` | 检查是否关注（需登录） |
| GET | `/api/follow/followers/{userId}` | 获取用户粉丝列表（公开访问） |
| GET | `/api/follow/following/{userId}` | 获取用户关注列表（公开访问） |
| GET | `/api/follow/counts/{userId}` | 获取粉丝/关注数量（公开访问） |

### 通知模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/notification/list` | 获取通知列表（需登录） |
| GET | `/api/notification/unread-count` | 获取未读通知数量（需登录） |
| PUT | `/api/notification/{id}/read` | 标记单条通知已读（需登录） |
| PUT | `/api/notification/read-all` | 标记全部已读（需登录） |
| DELETE | `/api/notification/{id}` | 删除通知（需登录） |

### 热门/趋势模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/trending/posts` | 获取热门文章列表（公开访问） |
| GET | `/api/trending/hot-tags` | 获取热门标签（公开访问） |
| POST | `/api/trending/update/{postId}` | 更新文章热度（需登录） |

### 草稿模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/post/draft` | 保存草稿（需登录） |
| GET | `/api/post/draft/latest` | 获取最新草稿（需登录） |
| DELETE | `/api/post/draft/{draftId}` | 删除草稿（需登录） |
| GET | `/api/post/draft/{draftId}` | 获取指定草稿（需登录） |

### 举报模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/report` | 举报内容（需登录） |
| GET | `/api/report/my` | 获取我的举报记录（需登录） |

### 管理员举报模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/admin/reports/pending` | 获取待处理举报列表（需管理员） |
| GET | `/api/admin/reports/{reportId}` | 获取举报详情（需管理员） |
| PUT | `/api/admin/reports/{reportId}` | 处理举报（需管理员） |

### 校友圈模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/circle/post` | 发布动态（需登录） |
| GET | `/api/circle/feed/recommend` | 获取推荐流（公开访问） |
| GET | `/api/circle/feed/following` | 获取关注流（需登录） |
| GET | `/api/circle/post/{postId}` | 获取动态详情（公开访问） |
| DELETE | `/api/circle/post/{postId}` | 删除动态（需登录，仅作者） |
| POST | `/api/circle/like/{postId}` | 点赞/取消点赞（需登录） |
| GET | `/api/circle/like/check/{postId}` | 检查是否已点赞（需登录） |
| GET | `/api/circle/comment/{postId}` | 获取动态评论（公开访问） |
| POST | `/api/circle/comment` | 发表评论（需登录） |
| DELETE | `/api/circle/comment/{commentId}` | 删除评论（需登录，仅作者） |
| POST | `/api/circle/repost/{postId}` | 转发动态（需登录） |
| GET | `/api/circle/search` | 搜索动态（公开访问，keyword, page, pageSize） |

### 用户模块扩展

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| PUT | `/api/user/password` | 修改密码（需登录，body: `{"oldPassword": "", "newPassword": ""}`） |
| GET | `/api/user/search` | 搜索用户（需登录，参数: `keyword`, `page`, `pageSize`） |

### 文章搜索模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/post/search/advanced` | 文章高级搜索（需登录） |
| GET | `/api/post/search/suggest` | 获取搜索建议（需登录） |

### 媒体上传模块

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/media/upload` | 上传图片/视频（需登录，支持批量） |
| GET | `/api/media/{id}` | 获取媒体详情（需登录） |
| GET | `/api/media/list` | 获取我的媒体列表（需登录） |
| DELETE | `/api/media/{id}` | 删除媒体（需登录，仅作者） |
| POST | `/api/media/upload/multiple` | 批量上传图片/视频（需登录） |
| POST | `/api/media/bind/{postId}` | 绑定媒体到文章（需登录） |
| GET | `/api/media/post/{postId}` | 获取文章的媒体列表（需登录） |

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
- 分页插件：已配置

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

### v1.26 (2026-04-26)
- 🔧 **通知系统修复**：集成 sendNotification 到点赞/评论/关注服务，通知系统正式启用
- 🔧 **校友圈 Bug 修复**：推荐流对未登录用户返回空问题修复
- 🔧 **校友圈 Bug 修复**：内容类型定义修正（1=纯文本，2=图文，3=转发）
- 🔧 **校友圈 Bug 修复**：发布动态参数校验修正，允许图文动态和纯转发动态
- 🔧 **趋势系统 Bug 修复**：SQL 分页使用 LIMIT/OFFSET，修复今日数据被排除问题
- 🔧 **趋势系统 Bug 修复**：添加 isDeleted 检查，修复大事务问题
- 🔧 **草稿系统 Bug 修复**：saveDraft 正确处理 draftId 参数
- 🔧 **草稿系统 Bug 修复**：删除草稿添加管理员权限豁免
- 🔧 **草稿系统 Bug 修复**：搜索建议改为公开功能，convertToSaveDraftRequest 增加异常处理
- 🔧 **举报系统 Bug 修复**：状态值定义统一，添加处理联动操作（封禁用户/下架文章）
- 🔧 **举报系统 Bug 修复**：添加重复举报检查，ReportVO 添加 handlerId 字段
- 🔧 **高级搜索 Bug 修复**：移除 .last("LIMIT 10") 改用 Page 对象，添加关键词长度限制
- 🔧 **媒体系统修复**：添加 Spring 文件上传配置（500MB 限制）
- 📝 文档：删除 CAMPUS_BLOG_ENHANCEMENT_PLAN.md 增强计划文档

### v1.25 (2026-04-26)
- 🔧 **安全审计修复**：CircleLikeMapper 表名错误 (circle_like → blog_circle_like)
- 🔧 **安全审计修复**：deletePost 删除时缺少级联删除关联数据
- 🔧 **安全审计修复**：toggleLike 缺少可见性权限检查
- 🔧 **安全审计修复**：getRecommendFeed 和 searchPosts 可见性过滤漏洞
- 🔧 **安全审计修复**：canViewPost 添加 NPE 防护
- 🔧 **API 修复**：Token 刷新响应格式文档修正
- 🔧 **API 修复**：登录响应新增 avatar 字段
- 🔧 **API 修复**：前端 refreshToken 示例代码缺少 Authorization header
- 🔧 **数据库增强**：为 BlogLike/BlogCollect/BlogFollow/CircleLike/BlogTrending 添加 @TableUnique 注解
- 📝 文档：CORS 配置添加生产环境安全警告
- 📝 文档：所有文档版本号更新至 v1.25

### v1.24 (2026-04-26)
- 🌐 **校友圈权限升级**：新增 visibility/allowComment/allowRepost 字段
- 🌐 **可见性控制**：0=公开，1=仅关注者，2=仅自己
- 🌐 **评论/转发权限**：可以关闭评论或禁止转发
- 🌐 **Feed 权限过滤**：推荐流/关注流/搜索/详情页都做了权限校验
- 🌐 **转发权限检查**：转发时验证用户是否有权查看原动态
- 🌐 **评论列表检查**：获取评论前验证用户是否有权查看动态
- 🌐 **转发原动态隐藏**：当转发的原动态无权查看时返回 originalPostHidden 标记
- 🔧 **端口变更**：默认端口从 80 改为 8825，可通过环境变量 SERVER_PORT 覆盖
- 📝 前端开发文档同步更新：校友圈 API 新增字段说明
- 📝 端口变更：所有文档中的 localhost:8080 改为 localhost:8825

### v1.23 (2026-04-26)
- ✨ 新增：标签创建功能 POST /api/tag，支持创建新标签
- 📝 文档：API 接口文档添加标签创建接口
- 📝 前端开发文档：完善至完整版 v1.23，覆盖全部 13 个 Controller 接口
- 📝 补充缺失接口：关注、通知、草稿、举报、校友圈、媒体、热门/趋势、标签
- 📝 新增状态管理方案：发布-订阅模式的全局状态管理
- 📝 新增错误处理：Toast 提示组件、表单前端校验
- 📝 完善 Token 刷新：集成到 Axios 拦截器自动处理
- 📝 新增页面清单：个人中心、校友圈、通知中心、管理员后台等完整页面列表
- 📝 新增开发指南：环境配置、移动端适配、安全注意事项、调试技巧
- 📝 更新技术栈：Marked.js 9.x、Highlight.js 11.x、DOMPurify 3.x

### v1.22 (2026-04-26)
- 📝 前端开发文档：完善至完整版 v1.23，覆盖全部 13 个 Controller 接口
- 📝 补充缺失接口：关注、通知、草稿、举报、校友圈、媒体、热门/趋势、标签
- 📝 新增状态管理方案：发布-订阅模式的全局状态管理
- 📝 新增错误处理：Toast 提示组件、表单前端校验
- 📝 完善 Token 刷新：集成到 Axios 拦截器自动处理
- 📝 新增页面清单：个人中心、校友圈、通知中心、管理员后台等完整页面列表
- 📝 新增开发指南：环境配置、移动端适配、安全注意事项、调试技巧
- 📝 更新技术栈：Marked.js 9.x、Highlight.js 11.x、DOMPurify 3.x

### v1.22 (2026-04-26)
- 🔒 安全修复：MediaController.getMediaInfo/getPostMedia 添加登录校验，防止越权访问
- 🔒 安全修复：GlobalExceptionHandler 兜底异常不返回异常类名，防止信息泄露
- 🔧 增强：NotificationController 分页参数添加 @Min/@Max 验证
- 🔧 增强：MediaController.bindPostMedia mediaIds 参数添加 @Size(max=20) 验证
- 🔧 增强：CORS 配置支持环境变量 CORS_ALLOWED_ORIGINS
- 🐛 修复：BlogTrending.statDate 类型从 LocalDateTime 改为 LocalDate
- 🐛 修复：CirclePost 添加缺失字段 repostUserId/repostContent/mentions
- 📝 文档：开发进度.md 更新至 v1.22

### v1.21 (2026-04-26)
- ✨ 新增校友圈搜索功能：GET /api/circle/search（关键词搜索动态）
- 🐛 修复 FollowServiceImpl 潜在 NPE：follow/unfollow 方法添加 targetUserId 和 currentUserId null 检查
- 🐛 修复搜索关键词无长度限制问题：限制最大 200 字符
- 📝 文档更新：README.md 更新至 v1.21

### v1.20 (2026-04-26)
- 🔒 安全修复：ReportServiceImpl 添加管理员权限校验
- 🔒 安全修复：SysUserServiceImpl.login 密码验证顺序修正（先检查账户状态）
- 🐛 修复 toggleLike/toggleCollect/follow/unfollow 逻辑删除+唯一约束冲突 bug
- 🐛 修复 TrendingServiceImpl.getHotTags 分页-排序错误
- 🐛 修复 MediaServiceImpl 软删除机制统一（移除手动 status 检查）
- 📝 文档更新：README.md、campus_blog.md、前端开发文档.md 等更新至 v1.20
- 📝 修复 CLAUDE.md 数据库表数量（7→18）
- 📝 移除对不存在文档 MEDIA_UPLOAD_PLAN.md 的引用

### v1.19 (2026-04-25)
- ✨ 新增社交/关注系统：BlogFollow、FollowService、FollowController
- ✨ 新增通知系统：BlogNotification、NotificationService、NotificationController
- ✨ 新增热门/趋势系统：BlogTrending、TrendingService、TrendingController
- ✨ 新增草稿自动保存：BlogDraft、SaveDraftRequest
- ✨ 新增举报管理：BlogReport、ReportService、AdminReportController
- ✨ 新增校友圈动态：CirclePost、Media、CircleService、CircleController
- ✨ 新增校友圈点赞/评论/转发：CircleLike、CircleComment、CircleRepost
- ✨ 新增修改密码和用户搜索功能
- ✨ 新增文章高级搜索和搜索建议自动补全
- ✨ 新增媒体上传功能：图片/视频上传、批量上传、自动压缩
- 🔒 修复 CircleServiceImpl.deleteComment 越权逻辑漏洞
- 🔧 实体层：BlogPostMedia 添加 @TableLogic 和 isDeleted 字段支持软删除
- 🔧 Mapper层：BlogPostMediaMapper.xml foreach 语法修复
- 🔧 媒体：MediaController 单文件上传路径修正为 /media/upload
- 🔧 软删除：CircleServiceImpl 和 BlogPostServiceImpl 多处添加 isDeleted 过滤

### v1.18 (2026-04-25)
- 🔧 数据库表结构更新：新增11张增强功能表
- 🔧 sys_user 新增 follower_count、following_count 字段
- 🔧 blog_post 新增 collectCount、cover_url 字段

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
