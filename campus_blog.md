# 校园博客论坛项目计划书 (Campus Blog Forum)

---

## 📋 项目基本信息

| 项目属性 | 内容 |
| :--- | :--- |
| **项目名称** | 校园博客论坛系统 |
| **项目类型** | 全栈 Web 应用 |
| **开发周期** | 校技能大赛周期 |
| **开发人员** | 刘畅 |
| **当前版本** | v1.36 |
| **GitHub 仓库** | https://github.com/Xinghe-0203/Campus_Blog |

---

## 1. 项目概述

### 1.1 项目背景

在校园生活中，学生需要一个能够分享学习心得、交流技术、讨论校园热点的平台。传统的社交媒体信息过于碎片化，而专业的博客论坛能够提供更深度、更具沉淀价值的内容。

### 1.2 项目目标

打造一个功能完整、界面美观、用户体验良好的全栈校园博客论坛系统，展示 Java Spring Boot 后端开发与前端 Web 技术。

### 1.3 适用场景

- 校园内的技术分享与交流
- 学生学习笔记和经验分享
- 校园资讯与热点讨论
- 学习资源共享与下载

---

## 2. 当前开发进度 ✅

| 模块 | 进度状态 | 完成度 |
| :--- | :--- | :--- |
| **📊 数据库设计** | ✅ 已完成 | 100% |
| **⚙️ 后端项目骨架** | ✅ 已完成 | 100% |
| **🛠️ 后端基础完善** | ✅ 已完成 | 100% |
| **✅ 版本兼容性修复** | ✅ 已完成 | 100% |
| **🔐 用户认证模块** | ✅ 已完成 | 100% |
| **📝 文章管理模块** | ✅ 已完成 | 100% |
| **💬 评论互动模块** | ✅ 已完成 | 100% |
| **❤️ 点赞收藏模块** | ✅ 已完成 | 100% |
| **🏷️ 标签管理模块** | ✅ 已完成 | 100% |
| **🔒 安全增强** | ✅ 已完成 | 100% |
| **👥 社交/关注系统** | ✅ 已完成 | 100% |
| **🔔 通知系统** | ✅ 已完成 | 100% |
| **📈 热门/趋势系统** | ✅ 已完成 | 100% |
| **📝 草稿自动保存** | ✅ 已完成 | 100% |
| **🚨 举报管理** | ✅ 已完成 | 100% |
| **🌐 校友圈动态** | ✅ 已完成 | 100% |
| **📷 媒体上传** | ✅ 已完成 | 100% |
| **🎨 前端页面开发** | ✅ 文档已完成 | 100%（文档） |
| **🔗 前后端联调** | ⏳ 待开发 | 0% |

### 2.1 已完成的工作

#### ✅ 数据库设计（100%）
- 21 张数据表设计（含增强功能模块）
- 完整的 SQL 初始化脚本（数据库表.sql）
- 包含示例数据（管理员账号、示例标签）
- 支持逻辑删除、自动时间戳
- 包含：关注关系、通知、热度统计、草稿、举报、校友圈、媒体上传、话题、私信等增强功能

#### ✅ 后端项目骨架（100%）
- Maven 项目结构搭建
- 核心依赖配置（MyBatis Plus、MySQL、Knife4j、Lombok、Hutool）
- 标准的包结构（controller、service、mapper、entity、config、common）
- 21 个实体类（Entity）编写完成
- 21 个 Mapper 接口编写完成
- 统一响应结果封装（Result）
- MyBatis Plus 配置（分页插件已启用）
- API 文档集成（Knife4j）
- 20 个 Controller 编写完成
- 17 个 Service 接口 + 17 个 ServiceImpl 实现类

#### ✅ 后端基础完善（2026-04-21）
- 修复联合主键实体类配置（BlogPostTag、BlogCollect、BlogLike）
- 为 BlogTag 和 BlogComment 添加 isDeleted 逻辑删除字段
- 创建 MetaObjectHandler 自动填充处理器（自动填充 createTime、updateTime）
- 创建完整的 Service 层（13 个 Service 接口 + 13 个 ServiceImpl 实现类）
- 创建全局异常处理器（GlobalExceptionHandler + BusinessException）

#### ✅ 后端增强功能（v1.8 - v1.17）
- 点赞/收藏/评论模块完整实现（含并发安全处理）
- Spring Security + JWT 认证（登录锁定、Token黑名单、刷新Token）
- XSS 防护过滤器（Jsoup）
- 标签查询功能（BlogTagController）
- 收藏数统计（collectCount）

#### ✅ 版本兼容性修复（2026-04-24）
- 解决 Spring Boot 与 MyBatis Plus 兼容性问题
- 测试多个版本组合，确定稳定方案：**Spring Boot 3.0.12 + MyBatis Plus 3.5.5**
- 项目成功启动并正常运行！
- 数据库连接正常（云端 MySQL）
- API 接口正常响应

#### ✅ 后端安全修复 v1.36（2026-04-27）
- SecurityConfig 白名单修复：新增 circle/follow/topic/user 等 11 条公开端点 permitAll 规则
- JwtAuthenticationFilter Critical 修复：Refresh Token 用于非刷新端点时返回 401
- JwtUtils 黑名单容量保护：BLACKLIST_MAX_SIZE=100_000
- BlogTagServiceImpl 管理员权限校验
- TrendingServiceImpl 仅统计已发布文章
- EmailServiceImpl 异常处理统一
- CircleController 参数校验完善

#### ✅ 后端增强完善 v1.35（2026-04-27）
- 速率限制（Rate Limiting）：基于 Caffeine 的接口频率限制拦截器
- Caffeine 缓存策略修复：SimpleCacheManager 具名缓存差异化配置
- CirclePost 统一逻辑删除：添加 is_deleted + @TableLogic 支持
- JSON 列 TypeHandler 配置：JacksonTypeHandler for CirclePost
- BlogDraft 1NF 规范化：blog_draft_tag 关联表分离草稿标签多值依赖
- BlogPostMedia 逻辑删除统一
- 外键约束参考 SQL：新增 29 条 ALTER TABLE 外键语句
- view_count 类型升级：INT → BIGINT
- 线程池参数可配置化：@Value 注入 AsyncConfig 核心参数
- 新增工具类：TimeUtils、StringMaskUtils、UserConverter

---

## 3. 需求分析

### 3.1 用户功能 (User Features)

- **用户认证**：注册、登录、个人资料修改、密码重置
- **内容发布**：支持 Markdown 格式发布文章、保存草稿
- **互动交流**：评论、点赞、收藏、阅读量统计
- **分类导航**：按技术、校园生活、资源分享等分类查看
- **标签系统**：支持多标签管理，方便内容聚合
- **搜索功能**：按标题、内容、作者搜索文章
- **个人中心**：查看我的文章、我的收藏、我的评论

### 3.2 管理功能 (Admin Features)

- **内容审核**：管理员可对违规帖子或评论进行删除或下架
- **用户管理**：封禁违规用户、重置用户密码
- **分类管理**：自定义论坛版块分类
- **数据统计**：查看平台运营数据（用户数、文章数、评论数等）

