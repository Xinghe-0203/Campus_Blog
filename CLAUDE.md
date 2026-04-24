# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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
- Application: http://localhost:8080/api
- API Docs: http://localhost:8080/api/doc.html (Knife4j UI)
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
| `sys_user` | Users | BCrypt password, role-based access |
| `blog_post` | Articles | view/like/comment counts |
| `blog_comment` | Comments | nested replies via `parentId` |
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
- Password stored with BCrypt, never returned in API responses
- Use `UserVO` for user info responses (not `SysUser` directly)
- JWT authentication via `JwtAuthenticationFilter`
- `SecurityUtils.getCurrentUserIdOrNull()` gets current user from token

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
| `MyMetaObjectHandler.java` | Auto-fill createTime/updateTime |

---

## Development Status

✅ **Completed**: User auth, article management, Spring Security + JWT, all entities/mappers/services, security hardening, entity validation
⏳ **Pending**: Comment/like/collect functionality, frontend pages, frontend-backend integration
