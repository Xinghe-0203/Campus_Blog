# 校园博客论坛项目计划书 (Campus Blog Forum)

---

## 📋 项目基本信息

| 项目属性 | 内容 |
| :--- | :--- |
| **项目名称** | 校园博客论坛系统 |
| **项目类型** | 全栈 Web 应用 |
| **开发周期** | 校技能大赛周期 |
| **开发人员** | 刘畅 |
| **当前版本** | v1.0-SNAPSHOT |

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
| **🔐 用户认证模块** | ⏳ 待开发 | 0% |
| **📝 文章管理模块** | ⏳ 待开发 | 0% |
| **💬 评论互动模块** | ⏳ 待开发 | 0% |
| **❤️ 点赞收藏模块** | ⏳ 待开发 | 0% |
| **🎨 前端页面开发** | ⏳ 待开发 | 0% |
| **🔗 前后端联调** | ⏳ 待开发 | 0% |

### 2.1 已完成的工作

#### ✅ 数据库设计（100%）
- 7 张核心数据表设计
- 完整的 SQL 初始化脚本
- 包含示例数据（管理员账号、示例标签）
- 支持逻辑删除、自动时间戳

#### ✅ 后端项目骨架（100%）
- Maven 项目结构搭建
- 核心依赖配置（MyBatis Plus、MySQL、Knife4j、Lombok、Hutool）
- 标准的包结构（controller、service、mapper、entity、config、common）
- 7 个实体类（Entity）编写完成
- 7 个 Mapper 接口编写完成
- 统一响应结果封装（Result）
- MyBatis Plus 配置（分页插件）
- API 文档集成（Knife4j）
- 测试 Controller 编写完成

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
| **Spring Boot** | 4.0.5 | 核心应用框架，提供自动配置和依赖管理 |
| **Spring Web MVC** | - | Web 层框架，提供 RESTful API 支持 |
| **MyBatis Plus** | 3.5.9 | ORM 持久层框架，MyBatis 的增强工具，极大简化数据库操作 |
| **MySQL Connector** | - | MySQL 数据库驱动 |
| **Lombok** | 最新 | Java 代码简化工具，自动生成 Getter/Setter/Builder 等 |
| **Hutool** | 5.8.38 | Java 工具类库，提供字符串、日期、加密等常用工具 |
| **Knife4j** | 4.5.0 | API 文档工具，基于 Swagger 的增强版，提供美观的 UI 界面 |
| **Springdoc OpenAPI** | - | OpenAPI 3.0 规范支持 |

### 4.2 数据库技术 (Database)

| 技术名称 | 版本 | 用途说明 |
| :--- | :--- | :--- |
| **MySQL** | 8.0+ | 关系型数据库，存储所有业务数据 |
| **HikariCP** | - | Spring Boot 默认连接池，性能最优 |

### 4.3 前端技术栈 (Frontend)

| 技术名称 | 版本 | 用途说明 |
| :--- | :--- | :--- |
| **HTML5** | - | 页面结构标记语言 |
| **CSS3** | - | 页面样式设计 |
| **JavaScript (ES6+)** | - | 前端交互逻辑 |
| **Bootstrap** | 5.x | UI 组件框架，提供响应式布局和现成组件 |
| **jQuery** | - | DOM 操作和 AJAX 请求（可选） |
| **Axios** | - | HTTP 请求库，用于前后端数据交互 |
| **Font Awesome** | - | 图标库 |
| **Editor.md / Vditor** | - | Markdown 编辑器，支持实时预览 |
| **ECharts** | - | 数据可视化图表库 |

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

项目包含 **7 张核心数据表**：

| 表名 | 中文说明 | 数据量预估 |
| :--- | :--- | :--- |
| **sys_user** | 用户表 | 中等 |
| **blog_post** | 文章/帖子表 | 大 |
| **blog_comment** | 评论表 | 大 |
| **blog_tag** | 标签表 | 小 |
| **blog_post_tag** | 文章-标签关联表 | 中 |
| **blog_like** | 点赞记录表 | 大 |
| **blog_collect** | 收藏记录表 | 中 |

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
| email | VARCHAR(100) | NULLABLE | 邮箱地址 |
| role | VARCHAR(20) | DEFAULT 'user' | 用户角色：user/管理员 |
| status | TINYINT(1) | DEFAULT 1 | 账号状态：1=正常，0=禁用 |
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
| content | LONGTEXT | NOT NULL | 文章内容（Markdown） |
| category | VARCHAR(50) | DEFAULT '其他' | 文章分类 |
| view_count | INT | DEFAULT 0 | 阅读量 |
| like_count | INT | DEFAULT 0 | 点赞数 |
| comment_count | INT | DEFAULT 0 | 评论数 |
| status | TINYINT(1) | DEFAULT 1 | 状态：1=发布，0=草稿，2=下架 |
| create_time | DATETIME | DEFAULT NOW | 发布时间 |
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
| create_time | DATETIME | DEFAULT NOW | 评论时间 |

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