---

## 4. 技术栈选型（详细版）

### 4.1 后端技术栈 (Backend)

| 技术名称 | 版本 | 用途说明 |
| :--- | :--- | :--- |
| **Spring Boot** | 3.0.12 | 核心应用框架，提供自动配置和依赖管理 |
| **Spring Web MVC** | - | Web 层框架，提供 RESTful API 支持 |
| **MyBatis Plus** | 3.5.5 | ORM 持久层框架，MyBatis 的增强工具，极大简化数据库操作 |
| **MySQL Connector** | - | MySQL 数据库驱动 |
| **Lombok** | 1.18.40 | Java 代码简化工具，自动生成 Getter/Setter/Builder 等 |
| **Hutool** | 5.8.38 | Java 工具类库，提供字符串、日期、加密等常用工具 |
| **Knife4j** | 4.5.0 | API 文档工具，基于 Swagger 的增强版，提供美观的 UI 界面 |
| **Spring Security** | 3.0.12 | 安全认证框架 |
| **JWT (JJWT)** | 0.12.3 | JSON Web Token 认证 |

### 4.2 数据库技术 (Database)

| 技术名称 | 版本 | 用途说明 |
| :--- | :--- | :--- |
| **MySQL** | 8.0+ | 关系型数据库，存储所有业务数据（云端部署） |
| **HikariCP** | - | Spring Boot 默认连接池，性能最优 |

### 4.3 前端技术栈 (Frontend)

| 技术名称 | 版本 | 用途说明 |
| :--- | :--- | :--- |
| **Vue.js** | 3.x | 渐进式 JavaScript 框架（推荐），或使用原生 HTML5/CSS3/JavaScript |
| **HTML5** | - | 页面结构标记语言 |
| **CSS3** | - | 页面样式设计 |
| **JavaScript (ES6+)** | ES Modules | 前端交互逻辑（模块化） |
| **Axios** | 1.x | HTTP 请求库，用于前后端数据交互 |
| **Marked.js** | 9.x | Markdown 解析 |
| **Highlight.js** | 11.x | 代码高亮 |
| **DOMPurify** | 3.x | HTML 净化（XSS 防护） |
| **ECharts** | 5.x | 数据可视化图表库 |

**后端 API 现状（v1.33 已完成）：**
- Knife4j API 文档：`http://localhost:8825/api/doc.html`
- 127 个 Java 文件，完整的后端接口实现
- 认证方式：JWT Bearer Token
- 所有接口返回统一 `Result<T>` 响应格式

### 4.4 开发工具

| 工具名称 | 用途说明 |
| :--- | :--- |
| **IntelliJ IDEA / Eclipse** | Java 后端开发 IDE |
| **VS Code / WebStorm** | 前端开发 IDE |
| **Navicat / DataGrip** | 数据库管理工具 |
| **Git** | 版本控制 |
| **Maven** | 项目构建和依赖管理 |

---

## 5. 数据库设计（完整版）

### 5.1 数据库表概览

项目包含 **21 张数据表**（含增强功能模块）：

| 表名 | 中文说明 | 数据量预估 |
| :--- | :--- | :--- |
| **sys_user** | 用户表 | 中等 |
| **blog_post** | 文章/帖子表 | 大 |
| **blog_comment** | 评论表 | 大 |
| **blog_tag** | 标签表 | 小 |
| **blog_post_tag** | 文章-标签关联表 | 中 |
| **blog_like** | 点赞记录表 | 大 |
| **blog_collect** | 收藏记录表 | 中 |
| **blog_follow** | 关注关系表 | 中 |
| **blog_notification** | 通知表 | 大 |
| **blog_trending** | 热度统计表 | 中 |
| **blog_draft** | 文章草稿表 | 中 |
| **blog_draft_tag** | **草稿-标签关联表** | **小** |
| **blog_report** | 内容举报表 | 小 |
| **blog_circle_post** | 校友圈动态表 | 大 |
| **blog_circle_like** | 校友圈点赞表 | 大 |
| **blog_circle_comment** | 校友圈评论表 | 大 |
| **blog_circle_repost** | 校友圈转发表 | 中 |
| **blog_media** | 媒体资源表 | 中 |
| **blog_post_media** | 文章媒体关联表 | 中 |
| **blog_topic** | 话题表 | 小 |
| **blog_message** | 私信表 | 中 |

### 5.2 详细表结构

#### 表一：sys_user（用户表）

存储论坛的所有用户信息。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名（登录账号） |
| password | VARCHAR(100) | NOT NULL | 密码（BCrypt 加密） |
| nickname | VARCHAR(50) | NULLABLE | 用户昵称 |
| avatar | VARCHAR(255) | NULLABLE | 头像 URL |
| follower_count | INT | DEFAULT 0 | 粉丝数 |
| following_count | INT | DEFAULT 0 | 关注数 |
| email | VARCHAR(100) | NULLABLE | 邮箱地址 |
| role | VARCHAR(20) | DEFAULT 'user' | 用户角色：user/管理员 |
| status | TINYINT(1) | DEFAULT 1 | 账号状态：1=正常，0=禁用 |
| login_fail_count | INT | DEFAULT 0 | 登录失败次数 |
| lock_until | DATETIME | NULLABLE | 账户锁定截止时间 |
| create_time | DATETIME | DEFAULT NOW | 创建时间 |
| update_time | DATETIME | AUTO UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除：0=正常，1=删除 |

**索引**：
- PRIMARY KEY (id)
- UNIQUE KEY idx_username (username)

---

#### 表二：blog_post（文章/帖子表）

存储用户发布的博客文章。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | NOT NULL | 作者用户ID（外键） |
| title | VARCHAR(200) | NOT NULL | 文章标题 |
| summary | VARCHAR(500) | NULLABLE | 文章摘要 |
| cover_url | VARCHAR(500) | NULLABLE | 封面图 URL |
| content | LONGTEXT | NOT NULL | 文章内容（Markdown） |
| category | VARCHAR(50) | DEFAULT '其他' | 文章分类 |
| view_count | INT | DEFAULT 0 | 阅读量 |
| like_count | INT | DEFAULT 0 | 点赞数 |
| comment_count | INT | DEFAULT 0 | 评论数 |
| collect_count | INT | DEFAULT 0 | 收藏数 |
| status | TINYINT(1) | DEFAULT 0 | 状态：0=待审核，1=已发布，2=已驳回 |
| reviewer_id | BIGINT | NULL | 审核人ID |
| review_time | DATETIME | NULL | 审核时间 |
| reject_reason | VARCHAR(500) | NULL | 驳回原因 |
| create_time | DATETIME | DEFAULT NOW | 创建时间 |
| update_time | DATETIME | AUTO UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除 |

**索引**：
- PRIMARY KEY (id)
- INDEX idx_user_id (user_id)
- INDEX idx_category (category)
- INDEX idx_create_time (create_time)
- INDEX idx_status_deleted (status, is_deleted)

---

#### 表三：blog_comment（评论表）

