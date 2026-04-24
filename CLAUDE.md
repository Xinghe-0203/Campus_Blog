
# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Project Overview

This is a **campus blog forum system** - a full-stack web application built with Spring Boot 3.x (backend) and planned HTML5/CSS3/JavaScript (frontend). It's being developed for a school skills competition.

**Key Links**:
- GitHub: https://github.com/Xinghe-0203/Campus_Blog
- Project Documentation: `campus_blog.md` (complete development plan)
- Backend README: `edu_project/README.md`

---

## Common Commands

### Build & Run

```bash
# Navigate to backend directory first
cd edu_project

# Build project
mvn clean package

# Run application
mvn spring-boot:run
# Or run JAR directly
java -jar target/edu_project-0.0.1-SNAPSHOT.jar

# Run tests
mvn test
```

### Access

- Application: http://localhost:8080/api
- API Documentation: http://localhost:8080/api/doc.html (Knife4j UI)
- Default admin: `admin` / `admin123`

---

## Architecture & Structure

### Tech Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 3.0.12 |
| Spring Security | (included, pending config) |
| MyBatis Plus | 3.5.15 |
| JWT | 0.12.3 |
| Knife4j | 4.5.0 |
| MySQL | 8.x |

### Layered Architecture

```
Controller (Presentation) → Service (Business) → Mapper (Persistence) → Entity (Data Model)
```

All controllers return `Result<T>` - the unified API response wrapper. All mappers extend `BaseMapper<T>` for automatic CRUD operations.

### Database Tables (7 total)

1. `sys_user` - Users
2. `blog_post` - Articles/Posts
3. `blog_comment` - Comments (supports nested replies via `parentId`)
4. `blog_tag` - Tags
5. `blog_post_tag` - Post-Tag many-to-many junction
6. `blog_like` - Like records (composite PK: userId+postId)
7. `blog_collect` - Collection records (composite PK: userId+postId)

### Key Files

| Path | Purpose |
|------|---------|
| `edu_project/pom.xml` | Maven dependencies |
| `edu_project/src/main/resources/application.yml` | App config (DB connection, etc.) |
| `edu_project/src/main/java/com/example/edu_project/common/result/Result.java` | Unified API response |
| `edu_project/src/main/java/com/example/edu_project/config/MyMetaObjectHandler.java` | Auto-fill `createTime`/`updateTime` |
| `campus_blog.md` | Complete project plan &amp; API design |
| `数据库表` | Database initialization SQL |

---

## Development Status

✅ **Completed**: Database design, backend skeleton, all entities/mappers/services, exception handling, auto-fill config  
⏳ **Pending**: Spring Security + JWT implementation, all Controllers, frontend pages, integration

---

## Important Conventions

- All endpoints prefixed with `/api` (configured via `server.servlet.context-path`)
- Soft delete enabled globally via `isDeleted` field
- MyBatis Plus auto-fills `createTime` and `updateTime`
- Use `Result.success(data)` and `Result.error(msg)` for responses
- Business logic exceptions should throw `BusinessException`
- **每次更新完代码都要更新md文件** - Update README.md and campus_blog.md after any code changes

---

## 代码修改规范

**【强制】每次修改代码前，必须先阅读相关文档和现有代码：**

1. **阅读项目文档** - 修改前先阅读 `campus_blog.md`、`README.md`、`CLAUDE.md`，了解项目架构和已有设计
2. **阅读相关代码** - 修改某个模块前，先完整阅读该模块的所有相关文件（Controller、Service、Mapper、Entity）
3. **了解关联关系** - 不要臆想或猜测代码之间的关联，务必通过阅读源码确认
4. **避免重复造轮子** - 确认现有功能后再决定是复用还是新增
5. **不确定的先问我** - 如果遇到不清楚的地方或不确定如何处理，先向用户确认再执行

违反此规范可能导致：
- 破坏已有的正确实现
- 与现有架构设计冲突
- 重复造轮子，浪费工作量

---

## 开发八荣八耻

以瞎清接口为耻，以认真查询为荣。
以模糊执行为耻，以寻求确认为荣。
以想业务为耻，以人类确认为荣。
以创造接口为耻，以复用现有为荣。
以跳过验证为耻，以主动测试为荣。
以破坏架构为耻，以遵循规范为荣。
以假装理解为耻，以诚实无知为荣。
以盲目修改为耻，以谨慎重构为荣。
以忘记更文档为耻，以及时更新为荣。

