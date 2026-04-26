-- ============================================================================
-- 校园博客论坛系统 - 数据库初始化脚本 (Campus Blog Forum System)
-- ============================================================================
-- 作者: Zora / 刘畅
-- 创建日期: 2025-07-01
-- 更新日期: 2026-04-25
-- 数据库版本: MySQL 8.0 及以上
-- 字符集: utf8mb4 (支持 emoji 表情和特殊字符)
-- 说明:
--   本脚本用于创建校园博客论坛系统的核心数据表。
--   采用"逻辑删除"策略，确保数据可追溯，防止误删。
--   所有表均使用 InnoDB 引擎，保证事务支持和行级锁。
-- ============================================================================


-- -------------------------------------------------------
-- 第一步：创建数据库（如果不存在）
-- -------------------------------------------------------
-- 如果已存在同名数据库，请删除后重新运行，或将此处注释掉
CREATE DATABASE IF NOT EXISTS `campus_blog`
    DEFAULT CHARACTER SET utf8mb4          -- 设置默认字符集为 utf8mb4，支持存储中文和 emoji
    DEFAULT COLLATE utf8mb4_unicode_ci;   -- 排序规则，使用 unicode 排序，兼容性好

-- 切换到当前数据库
USE `campus_blog`;


-- ============================================================================
-- 表一：用户表 (sys_user)
-- ============================================================================
-- 【业务说明】
--   存储论坛的所有用户信息，包括普通学生用户和管理员用户。
--   用户可以进行注册、登录、发布文章、发表评论、点赞等操作。
--
-- 【设计要点】
--   1. 用户名 (username) 设置唯一索引，防止重复注册。
--   2. 密码 (password) 存储加密后的值（实际开发中应使用 BCrypt 加密）。
--   3. 头像 (avatar) 存储头像图片的 URL 地址，默认为 NULL 表示未上传。
--   4. 使用逻辑删除 (is_deleted) 而非物理删除，保留用户历史数据。
--   5. create_time 和 update_time 自动维护，记录用户创建和最后修改时间。
--   6. follower_count 和 following_count 为关注系统冗余计数字段。
--
DROP TABLE IF EXISTS `sys_user`;  -- 如果表已存在，则先删除（重新建表时使用）

CREATE TABLE `sys_user` (
    -- -------------------- 基础信息字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长，用于唯一标识每个用户',

    `username`          VARCHAR(50)     NOT NULL                     COMMENT '用户名，用于登录系统的唯一账号名',

    `password`          VARCHAR(100)   NOT NULL                     COMMENT '密码，经过加密处理后的存储值（实际项目应使用 BCrypt 加密存储）',

    `nickname`          VARCHAR(50)    DEFAULT NULL                  COMMENT '用户昵称，用于在论坛中显示的名称，不强制要求，可为空',

    `avatar`            VARCHAR(255)   DEFAULT NULL                  COMMENT '用户头像图片的 URL 地址链接，默认为 NULL 表示使用系统默认头像',

    -- -------------------- 社交统计字段（关注系统） --------------------
    `follower_count`    INT             DEFAULT 0                    COMMENT '粉丝数，关注该用户的用户数量',

    `following_count`   INT             DEFAULT 0                    COMMENT '关注数，该用户关注的用户数量',

    `email`             VARCHAR(100)   DEFAULT NULL                  COMMENT '用户邮箱地址，用于找回密码或接收系统通知，可为空',

    -- -------------------- 权限与状态字段 --------------------
    `role`              VARCHAR(20)    DEFAULT 'user'                COMMENT '用户角色标识：user=普通用户，admin=管理员。可扩展其他角色',

    `status`            TINYINT(1)    DEFAULT 1                    COMMENT '账号状态：1=正常启用，0=禁用封禁。管理员可修改此字段来封禁违规用户',

    `login_fail_count`  INT            DEFAULT 0                     COMMENT '登录失败次数，连续失败5次后账户将被锁定',

    `lock_until`        DATETIME       DEFAULT NULL                  COMMENT '账户锁定截止时间，锁定期间无法登录，为NULL表示未锁定',

    -- -------------------- 时间戳字段 --------------------
    `create_time`       DATETIME       DEFAULT CURRENT_TIMESTAMP    COMMENT '用户注册时间，自动取当前时间戳，记录用户创建时间',

    `update_time`       DATETIME       DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '用户信息最后更新时间，当记录被修改时自动更新',

    -- -------------------- 逻辑删除字段 --------------------
    `is_deleted`        TINYINT(1)    DEFAULT 0                     COMMENT '逻辑删除标记：0=正常数据（未删除），1=已删除数据。避免物理删除，保留数据可追溯性',

    -- -------------------- 索引约束 --------------------
    PRIMARY KEY (`id`),                                                 -- 主键索引，加速按 ID 查询
    UNIQUE KEY `idx_username` (`username`)                              -- 唯一索引，保证用户名不重复，用于快速登录校验

) ENGINE=InnoDB                                                      -- 使用 InnoDB 引擎，支持事务和行级锁
  DEFAULT CHARSET=utf8mb4                                             -- 字符集，支持存储中文、emoji 等
  COMMENT='用户信息表：存储论坛所有用户的账号、密码和个人资料信息';