存储用户对文章的评论，支持二级回复。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| post_id | BIGINT | NOT NULL | 所属文章ID（外键） |
| user_id | BIGINT | NOT NULL | 评论者用户ID（外键） |
| parent_id | BIGINT | NULLABLE | 父评论ID（NULL=一级评论） |
| content | TEXT | NOT NULL | 评论内容 |
| create_time | DATETIME | DEFAULT NOW | 创建时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除 |

**索引**：
- PRIMARY KEY (id)
- INDEX idx_post_id (post_id)
- INDEX idx_user_id (user_id)
- INDEX idx_parent_id (parent_id)

---

#### 表四：blog_tag（标签表）

存储文章的标签信息。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| name | VARCHAR(50) | NOT NULL, UNIQUE | 标签名称 |
| post_count | INT | DEFAULT 0 | 帖子数量 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除 |

**索引**：
- PRIMARY KEY (id)
- UNIQUE KEY idx_name (name)

---

#### 表五：blog_post_tag（文章-标签关联表）

实现文章和标签的多对多关系。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| post_id | BIGINT | NOT NULL | 文章ID（外键） |
| tag_id | BIGINT | NOT NULL | 标签ID（外键） |

**索引**：
- PRIMARY KEY (id)
- INDEX idx_post_id (post_id)
- INDEX idx_tag_id (tag_id)

---

#### 表六：blog_like（点赞记录表）

记录用户对文章的点赞行为。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | NOT NULL | 用户ID（外键） |
| post_id | BIGINT | NOT NULL | 文章ID（外键） |
| create_time | DATETIME | DEFAULT NOW | 点赞时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除：0=正常，1=删除 |

**索引**：
- PRIMARY KEY (id)
- INDEX idx_user_post (user_id, post_id) - 联合索引用于查询用户对文章的点赞状态
- INDEX idx_post_id (post_id)

---

#### 表七：blog_collect（收藏记录表）

记录用户收藏的文章。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | NOT NULL | 用户ID（外键） |
| post_id | BIGINT | NOT NULL | 文章ID（外键） |
| create_time | DATETIME | DEFAULT NOW | 收藏时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除：0=正常，1=删除 |

**索引**：
- PRIMARY KEY (id)
- INDEX idx_user_post (user_id, post_id) - 联合索引用于查询用户对文章的收藏状态
- INDEX idx_post_id (post_id)

---

### 5.3 ER 关系图（文字描述）

```
sys_user (用户表)
    ├── 1:N ──> blog_post (文章表)
    │           ├── 1:N ──> blog_comment (评论表)
    │           ├── N:M ──> blog_tag (标签表) [通过 blog_post_tag]
    │           ├── 1:N ──> blog_like (点赞记录)
    │           ├── 1:N ──> blog_collect (收藏记录)
    │           ├── 1:N ──> blog_draft (草稿表)
    │           └── 1:N ──> blog_post_media (文章媒体关联)
    │
    ├── 1:N ──> blog_comment (评论表) [通过 user_id]
    ├── 1:N ──> blog_like (点赞记录) [通过 user_id]
    ├── 1:N ──> blog_collect (收藏记录) [通过 user_id]
    ├── 1:N ──> blog_follow (关注关系) [作为 follower_id]
    ├── 1:N ──> blog_follow (粉丝关系) [作为 following_id]
    ├── 1:N ──> blog_notification (通知) [作为 user_id]
    ├── 1:N ──> blog_report (举报) [作为 reporter_id]
    ├── 1:N ──> blog_media (媒体资源) [作为 user_id]
    │
    └── 1:N ──> blog_circle_post (校友圈动态)

blog_circle_post (校友圈动态表)
    ├── 1:N ──> blog_circle_comment (校友圈评论)
    ├── 1:N ──> blog_circle_like (校友圈点赞)
    ├── 1:N ──> blog_circle_repost (校友圈转发)
    └── N:1 ──> blog_circle_post (转发原始动态)
```

---

## 6. 项目架构设计

### 6.1 后端架构

采用标准的 **MVC + Service + DAO** 分层架构：

```
edu_project/
├── 表现层 (Controller)
│   └── 接收前端请求，参数校验，返回响应
│
├── 业务层 (Service)
│   └── 处理核心业务逻辑（如点赞逻辑、评论树生成）
│
├── 持久层 (Mapper/DAO)
│   └── 与数据库交互，执行 CRUD 操作
│
└── 数据层 (Entity)
    └── 对应数据库表的实体类
```

### 6.2 包结构说明

```
src/main/java/com/example/edu_project/
├── EduProjectApplication.java          # 应用启动类
│
├── config/                               # 配置类
│   ├── MybatisPlusConfig.java          # MyBatis Plus 配置
│   ├── MyMetaObjectHandler.java         # 自动填充处理器
│   ├── SecurityConfig.java              # Spring Security 配置
│   ├── CaffeineCacheConfig.java         # Caffeine 本地缓存配置
│   ├── AsyncConfig.java                 # 异步任务线程池配置
│   ├── JwtSchedulerConfig.java          # JWT 黑名单定时清理
│   ├── JwtAuthenticationFilter.java     # JWT 认证过滤器
│   ├── WebMvcConfig.java                # 静态资源映射
│   ├── DotenvConfig.java                # .env 环境变量加载
│   ├── RateLimitInterceptor.java       # 频率限制拦截器
│   └── EnvValidationConfig.java          # 环境变量校验
│
├── controller/                           # Controller 层（20个）
│   ├── SysUserController.java            # 用户控制器
│   ├── BlogPostController.java            # 文章控制器
│   ├── BlogCommentController.java         # 评论控制器
│   ├── BlogLikeController.java           # 点赞控制器
│   ├── BlogCollectController.java       # 收藏控制器
│   ├── BlogTagController.java            # 标签控制器
│   ├── FollowController.java             # 关注控制器
│   ├── NotificationController.java       # 通知控制器
│   ├── TrendingController.java           # 热门控制器
│   ├── ReportController.java             # 举报控制器
│   ├── AdminReportController.java       # 管理员举报控制器
│   ├── CircleController.java             # 校友圈控制器
│   ├── MediaController.java             # 媒体控制器
│   ├── TopicController.java              # 话题控制器
│   ├── AdminStatisticsController.java    # 管理员统计控制器
│   ├── AdminPostController.java          # 管理员内容控制器
│   ├── AdminUserController.java          # 管理员用户控制器
│   ├── MessageController.java            # 私信控制器
│   └── PasswordController.java           # 密码找回控制器
│
├── service/                              # Service 层（17个）
│   ├── SysUserService.java
│   ├── BlogPostService.java
│   ├── BlogCommentService.java
│   ├── BlogTagService.java
│   ├── BlogPostTagService.java
│   ├── BlogLikeService.java
│   ├── BlogCollectService.java
│   ├── FollowService.java
│   ├── NotificationService.java
│   ├── TrendingService.java
│   ├── ReportService.java
│   ├── CircleService.java
│   ├── MediaService.java
│   ├── TopicService.java
│   ├── MessageService.java
│   ├── EmailService.java
│   └── StatisticsService.java
│
├── service/impl/                         # Service 实现类（17个）
│   ├── SysUserServiceImpl.java
│   ├── BlogPostServiceImpl.java
│   ├── BlogCommentServiceImpl.java
│   ├── BlogTagServiceImpl.java
│   ├── BlogPostTagServiceImpl.java
│   ├── BlogLikeServiceImpl.java
│   ├── BlogCollectServiceImpl.java
│   ├── FollowServiceImpl.java
│   ├── NotificationServiceImpl.java
│   ├── TrendingServiceImpl.java
│   ├── ReportServiceImpl.java
│   ├── CircleServiceImpl.java
│   ├── MediaServiceImpl.java
│   ├── TopicServiceImpl.java
│   ├── MessageServiceImpl.java
│   ├── EmailServiceImpl.java
│   └── StatisticsServiceImpl.java
│
├── mapper/                               # Mapper 层（20个）
│   ├── SysUserMapper.java
│   ├── BlogPostMapper.java
│   ├── BlogCommentMapper.java
│   ├── BlogTagMapper.java
│   ├── BlogPostTagMapper.java
│   ├── BlogLikeMapper.java
│   ├── BlogCollectMapper.java
│   ├── BlogFollowMapper.java
│   ├── BlogNotificationMapper.java
│   ├── BlogTrendingMapper.java
│   ├── BlogDraftMapper.java
│   ├── BlogDraftTagMapper.java
│   ├── BlogReportMapper.java
│   ├── CirclePostMapper.java
│   ├── CircleLikeMapper.java
│   ├── CircleCommentMapper.java
│   ├── CircleRepostMapper.java
│   ├── MediaMapper.java
│   ├── BlogPostMediaMapper.java
│   ├── TopicMapper.java
│   └── MessageMapper.java
│
├── entity/                               # Entity 实体类（20个）
│   ├── SysUser.java
│   ├── BlogPost.java
│   ├── BlogComment.java
│   ├── BlogTag.java
│   ├── BlogPostTag.java
│   ├── BlogLike.java
│   ├── BlogCollect.java
│   ├── BlogFollow.java
│   ├── BlogNotification.java
│   ├── BlogTrending.java
│   ├── BlogDraft.java
│   ├── BlogDraftTag.java
│   ├── BlogReport.java
│   ├── CirclePost.java
│   ├── CircleLike.java
│   ├── CircleComment.java
│   ├── CircleRepost.java
│   ├── Media.java
│   ├── BlogPostMedia.java
│   ├── Topic.java
│   └── Message.java
│└── common/                               # 公共类
    ├── result/
    │   └── Result.java                   # 统一响应结果封装
    └── exception/
        ├── BusinessException.java       # 业务异常
        └── GlobalExceptionHandler.java  # 全局异常处理
```