**索引**：
- PRIMARY KEY (id)
- UNIQUE KEY idx_name (name)

---

#### 表五：blog_post_tag（文章-标签关联表）
实现文章和标签的多对多关系。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| post_id | BIGINT | PK, NOT NULL | 文章ID（外键） |
| tag_id | BIGINT | PK, NOT NULL | 标签ID（外键） |

**索引**：
- PRIMARY KEY (post_id, tag_id)

---

#### 表六：blog_like（点赞记录表）
记录用户对文章的点赞行为。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| user_id | BIGINT | PK, NOT NULL | 用户ID（外键） |
| post_id | BIGINT | PK, NOT NULL | 文章ID（外键） |
| create_time | DATETIME | DEFAULT NOW | 点赞时间 |

**索引**：
- PRIMARY KEY (user_id, post_id)
- INDEX idx_post_id (post_id)

---

#### 表七：blog_collect（收藏记录表）
记录用户收藏的文章。

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| user_id | BIGINT | PK, NOT NULL | 用户ID（外键） |
| post_id | BIGINT | PK, NOT NULL | 文章ID（外键） |
| create_time | DATETIME | DEFAULT NOW | 收藏时间 |

**索引**：
- PRIMARY KEY (user_id, post_id)
- INDEX idx_post_id (post_id)

---

### 5.3 ER 关系图（文字描述）

