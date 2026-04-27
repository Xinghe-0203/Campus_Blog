# 校园博客论坛系统 - 部署文档
# Campus Blog Forum System - Deployment Guide

---

## 目录

- [项目简介](#项目简介)
- [环境要求](#环境要求)
- [数据库初始化](#数据库初始化)
- [环境变量配置](#环境变量配置)
- [本地开发启动](#本地开发启动)
- [生产环境部署](#生产环境部署)
- [Docker 部署](#docker-部署)
- [Nginx 反向代理配置](#nginx-反向代理配置)
- [HTTPS 配置](#https-配置)
- [常见问题解答](#常见问题解答)
- [备份和恢复](#备份和恢复)

---

## 项目简介

校园博客论坛系统是一套前后端分离的全栈应用，后端基于 Spring Boot 3.0.12 构建。

### 技术栈

| 组件 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.0.12 |
| MyBatis Plus | 3.5.5 |
| MySQL | 8.0+ |
| Maven | 3.8+ |

### 主要功能

- 用户认证与授权（JWT）
- 文章发布与管理（含审核流程）
- 评论与回复系统
- 点赞、收藏、关注功能
- 校友圈动态（类似微博）
- 通知系统
- 媒体文件上传
- 内容举报与审核
- 私信功能
- 密码找回（邮件验证码）

---

## 环境要求

### 必需软件

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21 LTS | Oracle OpenJDK 或 Eclipse Temurin |
| Maven | 3.8+ | Java 项目构建工具 |
| MySQL | 8.0+ | 数据库服务器 |
| Docker | 20.10+ | 容器化部署（可选） |
| Docker Compose | 2.0+ | Docker 编排工具（可选） |

### 硬件要求

- CPU: 2 核+
- 内存: 4GB+
- 磁盘: 10GB+

---

## 数据库初始化

### 方式一：手动初始化

1. 登录 MySQL 服务器：
```bash
mysql -u root -p
```

2. 创建数据库和用户：
```sql
CREATE DATABASE IF NOT EXISTS `campus_blog`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER 'campus_blog'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON campus_blog.* TO 'campus_blog'@'%';
FLUSH PRIVILEGES;
```

3. 执行数据库初始化脚本：
```bash
mysql -u campus_blog -p campus_blog < 数据库表.sql
```

### 方式二：使用 Docker Compose 自动初始化

使用项目提供的 `docker-compose.yml`，数据库容器启动时会自动执行初始化脚本。

---

## 环境变量配置

项目使用环境变量管理所有敏感配置，参考 `.env.example` 文件。

### 必需的环境变量

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `DB_HOST` | 数据库主机地址 | `localhost` 或 `mysql` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_NAME` | 数据库名称 | `campus_blog` |
| `DB_USERNAME` | 数据库用户名 | `campus_blog` |
| `DB_PASSWORD` | 数据库密码 | `your_secure_password` |
| `JWT_SECRET` | JWT 密钥（至少 32 位） | `your_jwt_secret_key_minimum_32_characters` |
| `JWT_EXPIRATION` | Token 过期时间(ms) | `86400000` (24小时) |
| `JWT_REFRESH_EXPIRATION` | 刷新Token过期时间(ms) | `604800000` (7天) |

### 可选的环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SERVER_PORT` | 应用端口 | `8825` |
| `MYBATIS_SQL_LOG` | SQL 日志配置 | `StdOutImpl` |
| `CORS_ALLOWED_ORIGINS` | 允许的跨域来源 | `http://localhost:8080` |
| `EMAIL_HOST` | 邮件服务器地址 | `smtp.example.com` |
| `EMAIL_PORT` | 邮件服务器端口 | `587` |
| `EMAIL_USERNAME` | 邮件用户名 | - |
| `EMAIL_PASSWORD` | 邮件密码 | - |
| `EMAIL_FROM` | 发件人地址 | `noreply@campusblog.com` |
| `VERIFY_CODE_EXPIRATION` | 验证码过期时间(秒) | `300` |

### 配置步骤

1. 复制环境变量模板：
```bash
# Linux/Mac
cp .env.example .env

# Windows PowerShell
copy .env.example .env
```

2. 修改 `.env` 文件，填入实际配置值。

3. 确保 `.env` 文件不会被提交到 Git（已在 `.gitignore` 中）。

---

## 本地开发启动

### 前置条件

1. 确保 MySQL 服务已启动
2. 配置好 `.env` 文件
3. 执行数据库初始化脚本

### 启动命令

```bash
# 进入项目目录
cd your-project-directory

# 使用 Maven 启动（开发模式）
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/edu_project-0.0.1-SNAPSHOT.jar
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 应用接口 | http://localhost:8825/api |
| API 文档 | http://localhost:8825/api/doc.html |
| Swagger UI | http://localhost:8825/api/swagger-ui.html |

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

---

## 生产环境部署

### 服务器准备

1. 安装 JDK 21：
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# CentOS/RHEL
sudo yum install java-21-openjdk
```

2. 安装 MySQL 8.0：
```bash
# Ubuntu
sudo apt install mysql-server

# 启动 MySQL
sudo systemctl start mysql
sudo systemctl enable mysql
```

3. 安装 Nginx：
```bash
sudo apt install nginx
```

### 应用部署

1. 上传项目包到服务器：
```bash
scp target/edu_project-0.0.1-SNAPSHOT.jar user@server:/opt/campus-blog/
```

2. 创建 systemd 服务文件 `/etc/systemd/system/campus-blog.service`：
```ini
[Unit]
Description=Campus Blog Forum System
After=network.target mysql.service

[Service]
Type=simple
User=campus-blog
WorkingDirectory=/opt/campus-blog
EnvironmentFile=/opt/campus-blog/.env
ExecStart=/usr/bin/java -jar edu_project-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

3. 配置环境变量文件 `/opt/campus-blog/.env`

4. 启动服务：
```bash
sudo systemctl daemon-reload
sudo systemctl start campus-blog
sudo systemctl enable campus-blog
```

5. 查看服务状态：
```bash
sudo systemctl status campus-blog
```

---

## Docker 部署

### 快速启动

```bash
# 进入项目目录
cd your-project-directory

# 启动所有服务（MySQL + 应用）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f app
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 应用接口 | http://localhost:8825/api |
| API 文档 | http://localhost:8825/api/doc.html |
| phpMyAdmin | http://localhost:8080 (可选) |

### 停止服务

```bash
docker-compose down

# 删除数据卷（慎用，会清除数据库数据）
docker-compose down -v
```

### 查看日志

```bash
# 应用日志
docker-compose logs -f app

# MySQL 日志
docker-compose logs -f mysql

# 所有服务日志
docker-compose logs -f
```

---

## Nginx 反向代理配置

### 基础配置

创建 `/etc/nginx/sites-available/campus-blog`：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 强制跳转 HTTPS（可选）
    # return 301 https://$server_name$request_uri;

    # 日志配置
    access_log /var/log/nginx/campus-blog.access.log;
    error_log /var/log/nginx/campus-blog.error.log;

    # 上传文件大小限制
    client_max_body_size 500m;

    # 超时配置
    proxy_connect_timeout 60s;
    proxy_send_timeout 60s;
    proxy_read_timeout 60s;

    location /api {
        proxy_pass http://127.0.0.1:8825/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 支持（如有需要）
        # proxy_http_version 1.1;
        # proxy_set_header Upgrade $http_upgrade;
        # proxy_set_header Connection "upgrade";
    }

    # 静态资源（前端部署时使用）
    location / {
        root /var/www/campus-blog/static;
        try_files $uri $uri/ /index.html;
    }
}
```

启用配置：
```bash
sudo ln -s /etc/nginx/sites-available/campus-blog /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## HTTPS 配置

### 使用 Let's Encrypt 免费证书

1. 安装 Certbot：
```bash
# Ubuntu
sudo apt install certbot python3-certbot-nginx

# 其他系统参考 https://certbot.eff.org/
```

2. 获取证书：
```bash
sudo certbot --nginx -d your-domain.com
```

3. 自动续期验证：
```bash
sudo certbot renew --dry-run
```

### 完整 HTTPS 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_trusted_certificate /etc/letsencrypt/live/your-domain.com/chain.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 1d;

    location /api {
        proxy_pass http://127.0.0.1:8825/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 常见问题解答

### Q1: 数据库连接失败

**问题**: 应用启动时报 `Connection refused` 或 `Access denied`

**排查步骤**:
1. 检查 MySQL 服务是否运行：`sudo systemctl status mysql`
2. 检查端口是否通：`telnet localhost 3306`
3. 检查用户名密码是否正确
4. 检查用户权限：`SHOW GRANTS FOR 'campus_blog'@'%';`

### Q2: 端口被占用

**问题**: `Port 8825 is already in use`

**解决方案**:
```bash
# 查找占用端口的进程
netstat -ano | findstr :8825

# 结束进程或修改 .env 中的 SERVER_PORT
```

### Q3: JWT 验证失败

**问题**: 登录后请求 API 返回 401 Unauthorized

**排查步骤**:
1. 检查 `JWT_SECRET` 环境变量是否配置正确
2. 检查客户端请求头是否正确传递 Token：`Authorization: Bearer <token>`
3. 检查 Token 是否过期

### Q4: Docker 构建失败

**问题**: `docker-compose build` 失败

**解决方案**:
```bash
# 清理 Docker 缓存
docker-compose down --rmi all
docker system prune -f

# 重新构建
docker-compose build --no-cache
```

### Q5: 数据库数据丢失

**问题**: 重启容器后数据丢失

**原因**: 使用了默认的匿名卷

**解决方案**: 使用 docker-compose.yml 中定义的命名卷，或使用主机目录挂载：
```yaml
volumes:
  - ./data/mysql:/var/lib/mysql
```

### Q6: 前端无法访问 API

**问题**: CORS 跨域错误

**解决方案**:
1. 检查 `CORS_ALLOWED_ORIGINS` 环境变量是否包含前端域名
2. 检查 Nginx 是否正确配置了代理

---

## 备份和恢复

### 数据库备份

```bash
# 使用 mysqldump 备份
mysqldump -u campus_blog -p campus_blog > backup_$(date +%Y%m%d).sql

# Docker 环境备份
docker exec mysql-container mysqldump -u campus_blog -p campus_blog > backup.sql
```

### 数据库恢复

```bash
# 恢复数据库
mysql -u campus_blog -p campus_blog < backup_20260426.sql

# Docker 环境恢复
cat backup.sql | docker exec -i mysql-container mysql -u campus_blog -p campus_blog
```

### 自动化备份脚本

创建 `/opt/campus-blog/backup.sh`：
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR=/opt/campus-blog/backups
CONTAINER_NAME=campus-blog-mysql

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
docker exec $CONTAINER_NAME mysqldump -u campus_blog -p$DB_PASSWORD campus_blog > $BACKUP_DIR/backup_$DATE.sql

# 保留最近 30 天的备份
find $BACKUP_DIR -name "backup_*.sql" -mtime +30 -delete

echo "Backup completed: backup_$DATE.sql"
```

添加定时任务：
```bash
# 每天凌晨 3 点执行备份
0 3 * * * /opt/campus-blog/backup.sh >> /var/log/backup.log 2>&1
```

---

## 联系方式

- 项目 GitHub: https://github.com/Xinghe-0203/Campus_Blog
- 开发者: 刘畅

---

*文档版本: v1.34 | 最后更新: 2026-04-27*