### 6.3 统一 API 响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin"
  },
  "timestamp": 1234567890123
}
```

**状态码说明**：
- `200` = 成功
- `400` = 请求参数错误
- `401` = 未登录
- `403` = 无权限
- `404` = 资源不存在
- `409` = 数据已存在，操作冲突
- `500` = 服务器内部错误

---

## 7. API 接口设计（已完成）

### 7.1 用户模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 用户注册 | POST | `/api/user/register` | ✅ 已实现 |
| 用户登录 | POST | `/api/user/login` | ✅ 已实现 |
| 刷新Token | POST | `/api/user/refresh` | ✅ 已实现 |
| 根据ID查询用户 | GET | `/api/user/{id}` | ✅ 已实现 |
| 修改密码 | PUT | `/api/user/password` | ✅ 已实现 |
| 搜索用户 | GET | `/api/user/search` | ✅ 已实现 |
| 发送验证码 | POST | `/api/user/send-code` | ✅ 已实现 |
| 重置密码 | POST | `/api/user/reset-password` | ✅ 已实现 |

### 7.2 文章模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发布文章 | POST | `/api/post` | ✅ 已实现 |
| 更新文章 | PUT | `/api/post/{id}` | ✅ 已实现 |
| 删除文章 | DELETE | `/api/post/{id}` | ✅ 已实现 |
| 获取文章详情 | GET | `/api/post/{id}` | ✅ 已实现 |
| 获取文章列表 | GET | `/api/post/list` | ✅ 已实现 |
| 增加阅读量 | PUT | `/api/post/{id}/view` | ✅ 已实现 |
| 高级搜索 | GET | `/api/post/search/advanced` | ✅ 已实现 |
| 搜索建议 | GET | `/api/post/search/suggest` | ✅ 已实现 |

### 7.3 评论模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发表评论 | POST | `/api/comment` | ✅ 已实现 |
| 获取文章评论 | GET | `/api/comment/post/{postId}` | ✅ 已实现 |
| 删除评论 | DELETE | `/api/comment/{id}` | ✅ 已实现 |

### 7.4 点赞模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 点赞/取消点赞 | POST | `/api/like/{postId}` | ✅ 已实现 |
| 检查是否已点赞 | GET | `/api/like/check/{postId}` | ✅ 已实现 |

### 7.5 收藏模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 收藏/取消收藏 | POST | `/api/collect/{postId}` | ✅ 已实现 |
| 检查收藏状态 | GET | `/api/collect/check/{postId}` | ✅ 已实现 |
| 获取我的收藏 | GET | `/api/collect/my` | ✅ 已实现 |

### 7.6 标签模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 创建标签 | POST | `/api/tag` | ✅ 已实现 |
| 获取标签列表 | GET | `/api/tag/list` | ✅ 已实现 |

### 7.7 关注模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 关注用户 | POST | `/api/follow` | ✅ 已实现 |
| 取消关注 | DELETE | `/api/follow/{targetUserId}` | ✅ 已实现 |
| 检查关注状态 | GET | `/api/follow/check/{targetUserId}` | ✅ 已实现 |
| 获取粉丝列表 | GET | `/api/follow/followers/{userId}` | ✅ 已实现 |
| 获取关注列表 | GET | `/api/follow/following/{userId}` | ✅ 已实现 |
| 获取粉丝/关注数量 | GET | `/api/follow/counts/{userId}` | ✅ 已实现 |

### 7.8 通知模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 获取通知列表 | GET | `/api/notification/list` | ✅ 已实现 |
| 获取未读数量 | GET | `/api/notification/unread-count` | ✅ 已实现 |
| 标记单条已读 | PUT | `/api/notification/{id}/read` | ✅ 已实现 |
| 标记全部已读 | PUT | `/api/notification/read-all` | ✅ 已实现 |
| 删除通知 | DELETE | `/api/notification/{id}` | ✅ 已实现 |

### 7.9 热门/趋势模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 获取热门文章 | GET | `/api/trending/posts` | ✅ 已实现 |
| 获取热门标签 | GET | `/api/trending/hot-tags` | ✅ 已实现 |
| 更新文章热度 | POST | `/api/trending/update/{postId}` | ✅ 已实现 |

### 7.10 草稿模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 保存草稿 | POST | `/api/post/draft` | ✅ 已实现 |
| 获取最新草稿 | GET | `/api/post/draft/latest` | ✅ 已实现 |
| 删除草稿 | DELETE | `/api/post/draft/{draftId}` | ✅ 已实现 |
| 获取指定草稿 | GET | `/api/post/draft/{draftId}` | ✅ 已实现 |

### 7.11 举报模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 举报内容 | POST | `/api/report` | ✅ 已实现 |
| 获取我的举报 | GET | `/api/report/my` | ✅ 已实现 |

### 7.12 管理员举报模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 获取待处理举报 | GET | `/api/admin/reports/pending` | ✅ 已实现 |
| 获取举报详情 | GET | `/api/admin/reports/{reportId}` | ✅ 已实现 |
| 处理举报 | PUT | `/api/admin/reports/{reportId}` | ✅ 已实现 |

### 7.13 校友圈模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发布动态 | POST | `/api/circle/post` | ✅ 已实现 |
| 获取推荐流 | GET | `/api/circle/feed/recommend` | ✅ 已实现 |
| 获取关注流 | GET | `/api/circle/feed/following` | ✅ 已实现 |
| 获取动态详情 | GET | `/api/circle/post/{postId}` | ✅ 已实现 |
| 删除动态 | DELETE | `/api/circle/post/{postId}` | ✅ 已实现 |
| 点赞/取消点赞 | POST | `/api/circle/like/{postId}` | ✅ 已实现 |
| 检查点赞状态 | GET | `/api/circle/like/check/{postId}` | ✅ 已实现 |
| 获取动态评论 | GET | `/api/circle/comment/{postId}` | ✅ 已实现 |
| 发表评论 | POST | `/api/circle/comment` | ✅ 已实现 |
| 删除评论 | DELETE | `/api/circle/comment/{commentId}` | ✅ 已实现 |
| 转发动态 | POST | `/api/circle/repost/{postId}` | ✅ 已实现 |
| 搜索动态 | GET | `/api/circle/search` | ✅ 已实现 |

### 7.14 媒体上传模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 上传图片/视频 | POST | `/api/media/upload` | ✅ 已实现 |
| 获取媒体详情 | GET | `/api/media/{id}` | ✅ 已实现 |
| 获取我的媒体 | GET | `/api/media/list` | ✅ 已实现 |
| 删除媒体 | DELETE | `/api/media/{id}` | ✅ 已实现 |
| 批量上传 | POST | `/api/media/upload/multiple` | ✅ 已实现 |
| 绑定媒体到文章 | POST | `/api/media/bind/{postId}` | ✅ 已实现 |
| 获取文章媒体 | GET | `/api/media/post/{postId}` | ✅ 已实现 |

### 7.15 话题管理模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 创建话题 | POST | `/api/topic` | ✅ 已实现 |
| 获取话题列表 | GET | `/api/topic/list` | ✅ 已实现 |
| 获取热门话题 | GET | `/api/topic/hot` | ✅ 已实现 |
| 获取话题详情 | GET | `/api/topic/{topicId}` | ✅ 已实现 |
| 获取话题下的动态列表 | GET | `/api/topic/{topicId}/posts` | ✅ 已实现 |

### 7.16 内容审核模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 文章列表 | GET | `/api/admin/post/list` | ✅ 已实现 |
| 审核通过文章 | PUT | `/api/admin/post/{id}/approve` | ✅ 已实现 |
| 驳回文章 | PUT | `/api/admin/post/{id}/reject` | ✅ 已实现 |
| 删除文章 | DELETE | `/api/admin/post/{id}` | ✅ 已实现 |
| 评论列表 | GET | `/api/admin/comment/list` | ✅ 已实现 |
| 删除评论 | DELETE | `/api/admin/comment/{id}` | ✅ 已实现 |

### 7.17 私信模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发送私信 | POST | `/api/message/send` | ✅ 已实现 |
| 获取收到的私信 | GET | `/api/message/received` | ✅ 已实现 |
| 获取发送的私信 | GET | `/api/message/sent` | ✅ 已实现 |
| 标记已读 | PUT | `/api/message/{id}/read` | ✅ 已实现 |
| 删除私信 | DELETE | `/api/message/{id}` | ✅ 已实现 |
| 获取未读数量 | GET | `/api/message/unread-count` | ✅ 已实现 |

### 7.18 密码找回模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发送验证码 | POST | `/api/user/send-code` | ✅ 已实现 |
| 重置密码 | POST | `/api/user/reset-password` | ✅ 已实现 |

### 7.19 管理员统计模块

| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 获取平台统计数据 | GET | `/api/admin/statistics` | ✅ 已实现 |

---

## 8. 开发计划与里程碑

| 阶段 | 任务 | 目标 | 状态 |
| :--- | :--- | :--- | :--- |
| **✅ 第一阶段** | 数据库与环境搭建 | 完成 MySQL 表创建，初始化 Spring Boot 项目骨架 | ✅ 已完成 |
| **✅ 第二阶段** | 版本兼容性修复 | 解决依赖冲突，确定稳定版本组合 | ✅ 已完成 |
| **✅ 第三阶段** | 用户认证模块 | 实现用户注册、登录、密码加密 | ✅ 已完成 |
| **✅ 第四阶段** | 文章管理模块 | 实现文章的增删改查接口 | ✅ 已完成 |
| **✅ 第五阶段** | 互动功能模块 | 实现评论、点赞、收藏功能 | ✅ 已完成 |
| **✅ 第六阶段** | 安全认证加固 | 启用 Spring Security + JWT | ✅ 已完成 |
| **🚧 第七阶段** | 增强功能开发 | 社交/关注、通知、热门/趋势、草稿、举报、校友圈、媒体上传 | ✅ 已完成 |
| **✅ 第八阶段** | 后端增强完善 | 内容审核、私信、密码找回、@提及、话题标签、单元测试、Actuator、Caffeine缓存、异步线程池 | ✅ 已完成 |
| **⏳ 第九阶段** | 前端页面开发 | 编写 HTML/CSS/Vue，实现响应式布局和 Markdown 集成 | ⏳ 待开始 |
| **⏳ 第十阶段** | 前后端联调 | 使用 Axios 将前端页面与后端接口连通 | ⏳ 待开始 |
| **⏳ 第十一阶段** | 优化与美化 | 加入 ECharts 统计图表，进行 UI 细节打磨 | ⏳ 待开始 |
| **⏳ 第十二阶段** | 测试与修复 | 功能测试、Bug 修复、性能优化 | ⏳ 待开始 |

---

## 9. 创新亮点（惊艳点）

1. **Markdown 全栈支持**
   - 后端存储 Markdown 源码
   - 前端实现实时预览
   - 支持代码高亮、数学公式等

2. **多级树形评论**
   - 支持评论回复功能
   - 展示复杂的逻辑处理能力
   - 二级嵌套设计

3. **响应式 UI 设计**
   - 一套代码适配电脑、平板、手机屏幕
   - 使用 Bootstrap 5 栅格系统
   - 移动端优先的设计理念

4. **可视化统计**
   - 在个人中心展示文章阅读量和获赞趋势图
   - 使用 ECharts 实现美观的数据可视化
   - 管理员数据统计大屏

5. **API 文档自动生成**
   - 使用 Knife4j 生成美观的 API 文档
   - 支持在线调试接口
   - 提升开发效率和可维护性

---

## 10. 部署说明

### 10.1 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### 10.2 环境变量配置（必需）

部署前必须设置以下环境变量，否则应用无法启动：

| 环境变量 | 说明 | 示例 |
| :--- | :--- | :--- |
| `DB_PASSWORD` | 数据库密码 | `your_db_password` |
| `JWT_SECRET` | JWT签名密钥（建议32位以上） | `your_jwt_secret_key_here` |

**Linux/Mac 设置：**
```bash
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret_key_here
```

**Windows CMD 设置：**
```cmd
set DB_PASSWORD=your_db_password
set JWT_SECRET=your_jwt_secret_key_here
```

**Windows PowerShell 设置：**
```powershell
$env:DB_PASSWORD="your_db_password"
$env:JWT_SECRET="your_jwt_secret_key_here"
```

**IDE 环境变量配置：**
在运行配置中添加上述环境变量。

### 10.3 本地数据库配置

如需本地数据库，修改 `src/main/resources/application.yml` 中的数据库连接信息（同时确保环境变量已设置）

### 10.4 访问地址

- 应用地址：http://localhost:8825/api
- API 文档：http://localhost:8825/api/doc.html

### 10.5 默认账号

- 用户名：`admin`
- 密码：`admin123`
- 前提：数据库中已存在该用户

---

## 11. 注意事项

### 11.1 当前开发阶段说明

- Spring Security + JWT 已启用，需正确配置环境变量
- 分页插件已启用
- 所有敏感接口需要 JWT Token 认证

### 11.2 安全配置（必需）

- 密码使用 BCrypt 加密存储（强度12轮）
- JWT Token 身份认证
- 敏感信息通过环境变量配置（禁止硬编码）
- 防止 SQL 注入（使用 MyBatis Plus 参数化查询）
- 防止 XSS 攻击（前端转义、后端过滤）
- Entity 实体类密码字段添加 @JsonIgnore 防止序列化泄露
- 登录失败锁定机制（连续5次失败锁定15分钟）
- JWT Token 黑名单机制（支持主动撤销Token）
- JWT 刷新Token机制（7天有效期的refreshToken）
- 阅读量防刷：已登录用户用userId，未登录用户用IP+User-Agent哈希

### 11.3 性能优化（后续版本）

- 使用 Redis 缓存热点数据（可选）
- 数据库查询优化（索引、分页）
- 静态资源 CDN 加速（可选）

---

## 12. 项目文件清单

```
edu_project/
├── campus_blog.md                          # 本项目计划书
├── README.md                               # 项目根目录说明文档
├── CLAUDE.md                               # Claude AI 协作文档
├── 数据库表                               # 数据库初始化 SQL 脚本
├── pom.xml                                 # Maven 依赖配置
└── src/main/
    ├── java/com/example/edu_project/
    │   ├── EduProjectApplication.java
    │   ├── config/
    │   │   ├── MybatisPlusConfig.java
    │   │   ├── MyMetaObjectHandler.java
    │   │   ├── SecurityConfig.java