```
sys_user (用户表)
    ├── 1:N ──> blog_post (文章表)
    │           ├── 1:N ──> blog_comment (评论表)
    │           ├── N:M ──> blog_tag (标签表) [通过 blog_post_tag]
    │           ├── 1:N ──> blog_like (点赞记录)
    │           └── 1:N ──> blog_collect (收藏记录)
    │
    ├── 1:N ──> blog_comment (评论表)
    │
    ├── 1:N ──> blog_like (点赞记录)
    │
    └── 1:N ──> blog_collect (收藏记录)
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
│   └── MybatisPlusConfig.java           # MyBatis Plus 配置
│
├── controller/                           # Controller 层（API 接口）
│   ├── SysUserController.java            # 用户相关接口
│   ├── BlogPostController.java           # 文章相关接口
│   ├── BlogCommentController.java        # 评论相关接口
│   └── ...
│
├── service/                              # Service 层（业务逻辑）
│   ├── SysUserService.java
│   ├── BlogPostService.java
│   └── ...
│
├── service/impl/                         # Service 实现类
│   ├── SysUserServiceImpl.java
│   ├── BlogPostServiceImpl.java
│   └── ...
│
├── mapper/                               # Mapper 层（数据库操作）
│   ├── SysUserMapper.java
│   ├── BlogPostMapper.java
│   └── ...
│
├── entity/                               # Entity 实体类
│   ├── SysUser.java
│   ├── BlogPost.java
│   └── ...
│
├── common/                               # 公共类
│   ├── result/
│   │   └── Result.java                  # 统一响应结果封装
│   └── exception/                        # 异常处理（待实现）
│
└── utils/                                # 工具类（待实现）
    ├── JwtUtils.java                    # JWT 工具
    └── ...
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
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未登录
- `403` - 无权限
- `404` - 资源不存在
- `500` - 服务器内部错误

---

## 7. API 接口设计（规划）

### 7.1 用户模块
| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 用户注册 | POST | /api/user/register | 新用户注册 |
| 用户登录 | POST | /api/user/login | 用户登录（返回 Token） |
| 获取用户信息 | GET | /api/user/info | 获取当前登录用户信息 |
| 更新用户信息 | PUT | /api/user/info | 更新用户个人资料 |
| 获取用户列表 | GET | /api/user/list | 获取用户列表（管理员） |

### 7.2 文章模块
| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发布文章 | POST | /api/post | 发布新文章 |
| 获取文章详情 | GET | /api/post/{id} | 获取文章详情 |
| 获取文章列表 | GET | /api/post/list | 获取文章列表（分页） |
| 搜索文章 | GET | /api/post/search | 搜索文章 |
| 更新文章 | PUT | /api/post/{id} | 更新文章 |
| 删除文章 | DELETE | /api/post/{id} | 删除文章 |
| 增加阅读量 | PUT | /api/post/{id}/view | 文章阅读量+1 |

### 7.3 评论模块
| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 发表评论 | POST | /api/comment | 发表评论 |
| 获取文章评论 | GET | /api/comment/post/{postId} | 获取文章的所有评论 |
| 删除评论 | DELETE | /api/comment/{id} | 删除评论 |

### 7.4 点赞模块
| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 点赞/取消点赞 | POST | /api/like/{postId} | 点赞或取消点赞 |
| 检查是否已点赞 | GET | /api/like/check/{postId} | 检查用户是否已点赞文章 |

### 7.5 收藏模块
| 接口 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 收藏/取消收藏 | POST | /api/collect/{postId} | 收藏或取消收藏 |
| 获取我的收藏 | GET | /api/collect/my | 获取我的收藏列表 |

---

## 8. 开发计划与里程碑

| 阶段 | 任务 | 目标 | 预计时间 |
| :--- | :--- | :--- | :--- |
| **✅ 第一阶段** | 数据库与环境搭建 | 完成 MySQL 表创建，初始化 Spring Boot 项目骨架 | 已完成 |
| **⏳ 第二阶段** | 用户认证模块 | 实现用户注册、登录、JWT 认证 | 待开始 |
| **⏳ 第三阶段** | 文章管理模块 | 实现文章的增删改查接口 | 待开始 |
| **⏳ 第四阶段** | 互动功能模块 | 实现评论、点赞、收藏功能 | 待开始 |
| **⏳ 第五阶段** | 前端页面开发 | 编写 HTML/CSS，实现响应式布局和 Markdown 集成 | 待开始 |
| **⏳ 第六阶段** | 前后端联调 | 使用 Axios 将前端页面与后端接口连通 | 待开始 |
| **⏳ 第七阶段** | 优化与美化 | 加入 ECharts 统计图表，进行 UI 细节打磨 | 待开始 |
| **⏳ 第八阶段** | 测试与修复 | 功能测试、Bug 修复、性能优化 | 待开始 |

---

## 9. 创新亮点（惊艳点）

1. **Markdown 全栈支持**
   - 后端存储 Markdown 源码
   - 前端实现实时预览
   - 支持代码高亮、数学公式等

2. **多级树形评论**
   - 支持评论回复功能
   - 展示复杂的逻辑处理能力
   - 无限层级或二级嵌套设计

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

### 10.2 配置步骤
1. 修改 `application.yml` 中的数据库连接信息
2. 运行 `数据库表` 文件初始化数据库
3. 执行 `mvn clean package` 打包项目
4. 运行生成的 JAR 文件：`java -jar edu_project.jar`

### 10.3 访问地址
- 应用地址：http://localhost:8080
- API 文档：http://localhost:8080/api/doc.html

---

## 11. 注意事项

### 11.1 安全注意事项
- 密码必须使用 BCrypt 加密存储
- 敏感接口需要 JWT Token 认证
- 防止 SQL 注入（使用 MyBatis Plus 参数化查询）
- 防止 XSS 攻击（前端转义、后端过滤）

### 11.2 性能优化
- 使用 Redis 缓存热点数据（可选）
- 数据库查询优化（索引、分页）
- 静态资源 CDN 加速（可选）

---

## 12. 项目文件清单

```
edu_project/
├── Campus_Blog_Forum_Project_Plan.md      # 本项目计划书
├── 数据库表                                 # 数据库初始化 SQL 脚本
├── edu_project/
│   ├── README.md                            # 项目 README 文档
│   ├── pom.xml                              # Maven 依赖配置
│   └── src/main/
│       ├── java/com/example/edu_project/   # Java 源代码
│       └── resources/
│           └── application.yml              # 应用配置文件
```

---

## 13. 联系方式

- **开发人员**：刘畅
- **项目路径**：`c:\Users\Zora\OneDrive\Desktop\mycode\edu_project`

---

## 14. 更新日志

| 日期 | 版本 | 更新内容 |
| :--- | :--- | :--- |
| 2026-04-21 | v1.0 | 初始化项目计划书，完成数据库设计和后端项目骨架搭建 |

---

**文档版本**：v1.0  
**最后更新**：2026-04-21
#   C a m p u s _ B l o g  
 