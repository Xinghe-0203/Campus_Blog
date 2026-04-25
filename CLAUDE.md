# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## 默认行为配置

**推理强度：默认开启最高强度推理模式**
- 调用多个子代理并行深度检查项目
- 全面分析架构、安全、业务逻辑等各个维度
- 不遗漏任何潜在问题

---

## Project Overview

This is a **campus blog forum system** - a full-stack web application built with Spring Boot 3.0.12 (backend) and planned HTML5/CSS3/JavaScript (frontend). It's being developed for a school skills competition by 刘畅.

**Key Links**:
- GitHub: https://github.com/Xinghe-0203/Campus_Blog
- Project Documentation: `campus_blog.md` (complete development plan)
- Backend README: `edu_project/README.md`

---

## Environment Setup

Environment variables (optional for local dev, required for production):
- `DB_PASSWORD` - Database password (default: `chaojiwudibangbangtang`)
- `JWT_SECRET` - JWT signing key (default has a local dev value)

```bash
# Linux/Mac
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key

# Windows PowerShell
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your_secret_key"
```

---

## Common Commands

```bash
cd edu_project

# Build
mvn clean package

# Run
mvn spring-boot:run
java -jar target/edu_project-0.0.1-SNAPSHOT.jar

# Test
mvn test
```

**Access:**
- Application: http://localhost/api
- API Docs: http://localhost/api/doc.html (Knife4j UI)
- Default admin: `admin` / `admin123`

---

## Architecture

### Tech Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 3.0.12 |
| MyBatis Plus | 3.5.5 |
| Spring Security | 3.0.12 |
| JWT (JJWT) | 0.12.3 |
| Knife4j | 4.5.0 |
| MySQL | 8.x |

### Layered Architecture

```
Controller → Service → Mapper → Entity
```

- All controllers return `Result<T>` (unified API response)
- All mappers extend `BaseMapper<T>`
- Soft delete via `isDeleted` field with `@TableLogic`

### Database Tables (7)

| Table | Purpose | Notes |
|-------|---------|-------|
| `sys_user` | Users | BCrypt password, role-based access, login fail lock |
| `blog_post` | Articles | view/like/comment counts |
| `blog_comment` | Comments | nested replies via `parentId`, cascade delete |
| `blog_tag` | Tags | unique name |
| `blog_post_tag` | Post-Tag relation | composite PK (postId, tagId) |
| `blog_like` | Likes | composite PK (userId, postId) |
| `blog_collect` | Collections | composite PK (userId, postId) |

---

## Important Conventions

### API Response
```java
Result.success(data)   // 200 OK
Result.error(message)  // error response
throw new BusinessException(code, message)  // business errors
```

### Security
- Password stored with BCrypt (strength 12), never returned in API responses
- Use `UserVO` for user info responses (not `SysUser` directly)
- JWT authentication via `JwtAuthenticationFilter`
- `SecurityUtils.getCurrentUserIdOrNull()` gets current user from token
- Login fail lock: 5 failures → 15min lock (atomic update, concurrent-safe)
- JWT supports revocation via blacklist and refresh token
- JWT refresh token rotation: old refresh token revoked after use

### Parameter Validation
- Use `@Valid` on request body parameters
- Use `@NotBlank`, `@Size`, `@Min`, `@Max` for validation
- Add `@Valid` to GET request parameters for pagination queries

### Service Layer Rules
- Controllers must use Service layer, never directly call Mapper
- Add `@Transactional(rollbackFor = Exception.class)` for write operations
- Add `@Transactional(readOnly = true)` for read-only operations

### Code Changes
- After code changes, update `README.md` and `campus_blog.md`
- Read related files before modifying any module
- When uncertain, ask the user before proceeding

---

## Key Files

| File | Purpose |
|------|---------|
| `application.yml` | Config: DB, JWT, server settings |
| `Result.java` | Unified API response wrapper |
| `BusinessException.java` | Custom business exception |
| `GlobalExceptionHandler.java` | Global exception handling |
| `SecurityConfig.java` | Spring Security + JWT config |
| `JwtUtils.java` | JWT token generation/validation |
| `JwtAuthenticationFilter.java` | JWT request filter |
| `JwtSchedulerConfig.java` | JWT blacklist cleanup scheduler |
| `MyMetaObjectHandler.java` | Auto-fill createTime/updateTime |
| `HtmlSanitizer.java` | XSS 防护，HTML 内容过滤 |

---

## Development Status

✅ **Completed**: User auth, article management, Spring Security + JWT, all entities/mappers/services, security hardening, entity validation, like/collect/comment functionality, login lock, JWT blacklist/refresh
⏳ **Pending**: Frontend pages, frontend-backend integration, XSS filter

---

## Important Reminders

### 文档更新规范
- **每次更新完代码都要更新md文件** - Update README.md and campus_blog.md after any code changes

---

## 代码修改规范

**【强制】每次修改代码前，必须先阅读相关文档和现有代码：**

1. **阅读项目文档** - 修改前先阅读 `campus_blog.md`、`README.md`、`CLAUDE.md`，了解项目架构和已有设计
2. **阅读相关代码** - 修改某个模块前，先完整阅读该模块的所有相关文件（Controller、Service、Mapper、Entity）
3. **了解关联关系** - 不要臆想或猜测代码之间的关联，务必通过阅读源码确认
4. **避免重复造轮子** - 确认现有功能后再决定是复用还是新增

违反此规范可能导致：
- 破坏已有的正确实现
- 与现有架构设计冲突
- 重复造轮子，浪费工作量

---

## 开发荣耻

以瞎清接口为耻，以认真查询为荣。
以模糊执行为耻，以寻求确认为荣。
以想业务为耻，以人类确认为荣。
以创造接口为耻，以复用现有为荣。
以跳过验证为耻，以主动测试为荣。
以破坏架构为耻，以遵循规范为荣。
以假装理解为耻，以诚实无知为荣。
以盲目修改为耻，以谨慎重构为荣。
以忘记更新文档为耻，以及时更新文档为荣。