│   │   └── RateLimitInterceptor.java
    │   ├── controller/                           # Controller 层（20个）
    │   │   ├── SysUserController.java
    │   │   ├── BlogPostController.java
    │   │   ├── BlogCommentController.java
    │   │   ├── BlogLikeController.java
    │   │   ├── BlogCollectController.java
    │   │   ├── BlogTagController.java
    │   │   ├── FollowController.java
    │   │   ├── NotificationController.java
    │   │   ├── TrendingController.java
    │   │   ├── ReportController.java
    │   │   ├── AdminReportController.java
    │   │   ├── CircleController.java
    │   │   └── MediaController.java
    │   ├── entity/                               # Entity 实体类（21个）
    │   │   ├── SysUser.java
    │   │   ├── BlogPost.java
    │   │   ├── BlogComment.java
    │   │   ├── BlogTag.java
    │   │   ├── BlogPostTag.java
    │   │   ├── BlogLike.java
    │   │   ├── BlogCollect.java
    │   │   ├── BlogFollow.java
    │   │   ├── BlogNotification.java
    │   │   ├── BlogTrending.java
    │   │   ├── BlogDraft.java
    │   │   ├── BlogReport.java
    │   │   ├── CirclePost.java
    │   │   ├── CircleLike.java
    │   │   ├── CircleComment.java
    │   │   ├── CircleRepost.java
    │   │   ├── Media.java
    │   │   ├── BlogPostMedia.java
	│   │   ├── Topic.java
	│   │   ├── Message.java
	│   │   └── BlogDraftTag.java
    │   ├── mapper/                               # Mapper 层（21个）
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
    │   │   ├── BlogPostMediaMapper.java
	│   │   ├── TopicMapper.java
	│   │   ├── MessageMapper.java
	│   │   └── BlogDraftTagMapper.java
    │   ├── dto/
    │   │   ├── UserRegisterRequest.java
    │   │   └── UserLoginRequest.java
    │   ├── vo/
    │   │   └── UserLoginResponse.java
    │   ├── utils/
    │   │   ├── JwtUtils.java              # JWT 工具类
    │   │   ├── SecurityUtils.java         # 安全工具类
    │   │   ├── UserContext.java          # 用户上下文
    │   │   ├── HtmlSanitizer.java        # XSS 防护
	│   │   ├── TimeUtils.java            # 时间工具类
	│   │   ├── StringMaskUtils.java      # 字符串脱敏工具
	│   │   └── UserConverter.java        # 用户对象转换工具
    │   └── service/                              # Service 层（17个）
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
    │   │   └── MediaService.java
    │   ├── service/impl/                         # ServiceImpl 层（17个）
    │   │   ├── SysUserServiceImpl.java
    │   │   ├── BlogPostServiceImpl.java
    │   │   ├── BlogCommentServiceImpl.java
    │   │   ├── BlogTagServiceImpl.java
    │   │   ├── BlogPostTagServiceImpl.java
    │   │   ├── BlogLikeServiceImpl.java
    │   │   ├── BlogCollectServiceImpl.java
    │   │   ├── FollowServiceImpl.java
    │   │   ├── NotificationServiceImpl.java
    │   │   ├── TrendingServiceImpl.java
    │   │   ├── ReportServiceImpl.java
    │   │   ├── CircleServiceImpl.java
    │   │   └── MediaServiceImpl.java
    │   └── common/
    │       ├── result/
    │       │   └── Result.java
    │       └── exception/
    │           ├── BusinessException.java
    │           └── GlobalExceptionHandler.java
    └── resources/
        └── application.yml