-- ============================================================================
-- 表二：帖子/文章表 (blog_post)
-- ============================================================================
-- 【业务说明】
--   存储用户发布的博客文章，是论坛的核心内容表。
--   支持 Markdown 格式撰写文章正文，包含阅读量、点赞数等互动统计。
--
-- 【设计要点】
--   1. 文章内容 (content) 使用 LONGTEXT 类型，支持存储大型 Markdown 文本。
--   2. 摘要 (summary) 字段用于在列表页展示文章概要，避免加载全文影响性能。
--   3. 阅读量 (view_count) 和点赞数 (like_count) 在列表查询时频繁使用，
--      与文章基本信息一起存储，避免频繁 JOIN 查询（以空间换时间）。
--   4. 分类 (category) 字段用于简单的分类筛选，如"技术分享"、"校园生活"等。
--   5. user_id 外键关联用户表，记录文章作者信息。
--   6. collect_count 收藏数、cover_url 封面图 为增强功能字段。
--
DROP TABLE IF EXISTS `blog_post`;

CREATE TABLE `blog_post` (
    -- -------------------- 基础信息字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长，唯一标识每篇帖子',

    `user_id`           BIGINT          NOT NULL                     COMMENT '作者用户ID，关联 sys_user 表的外键，记录文章发布者',

    `title`             VARCHAR(200)    NOT NULL                     COMMENT '文章标题，最大200字符，在列表页和详情页展示',

    `summary`           VARCHAR(500)   DEFAULT NULL                  COMMENT '文章摘要/简介，最多500字符，用于列表页快速展示文章大意',

    `cover_url`        VARCHAR(500)   DEFAULT NULL                  COMMENT '文章封面图 URL，用于列表页展示封面',

    `content`           LONGTEXT       NOT NULL                     COMMENT '文章正文内容，存储 Markdown 格式的原始文本，支持大型富文本',

    `category`          VARCHAR(50)    DEFAULT '其他'                 COMMENT '文章所属分类，如"技术分享"、"校园生活"、"资源下载"等，默认为"其他"',

    -- -------------------- 互动统计字段 --------------------
    `view_count`        INT             DEFAULT 0                    COMMENT '文章阅读量/浏览次数，每次进入详情页时自增，用于展示热门文章',

    `like_count`        INT             DEFAULT 0                    COMMENT '文章点赞数量，用户点赞时自增，用于展示受欢迎程度',

    `comment_count`     INT             DEFAULT 0                    COMMENT '文章评论数量，实时统计该文章的评论总数，避免频繁 COUNT 查询',

    `collect_count`     INT             DEFAULT 0                    COMMENT '文章收藏数量，用户收藏时自增',

    -- -------------------- 状态与时间字段 --------------------
    `status`            TINYINT(1)     DEFAULT 1                    COMMENT '文章状态：1=已发布（公开），0=草稿（仅作者可见），2=已下架（管理员隐藏）',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '文章发布时间，自动记录首次发布的时间',

    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '文章最后修改时间，当文章被编辑时自动更新',

    -- -------------------- 逻辑删除字段 --------------------
    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除标记：0=正常文章，1=已删除（被作者或管理员删除）',

    -- -------------------- 索引约束 --------------------
    PRIMARY KEY (`id`),                                                 -- 主键索引
    INDEX `idx_user_id` (`user_id`),                                    -- 按用户查询其所有文章
    INDEX `idx_category` (`category`),                                  -- 按分类筛选文章
    INDEX `idx_create_time` (`create_time`),                           -- 按时间排序（最新发布）
    INDEX `idx_status_deleted` (`status`, `is_deleted`)                -- 复合索引，加速公开文章的查询

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='帖子/文章表：存储用户发布的博客文章内容，支持 Markdown 格式';


-- ============================================================================
-- 表三：评论表 (blog_comment)
-- ============================================================================
-- 【业务说明】
--   存储用户对文章的评论内容，支持多级回复（树形结构）。
--   通过 parent_id 字段实现评论的嵌套回复功能。
--
-- 【设计要点】
--   1. parent_id 字段实现树形评论结构：
--      - parent_id = NULL，表示这是一级评论（直接评论文章）。
--      - parent_id = 某评论ID，表示这是对该评论的回复（二级评论）。
--   2. content 字段存储评论的纯文本内容。
--   3. 支持逻辑删除，删除评论时会级联删除所有子评论。
--   4. 删除评论时使用逻辑删除 (is_deleted)，保留互动记录可追溯。
--
DROP TABLE IF EXISTS `blog_comment`;

CREATE TABLE `blog_comment` (
    -- -------------------- 基础信息字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长，唯一标识每条评论',

    `post_id`           BIGINT          NOT NULL                     COMMENT '所属文章ID，关联 blog_post 表，表示该评论属于哪篇文章',

    `user_id`           BIGINT          NOT NULL                     COMMENT '评论者用户ID，关联 sys_user 表，记录发表评论的用户',

    `parent_id`         BIGINT          DEFAULT NULL                  COMMENT '父评论ID，用于实现回复功能。NULL=一级评论（非回复），有值=回复某条评论',

    `content`           TEXT            NOT NULL                     COMMENT '评论内容，存储评论的文本，支持较长内容',

    -- -------------------- 时间戳字段 --------------------
    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '评论发布时间，自动记录',

    -- -------------------- 逻辑删除字段 --------------------
    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除标记：0=正常评论，1=已删除',

    -- -------------------- 索引约束 --------------------
    PRIMARY KEY (`id`),                                                 -- 主键索引
    INDEX `idx_post_id` (`post_id`),                                    -- 按文章查询所有评论
    INDEX `idx_user_id` (`user_id`),                                    -- 按用户查询其所有评论
    INDEX `idx_parent_id` (`parent_id`)                                -- 按父评论ID查询回复列表

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='评论表：存储用户对文章的评论，支持多级回复（树形结构）';


-- ============================================================================
-- 表四：标签表 (blog_tag)
-- ============================================================================
-- 【业务说明】
--   存储文章的标签信息，如"Java"、"Spring Boot"、"校园"等。
--   标签用于对文章进行多维度归类，方便用户按兴趣筛选。
--
-- 【设计要点】
--   1. 标签名 (name) 设置唯一索引，防止重复添加同名标签。
--   2. 标签表本身不存储文章关联关系，而是通过中间表 blog_post_tag 实现多对多关联。
--   3. 支持逻辑删除，删除标签时保留历史关联记录。
--   4. post_count 为该标签下的文章数量冗余计数字段。
--
DROP TABLE IF EXISTS `blog_tag`;

CREATE TABLE `blog_tag` (
    -- -------------------- 基础信息字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长，唯一标识每个标签',

    `name`              VARCHAR(50)    NOT NULL                     COMMENT '标签名称，如"Java"、"Python"、"校园生活"等，唯一不重复',

    -- -------------------- 统计字段 --------------------
    `post_count`        INT             DEFAULT 0                    COMMENT '该标签下的文章数量，用于展示热门标签',

    -- -------------------- 逻辑删除字段 --------------------
    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除标记：0=正常标签，1=已删除',

    -- -------------------- 索引约束 --------------------
    PRIMARY KEY (`id`),                                                 -- 主键索引
    UNIQUE KEY `idx_name` (`name`)                                      -- 唯一索引，保证标签名不重复，便于精确查询

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='标签表：存储文章的分类标签，支持文章多标签管理';


-- ============================================================================
-- 表五：帖子-标签关联表 (blog_post_tag)
-- ============================================================================
-- 【业务说明】
--   实现帖子表和标签表之间的多对多关联关系。
--   一篇文章可以拥有多个标签（如"Java"+"Spring Boot"），
--   一个标签也可以关联多篇文章。
--
-- 【设计要点】
--   1. 使用自增主键 id，便于管理中间表记录。
--   2. 联合唯一索引 (post_id, tag_id)，保证同一篇文章不会重复关联同一标签。
--   3. 查询某篇文章的所有标签：SELECT tag.* FROM blog_tag tag
--      JOIN blog_post_tag pt ON tag.id = pt.tag_id WHERE pt.post_id = ?
--   4. 查询某个标签下的所有文章：SELECT post.* FROM blog_post post
--      JOIN blog_post_tag pt ON post.id = pt.post_id WHERE pt.tag_id = ?
--
DROP TABLE IF EXISTS `blog_post_tag`;

CREATE TABLE `blog_post_tag` (
    -- -------------------- 主键字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长',

    -- -------------------- 关联字段 --------------------
    `post_id`           BIGINT          NOT NULL                     COMMENT '帖子ID，关联 blog_post 表的外键',

    `tag_id`            BIGINT          NOT NULL                     COMMENT '标签ID，关联 blog_tag 表的外键',

    -- -------------------- 主键和唯一约束 --------------------
    PRIMARY KEY (`id`),                                               -- 主键索引
    UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`)                    -- 联合唯一索引，保证 (文章+标签) 组合唯一

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='帖子-标签关联表（中间表）：实现文章与标签的多对多关联关系';


-- ============================================================================
-- 表六：点赞记录表 (blog_like)
-- ============================================================================
-- 【业务说明】
--   记录用户对文章的点赞行为，用于精确统计和防止重复点赞。
--   与 blog_post 表中的 like_count 字段配合使用。
--
-- 【设计要点】
--   1. 使用自增主键 id，便于管理记录。
--   2. 联合唯一索引 (user_id, post_id) 保证同一用户对同一文章只能点赞一次。
--   3. 与 blog_post.like_count 的区别：
--      - like_count 是冗余字段，用于快速排序和展示。
--      - blog_like 是明细表，用于精确控制和记录。
--   4. 删除点赞时，需要同时更新 blog_post.like_count 字段。
--
DROP TABLE IF EXISTS `blog_like`;

CREATE TABLE `blog_like` (
    -- -------------------- 主键字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长',

    -- -------------------- 关联字段 --------------------
    `user_id`           BIGINT          NOT NULL                     COMMENT '点赞用户ID，关联 sys_user 表',

    `post_id`           BIGINT          NOT NULL                     COMMENT '被点赞的文章ID，关联 blog_post 表',

    -- -------------------- 时间戳字段 --------------------
    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '点赞时间，记录用户点赞的时间点',

    -- -------------------- 逻辑删除字段 --------------------
    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除标记：0=正常，1=删除',

    -- -------------------- 主键和唯一约束 --------------------
    PRIMARY KEY (`id`),                                               -- 主键索引
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),                -- 联合唯一索引，防止重复点赞
    INDEX `idx_post_id` (`post_id`)                                    -- 按文章查询点赞用户列表

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='点赞记录表：记录用户对文章的点赞行为，防止重复点赞';


-- ============================================================================
-- 表七：收藏记录表 (blog_collect)
-- ============================================================================
-- 【业务说明】
--   记录用户收藏的文章，便于用户稍后阅读。
--
-- 【设计要点】
--   1. 使用自增主键 id，便于管理记录。
--   2. 联合唯一索引 (user_id, post_id) 保证同一用户对同一文章只能收藏一次。
--   3. 支持逻辑删除。
--
DROP TABLE IF EXISTS `blog_collect`;

CREATE TABLE `blog_collect` (
    -- -------------------- 主键字段 --------------------
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID，自增长',

    -- -------------------- 关联字段 --------------------
    `user_id`           BIGINT          NOT NULL                     COMMENT '收藏用户ID',

    `post_id`           BIGINT          NOT NULL                     COMMENT '被收藏的文章ID',

    -- -------------------- 时间戳字段 --------------------
    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '收藏时间',

    -- -------------------- 逻辑删除字段 --------------------
    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除标记：0=正常，1=删除',

    -- -------------------- 主键和唯一约束 --------------------
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    INDEX `idx_post_id` (`post_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='收藏记录表：记录用户收藏的文章，便于稍后阅读';


-- ============================================================================
-- 以下为功能增强模块新增的表（v1.17+）
-- ============================================================================


-- ============================================================================
-- 表八：关注关系表 (blog_follow)
-- ============================================================================
-- 【业务说明】
--   记录用户之间的关注关系，实现社交功能。
--   用户可以关注其他用户，关注后可查看关注用户的动态。
--
-- 【设计要点】
--   1. follower_id 为关注者，following_id 为被关注者。
--   2. 联合唯一索引 (follower_id, following_id) 防止重复关注。
--   3. 支持逻辑删除，取消关注时标记为已删除。
--   4. sys_user 表的 follower_count 和 following_count 为冗余计数字段。
--
DROP TABLE IF EXISTS `blog_follow`;

CREATE TABLE `blog_follow` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `follower_id`       BIGINT          NOT NULL                     COMMENT '关注者用户ID',

    `following_id`       BIGINT          NOT NULL                     COMMENT '被关注者用户ID',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '关注时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
    INDEX `idx_following_id` (`following_id`),
    INDEX `idx_create_time` (`create_time`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='关注关系表：记录用户之间的关注关系';


-- ============================================================================
-- 表九：通知表 (blog_notification)
-- ============================================================================
-- 【业务说明】
--   存储用户的通知消息，包括关注、点赞、评论、提及等类型。
--   用户可以在通知中心查看所有通知。
--
-- 【设计要点】
--   1. type 字段区分通知类型：follow/like/comment/mention/system。
--   2. from_user_id 为触发通知的用户，user_id 为接收通知的用户。
--   3. target_type 和 target_id 关联到具体的内容（文章/评论）。
--   4. 支持逻辑删除。
--
DROP TABLE IF EXISTS `blog_notification`;

CREATE TABLE `blog_notification` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '通知所属用户ID（接收者）',

    `type`              VARCHAR(50)     NOT NULL                     COMMENT '通知类型：follow/like/comment/mention/system',

    `title`             VARCHAR(200)    NOT NULL                     COMMENT '通知标题',

    `content`           VARCHAR(500)   DEFAULT NULL                  COMMENT '通知内容',

    `from_user_id`      BIGINT          DEFAULT NULL                  COMMENT '触发通知的用户ID（发送者）',

    `target_type`       VARCHAR(50)    DEFAULT NULL                  COMMENT '关联目标类型：post/comment',

    `target_id`         BIGINT          DEFAULT NULL                  COMMENT '关联目标ID',

    `is_read`           TINYINT(1)     DEFAULT 0                     COMMENT '是否已读：0=未读，1=已读',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_unread` (`user_id`, `is_read`),
    INDEX `idx_create_time` (`create_time`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='通知表：存储用户的站内通知消息';


-- ============================================================================
-- 表十：文章热度表 (blog_trending)
-- ============================================================================
-- 【业务说明】
--   记录文章每日热度分数，用于热门推荐和趋势展示。
--
-- 【设计要点】
--   1. 每天凌晨定时计算并更新热度分数。
--   2. 热度公式：score = view*1 + like*5 + comment*10。
--   3. 不使用逻辑删除，统计数据按日期自然覆盖。
--
DROP TABLE IF EXISTS `blog_trending`;

CREATE TABLE `blog_trending` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `post_id`           BIGINT          NOT NULL                     COMMENT '文章ID',

    `score`             DOUBLE          NOT NULL    DEFAULT 0         COMMENT '热度分数',

    `view_count`        INT             NOT NULL    DEFAULT 0         COMMENT '当日阅读数',

    `like_count`        INT             NOT NULL    DEFAULT 0         COMMENT '当日点赞数',

    `comment_count`     INT             NOT NULL    DEFAULT 0         COMMENT '当日评论数',

    `date`              DATE            NOT NULL                     COMMENT '统计日期',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_date` (`post_id`, `date`),
    INDEX `idx_score` (`score` DESC)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='文章热度表：用于热门推荐和趋势展示';


-- ============================================================================
-- 表十一：文章草稿表 (blog_draft)
-- ============================================================================
-- 【业务说明】
--   存储用户的文章草稿，支持自动保存功能。
--
DROP TABLE IF EXISTS `blog_draft`;

CREATE TABLE `blog_draft` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '用户ID',

    `title`             VARCHAR(200)   DEFAULT NULL                  COMMENT '草稿标题',

    `content`           TEXT            DEFAULT NULL                  COMMENT '草稿内容',

    `summary`           VARCHAR(500)   DEFAULT NULL                  COMMENT '草稿摘要',

    `category`          VARCHAR(50)    DEFAULT NULL                  COMMENT '草稿分类',

    `tag_ids`           VARCHAR(200)   DEFAULT NULL                  COMMENT '草稿标签ID（逗号分隔）',

    `post_id`           BIGINT          DEFAULT NULL                  COMMENT '关联的文章ID（编辑已有文章时使用）',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_post_id` (`post_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='文章草稿表：存储用户的文章草稿，支持自动保存';


-- ============================================================================
-- 表十二：内容举报表 (blog_report)
-- ============================================================================
-- 【业务说明】
--   记录用户举报的内容（文章/评论/用户），供管理员审核处理。
--
-- 【设计要点】
--   1. status 字段：0=待处理，1=已处理，2=已驳回。
--   2. 支持逻辑删除。
--
DROP TABLE IF EXISTS `blog_report`;

CREATE TABLE `blog_report` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `reporter_id`       BIGINT          NOT NULL                     COMMENT '举报人ID',

    `reported_user_id`   BIGINT          DEFAULT NULL                  COMMENT '被举报用户ID',

    `target_type`       VARCHAR(50)     NOT NULL                     COMMENT '举报目标类型：post/comment/user',

    `target_id`         BIGINT          NOT NULL                     COMMENT '举报目标ID',

    `reason`            VARCHAR(500)    NOT NULL                     COMMENT '举报原因',

    `status`            TINYINT(1)     DEFAULT 0                     COMMENT '处理状态：0=待处理，1=已驳回，2=已核实',

    `handler_id`        BIGINT          DEFAULT NULL                  COMMENT '处理人ID（管理员）',

    `handler_result`    VARCHAR(500)   DEFAULT NULL                  COMMENT '处理结果',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `handle_time`       DATETIME        DEFAULT NULL                  COMMENT '处理时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    INDEX `idx_reporter_id` (`reporter_id`),
    INDEX `idx_status` (`status`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='内容举报表：记录用户举报的内容，供管理员审核';


-- ============================================================================
-- 表十三：校友圈动态表 (blog_circle_post)
-- ============================================================================
-- 【业务说明】
--   校友圈是一个类似微博/Twitter 的独立社交动态流功能。
--   支持发布短文本动态、图片动态和转发。
--
-- 【设计要点】
--   1. content_type：1=纯文本，2=图片，3=转发。
--   2. image_urls 存储 JSON 格式的图片 URL 数组（最多9张）。
--   3. tags 存储 JSON 格式的话题标签数组。
--   4. mentions 存储 JSON 格式的 @提及用户 ID 数组。
--   5. 使用 status=2 表示删除，与逻辑删除字段配合。
--
DROP TABLE IF EXISTS `blog_circle_post`;

CREATE TABLE `blog_circle_post` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '发布者用户ID',

    `content`           VARCHAR(500)   NOT NULL                     COMMENT '动态内容（最多500字）',

    `content_type`       TINYINT(1)    DEFAULT 1                     COMMENT '内容类型：1=纯文本，2=图片，3=转发',

    `image_urls`        JSON            DEFAULT NULL                  COMMENT '图片URL数组（最多9张）',

    `repost_id`         BIGINT          DEFAULT NULL                  COMMENT '转发的动态ID',

    `repost_user_id`    BIGINT          DEFAULT NULL                  COMMENT '被转发者用户ID',

    `repost_content`    VARCHAR(500)   DEFAULT NULL                  COMMENT '转发时添加的内容',

    `tags`              JSON            DEFAULT NULL                  COMMENT '话题标签数组',

    `mentions`          JSON            DEFAULT NULL                  COMMENT '@提及的用户ID数组',

    `location`          VARCHAR(100)   DEFAULT NULL                  COMMENT '位置信息',

    `like_count`        INT             DEFAULT 0                    COMMENT '点赞数',

    `comment_count`     INT             DEFAULT 0                    COMMENT '评论数',

    `repost_count`      INT             DEFAULT 0                    COMMENT '转发数',

    `view_count`        INT             DEFAULT 0                    COMMENT '查看数',

    `is_top`            TINYINT(1)     DEFAULT 0                     COMMENT '是否置顶：0=否，1=是',

    `visibility`        TINYINT(1)     DEFAULT 0                     COMMENT '可见性：0=公开，1=仅关注者，2=仅自己',

    `allow_comment`     TINYINT(1)     DEFAULT 1                     COMMENT '是否允许评论：1=允许，0=不允许',

    `allow_repost`      TINYINT(1)     DEFAULT 1                     COMMENT '是否允许转发：1=允许，0=不允许',

    `status`            TINYINT(1)     DEFAULT 1                     COMMENT '状态：1=正常，0=隐藏，2=删除',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',

    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_like_count` (`like_count` DESC),
    INDEX `idx_repost_id` (`repost_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='校友圈动态表：类似微博的短动态发布功能';


-- ============================================================================
-- 表十四：校友圈点赞表 (blog_circle_like)
-- ============================================================================
-- 【业务说明】
--   记录用户对校友圈动态的点赞行为。
--
DROP TABLE IF EXISTS `blog_circle_like`;

CREATE TABLE `blog_circle_like` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '点赞用户ID',

    `post_id`           BIGINT          NOT NULL                     COMMENT '动态ID',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '点赞时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    INDEX `idx_post_id` (`post_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='校友圈点赞表：记录用户对校友圈动态的点赞';


-- ============================================================================
-- 表十五：校友圈评论表 (blog_circle_comment)
-- ============================================================================
-- 【业务说明】
--   存储校友圈动态的评论，支持二级回复。
--
DROP TABLE IF EXISTS `blog_circle_comment`;

CREATE TABLE `blog_circle_comment` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `post_id`           BIGINT          NOT NULL                     COMMENT '动态ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '评论者用户ID',

    `content`           VARCHAR(500)   NOT NULL                     COMMENT '评论内容',

    `parent_id`         BIGINT          DEFAULT NULL                  COMMENT '父评论ID（二级回复）',

    `reply_to_user_id`  BIGINT          DEFAULT NULL                  COMMENT '回复给的用户ID',

    `like_count`        INT             DEFAULT 0                    COMMENT '点赞数',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_parent_id` (`parent_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='校友圈评论表：支持二级回复的评论功能';


-- ============================================================================
-- 表十六：校友圈转发表 (blog_circle_repost)
-- ============================================================================
-- 【业务说明】
--   记录校友圈动态的转发关系。
--
DROP TABLE IF EXISTS `blog_circle_repost`;

CREATE TABLE `blog_circle_repost` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '转发者用户ID',

    `original_post_id`   BIGINT          NOT NULL                     COMMENT '原始动态ID',

    `new_post_id`        BIGINT          NOT NULL                     COMMENT '新动态ID（转发生成的新动态）',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '转发时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    INDEX `idx_original_post_id` (`original_post_id`),
    INDEX `idx_user_id` (`user_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='校友圈转发表：记录动态转发关系';


-- ============================================================================
-- 表十七：媒体资源表 (blog_media)
-- ============================================================================
-- 【业务说明】
--   存储用户上传的图片、视频等媒体文件。
--
DROP TABLE IF EXISTS `blog_media`;

CREATE TABLE `blog_media` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `user_id`           BIGINT          NOT NULL                     COMMENT '上传用户ID',

    `file_name`         VARCHAR(255)    NOT NULL                     COMMENT '原始文件名',

    `file_path`         VARCHAR(500)    NOT NULL                     COMMENT '存储路径',

    `file_url`          VARCHAR(500)    NOT NULL                     COMMENT '访问URL',

    `file_type`         VARCHAR(50)     NOT NULL                     COMMENT '文件类型：image/video',

    `mime_type`         VARCHAR(100)    NOT NULL                     COMMENT 'MIME类型',

    `file_size`         BIGINT          NOT NULL                     COMMENT '文件大小（字节）',

    `width`             INT             DEFAULT NULL                  COMMENT '图片宽度（仅图片）',

    `height`            INT             DEFAULT NULL                  COMMENT '图片高度（仅图片）',

    `thumb_url`         VARCHAR(500)    DEFAULT NULL                  COMMENT '视频缩略图URL',

    `status`            TINYINT(1)     DEFAULT 1                     COMMENT '状态：1=正常，0=禁用',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    `update_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',

    `is_deleted`        TINYINT(1)     DEFAULT 0                     COMMENT '逻辑删除：0=正常，1=删除',

    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_file_type` (`file_type`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='媒体资源表：存储用户上传的图片、视频等文件';


-- ============================================================================
-- 表十八：文章媒体关联表 (blog_post_media)
-- ============================================================================
-- 【业务说明】
--   实现文章和媒体文件的多对多关联。
--
DROP TABLE IF EXISTS `blog_post_media`;

CREATE TABLE `blog_post_media` (
    `id`                BIGINT          NOT NULL    AUTO_INCREMENT   COMMENT '主键ID',

    `post_id`           BIGINT          NOT NULL                     COMMENT '文章ID',

    `media_id`          BIGINT          NOT NULL                     COMMENT '媒体ID',

    `display_order`     INT             DEFAULT 0                     COMMENT '显示顺序',

    `create_time`       DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',

    PRIMARY KEY (`id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_media_id` (`media_id`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='文章媒体关联表：实现文章与媒体文件的多对多关联';


-- ============================================================================
-- 数据初始化：插入管理员账号
-- ============================================================================
-- 说明：插入一个默认管理员账号，用于系统管理。
-- 密码经过 BCrypt 加密，这里使用明文 "admin123" 的加密结果。
-- 实际项目部署时，请务必修改默认密码！
--
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `role`)
VALUES
    ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@campus.edu', 'admin');


-- ============================================================================
-- 数据初始化：插入示例标签
-- ============================================================================
INSERT INTO `blog_tag` (`name`) VALUES
    ('Java'),
    ('Spring Boot'),
    ('Python'),
    ('前端'),
    ('数据库'),
    ('校园生活'),
    ('技术分享'),
    ('资源下载'),
    ('面试题库'),
    ('经验分享');


-- ============================================================================
-- 脚本执行完毕
-- ============================================================================
-- 运行提示：
--   1. 请确保 MySQL 版本 >= 8.0，以支持 utf8mb4 字符集和 JSON 类型。
--   2. 如果数据库已存在，请先执行 DROP DATABASE campus_blog; 删除后重建。
--   3. 表创建完成后，可使用 SHOW TABLES; 查看所有表（共18张）。
--   4. 可使用 DESC 表名; 查看表结构。
-- ============================================================================