```

---

## 13. 开发规范

### 13.1 开发八荣八耻

- 以瞎清接口为耻，以认真查询为荣
- 以模糊执行为耻，以寻求确认为荣
- 以想业务为耻，以人类确认为荣
- 以创造接口为耻，以复用现有为荣
- 以跳过验证为耻，以主动测试为荣
- 以破坏架构为耻，以遵循规范为荣
- 以假装理解为耻，以诚实无知为荣
- 以盲目修改为耻，以谨慎重构为荣
- 以忘记更新文档为耻，以及时更新为荣

### 13.2 版本更新规范

每次更新代码后，必须更新以下文档：
- README.md
- campus_blog.md

---

## 14. 更新日志

| 日期 | 版本 | 更新内容 |
| :--- | :--- | :--- |
| 2026-04-27 | v1.36 | 🔧 **SecurityConfig 白名单修复**：新增 11 条 permitAll 规则（circle/follow/topic/user 公开端点）<br>🔧 **JwtAuthenticationFilter Critical 修复**：Refresh Token 用于非刷新端点时返回 401 而非匿名通过<br>🔧 **JwtUtils 黑名单容量保护**：BLACKLIST_MAX_SIZE=100_000 防止内存无限增长<br>🔧 **BlogTagServiceImpl 管理员权限**：deleteTag 添加管理员校验，listAllTags 添加只读事务<br>🔧 **TrendingServiceImpl 状态校验**：updatePostTrending 仅统计已发布文章（status=1）<br>🔧 **EmailServiceImpl 异常处理**：verifyCode/sendHtmlEmail 统一使用 BusinessException<br>🔧 **CircleController 参数校验**：searchPosts 的 keyword 参数添加 @NotBlank 验证 |
| 2026-04-27 | v1.35 | ✨ **速率限制**：新增 `RateLimitInterceptor` 基于 Caffeine 的接口频率限制<br>✨ **缓存策略修复**：SimpleCacheManager 具名缓存差异化配置<br>✨ **CirclePost 逻辑删除统一**：添加 is_deleted + @TableLogic 支持<br>✨ **JSON 列 TypeHandler**：JacksonTypeHandler 配置处理 CirclePost JSON 字段<br>✨ **BlogDraft 1NF 规范化**：新建 `blog_draft_tag` 关联表分离草稿标签多值依赖<br>✨ **BlogPostMedia 逻辑删除统一**：统一软删除机制<br>✨ **外键约束参考 SQL**：新增 29 条 ALTER TABLE 外键语句<br>✨ **view_count 类型升级**：INT → BIGINT<br>✨ **线程池参数可配置化**：@Value 注入 AsyncConfig 核心参数<br>✨ **新增工具类**：TimeUtils、StringMaskUtils、UserConverter<br>📦 **新增表**：`blog_draft_tag`（第21张表）<br>📦 **新增 Mapper/Entity**：`BlogDraftTagMapper`、`BlogDraftTag` |
| 2026-04-27 | v1.34 | ✨ **内容审核流程**：新增 `AdminPostController` 和 `AdminCommentController`（审核文章/评论列表、修改状态、删除）<br>✨ **私信功能**：新增 `Message` 实体、`MessageService`、`MessageController`（发送/接收/已读/删除私信、未读计数）<br>✨ **密码找回功能**：新增 `EmailService` 和 `EmailServiceImpl`（发送HTML邮件、验证码管理）<br>新增 `PasswordController`（`/user/send-code`、`/user/reset-password`）<br>新增 `SendCodeRequest`、`ResetPasswordRequest` DTO<br>添加 `spring-boot-starter-mail` 依赖，邮件配置支持环境变量<br>验证码5分钟有效期、3次验证尝试、60秒发送间隔限制<br>🔧 **Entity修复**：`TopicMapper.java` 和 `MessageMapper.java` 移除（已改为实体类 `Topic.java` 和 `Message.java`）<br>🐛 **测试配置修复**：H2数据库支持、Flyway配置修正 |
| 2026-04-27 | v1.32 | **P0 安全修复**：CircleServiceImpl XSS过滤、MediaServiceImpl Magic Number校验、SysUser.toString()密码泄露、multipart配置修正<br>**P0 管理员接口**：新增用户列表/封禁接口 AdminUserController<br>**P1 功能完善**：@提及通知(targetId bug修复)、话题标签完整实现<br>**P1 单元测试**：SysUserServiceImplTest、JwtUtilsTest、GlobalExceptionHandlerTest<br>**P1 配置增强**：Spring Boot Actuator健康检查、多环境配置(application-dev/prod.yml)、logback日志配置<br>**P2 性能优化**：N+1查询优化、@PreAuthorize权限控制集中化<br>**P2 架构完善**：Caffeine本地缓存、AsyncConfig异步线程池、AdminStatisticsController数据统计<br>**部署文档**：DEPLOY.md、Dockerfile、docker-compose.yml |
| 2026-04-24 | v1.6 | 全面安全加固<br>密码字段添加@JsonIgnore防泄露<br>getById返回UserVO替代SysUser<br>敏感信息改为环境变量<br>添加防刷机制和权限校验<br>Entity联合主键和逻辑删除修复 |
| 2026-04-24 | v1.5 | 安全与质量问题修复<br>添加@Valid参数校验<br>修复分层架构违规<br>添加密码复杂度校验<br>修复N+1查询问题<br>优化关联数据清理<br>完善异常处理机制 |
| 2026-04-24 | v1.4 | 实现用户注册、登录功能<br>启用 Spring Security + JWT 认证<br>新增 JwtUtils 工具类<br>新增 DTO/VO 层<br>BCrypt 密码加密存储<br>JWT Token 身份认证 |
| 2026-04-24 | v1.3 | 修复 Spring Boot 与 MyBatis Plus 兼容性问题<br>确定稳定版本组合：Spring Boot 3.0.12 + MyBatis Plus 3.5.5<br>暂时注释 Spring Security 和 JWT 依赖（开发阶段）<br>注释分页插件配置<br>项目成功启动并正常运行<br>完善所有文档 |
| 2026-04-21 | v1.2 | 完善后端基础架构<br>修复联合主键实体类配置问题<br>添加逻辑删除字段到 BlogTag 和 BlogComment<br>创建 MetaObjectHandler 自动填充处理器<br>创建完整的 Service 层（7个接口 + 7个实现类）<br>创建全局异常处理器<br>添加 Spring Security 和 JWT 依赖 |
| 2026-04-21 | v1.1 | 初始化项目计划书，完成数据库设计和后端项目骨架搭建<br>修复 Spring Boot 版本兼容性问题（4.0.5 → 3.3.5）<br>项目成功上传到 GitHub：https://github.com/Xinghe-0203/Campus_Blog |

---

## 15. 联系方式

- **开发人员**：刘畅
- **项目路径**：`D:\MyCode\edu_project`

---

| 2026-04-26 | v1.22 | 🔒 安全修复：MediaController.getMediaInfo/getPostMedia 添加登录校验<br>🔒 安全修复：GlobalExceptionHandler 兜底异常不返回异常类名<br>🔧 增强：NotificationController 分页参数添加 @Min/@Max 验证<br>🔧 增强：MediaController.bindPostMedia @Size 验证<br>🔧 增强：CORS 配置支持环境变量 CORS_ALLOWED_ORIGINS<br>🐛 修复：BlogTrending.statDate 类型改为 LocalDate<br>🐛 修复：CirclePost 添加缺失字段 repostUserId/repostContent/mentions |
| 2026-04-26 | v1.21 | ✨ 新增校友圈搜索功能（GET /api/circle/search）<br>🐛 修复 FollowServiceImpl 潜在 NPE（添加 targetUserId 和 currentUserId null 检查）<br>🐛 修复搜索关键词无长度限制问题（限制最大 200 字符） |
| 2026-04-26 | v1.20 | 🔒 ReportServiceImpl 添加管理员权限校验<br>🔒 SysUserServiceImpl.login 密码验证顺序修正<br>🐛 修复 toggleLike/toggleCollect/follow/unfollow 逻辑删除+唯一约束冲突 bug<br>🐛 修复 TrendingServiceImpl.getHotTags 分页-排序错误<br>🐛 修复 MediaServiceImpl 软删除机制统一<br>📝 文档更新至 v1.20 |
| 2026-04-25 | v1.19 | 新增社交/关注系统（BlogFollow、FollowService、FollowController）<br>新增通知系统（BlogNotification、NotificationService、NotificationController）<br>新增热门/趋势系统（BlogTrending、TrendingService、TrendingController）<br>新增草稿自动保存（BlogDraft、SaveDraftRequest）<br>新增举报管理（BlogReport、ReportService、AdminReportController）<br>新增校友圈动态（CirclePost、Media、CircleService、CircleController）<br>新增校友圈点赞/评论/转发（CircleLike、CircleComment、CircleRepost）<br>新增修改密码和用户搜索功能（PUT /user/password, GET /user/search）<br>新增文章高级搜索和搜索建议（GET /post/search/advanced, GET /post/search/suggest）<br>新增媒体上传功能（图片/视频上传、批量上传、自动压缩）<br>🔒 修复 CircleServiceImpl.deleteComment 越权逻辑漏洞<br>🔧 BlogPostMedia 添加 @TableLogic 和 isDeleted 字段支持软删除<br>🔧 BlogPostMediaMapper.xml foreach 语法修复<br>🔧 MediaController 单文件上传路径修正为 /media/upload<br>🔧 CircleServiceImpl 和 BlogPostServiceImpl 多处添加 isDeleted 过滤 |
| 2026-04-25 | v1.18 | 数据库更新：新增11张增强功能表（关注、通知、热度、草稿、举报、校友圈、媒体）<br>sys_user新增follower_count、following_count字段<br>blog_post新增collectCount、cover_url字段<br>blog_tag新增postCount字段<br>更新campus_blog.md文档（18张表、ER图、开发进度） |
| 2026-04-25 | v1.17 | 新增标签查询功能<br>添加BlogTagService接口和BlogTagController GET /tag/list<br>SecurityConfig添加/tag/**的permitAll规则 |
| 2026-04-25 | v1.16 | BlogPost新增collectCount字段<br>BlogCollectServiceImpl.toggleCollect()正确更新收藏数<br>getPostDetail未发布文章返回"文章未发布"<br>移除JwtUtils.getUserIdFromRequest()和SecurityUtils.getCurrentUserRole()死代码 |
| 2026-04-25 | v1.15 | 修复JWT黑名单验证绕过漏洞<br>修复isTokenExpired()异常处理<br>修复JwtAuthenticationFilter签名验证顺序<br>修复refresh token rotation<br>修复logger.warn格式 |
| 2026-04-25 | v1.14 | 安全与并发修复<br>修复点赞/收藏锁内存泄漏（添加主动清理过期锁）<br>修复阅读量增加TOCTOU竞态条件（使用CAS操作）<br>统一密码最小长度为8<br>移除DotenvConfig硬编码路径<br>添加category字段XSS防护<br>移除所有Controller的@CrossOrigin注解 |
| 2026-04-25 | v1.13 | .env配置支持与环境变量校验<br>新增DotenvConfig自动加载.env文件<br>新增EnvValidationConfig启动时校验环境变量<br>新增.env.example配置模板<br>添加.env到.gitignore<br>移除所有代码中的硬编码默认值 |
| 2026-04-25 | v1.12 | 安全修复与文档更新<br>移除JWT/Database密码硬编码默认值<br>BlogLikeServiceImpl添加updatedPost空指针检查<br>BlogCommentServiceImpl添加评论递归深度限制<br>移除未使用的convertToDetailResponse死代码<br>移除未使用的generateToken和containsDangerousTags方法 |
| 2026-04-25 | v1.11 | 安全与性能优化<br>JwtAuthenticationFilter添加Token撤销检查和权限列表<br>HtmlSanitizer移除data:协议防止XSS bypass<br>登录锁定信息通用化防用户枚举<br>CommentCreateRequest添加@NotNull校验<br>BlogCommentServiceImpl修复O(n²)查询为O(n) |
| 2026-04-25 | v1.10 | 安全增强与代码完善<br>修复用户枚举漏洞（通用错误信息）<br>修复点赞/收藏竞态条件（细粒度锁 + DuplicateKeyException处理）<br>新增XSS防护（Jsoup过滤）<br>完善@Transactional注解<br>修复batchInsertPostTags事务缺失问题 |
| 2026-04-25 | v1.9 | 安全增强与并发修复<br>修复IP伪造漏洞（IP+User-Agent指纹）<br>修复点赞竞态条件（DuplicateKeyException处理）<br>修复评论删除级联问题（递归删除子评论）<br>新增登录失败锁定机制（5次失败锁定15分钟，原子更新并发安全）<br>提升BCrypt强度至12轮<br>新增JWT Token黑名单机制（支持主动撤销Token）<br>新增JWT刷新Token机制（7天有效期+Rotation）<br>新增JWT黑名单定时清理（每小时）<br>新增刷新Token接口POST /api/user/refresh |
| 2026-04-25 | v1.8 | 新增点赞/收藏/评论模块<br>支持发表评论/回复/树形结构展示<br>支持点赞/取消点赞自动更新计数<br>支持收藏/取消收藏和我的收藏列表<br>管理员可删除任意评论 |
| 2026-04-26 | v1.25 | 🔧 安全审计修复：CircleLikeMapper表名错误(circle_like→blog_circle_like)<br>🔧 安全审计修复：deletePost级联删除关联数据<br>🔧 安全审计修复：toggleLike可见性权限检查<br>🔧 安全审计修复：getRecommendFeed/searchPosts可见性过滤漏洞<br>🔧 安全审计修复：canViewPost添加NPE防护<br>🔧 API修复：Token刷新响应格式文档修正<br>🔧 API修复：登录响应新增avatar字段<br>🔧 API修复：refreshToken前端示例添加Authorization header<br>🔧 数据库增强：为实体添加唯一约束注解(实际由数据库保证) |
| 2026-04-26 | v1.22 | 🔒 安全修复：MediaController添加getMediaInfo/getPostMedia登录校验<br>🔒 安全修复：GlobalExceptionHandler兜底异常不返回异常类名<br>🔧 增强：NotificationController分页参数添加@Min/@Max验证<br>🔧 增强：MediaController.bindPostMedia添加@Size验证<br>🔧 增强：CORS配置支持环境变量CORS_ALLOWED_ORIGINS<br>🐛 修复：BlogTrending.statDate类型改为LocalDate<br>🐛 修复：CirclePost添加缺失字段repostUserId/repostContent/mentions |

---

**文档版本**：v1.36
**最后更新**：2026-04-27
