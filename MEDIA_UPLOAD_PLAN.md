# 媒体上传功能后端实现计划书

> 校园博客论坛系统 - 媒体管理模块
> 版本：v1.0
> 日期：2026-04-25
> 状态：✅ 可实施

---

## 一、需求概述

| 功能模块 | 描述 | 优先级 |
|---------|------|--------|
| 图片上传 | 文章图片、评论图片、头像上传 | P0 |
| 视频上传 | 文章内嵌视频（MP4/WebM） | P1 |
| 封面图 | 文章封面图片 | P0 |
| 媒体管理 | 用户已上传媒体文件列表/删除 | P1 |
| 图片压缩 | 上传时自动压缩大图 | P2 |

---

## 二、数据库设计

### 2.1 新增表 `blog_media`

```sql
-- 媒体资源表
CREATE TABLE `blog_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_url` VARCHAR(500) NOT NULL COMMENT '访问URL',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型：image/video',
  `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `width` INT DEFAULT NULL COMMENT '图片宽度（仅图片）',
  `height` INT DEFAULT NULL COMMENT '图片高度（仅图片）',
  `duration` INT DEFAULT NULL COMMENT '视频时长秒数（仅视频）',
  `thumb_url` VARCHAR(500) DEFAULT NULL COMMENT '视频缩略图URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=正常，0=禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_file_type` (`file_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体资源表';
```

### 2.2 新增表 `blog_post_media`

```sql
-- 文章媒体关联表（用于文章内嵌的多媒体）
CREATE TABLE `blog_post_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  `media_id` BIGINT NOT NULL COMMENT '媒体ID',
  `display_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_media_id` (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章媒体关联表';
```

### 2.3 修改表 `blog_post`

```sql
-- 为文章表添加封面图字段
ALTER TABLE `blog_post`
ADD COLUMN `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL' AFTER `summary`;
```

### 2.4 修改表 `sys_user`

```sql
-- 为用户表添加头像URL字段
ALTER TABLE `sys_user`
ADD COLUMN `avatar` VARCHAR(500) DEFAULT NULL COMMENT '用户头像URL' AFTER `nickname`;
```

---

## 三、项目结构

```
src/main/java/com/example/edu_project/
├── controller/
│   └── MediaController.java              # 媒体上传下载控制器
├── service/
│   ├── MediaService.java                 # 媒体服务接口
│   └── impl/
│       └── MediaServiceImpl.java         # 媒体服务实现
├── mapper/
│   ├── BlogMediaMapper.java              # 媒体 Mapper
│   └── BlogPostMediaMapper.java          # 文章媒体关联 Mapper
├── entity/
│   ├── BlogMedia.java                    # 媒体实体
│   └── BlogPostMedia.java                # 文章媒体关联实体
├── dto/
│   └── MediaQueryRequest.java            # 媒体查询请求
├── vo/
│   └── MediaVO.java                     # 媒体视图对象
└── config/
    └── WebMvcConfig.java                 # 静态资源映射配置
```

---

## 四、代码实现

### 4.1 pom.xml - 添加依赖

```xml
<!-- 添加到 <dependencies> 中 -->

<!-- 图片处理：缩略图生成、Metadata提取 -->
<dependency>
    <groupId>com.drewnoakes</groupId>
    <artifactId>metadata-extractor</artifactId>
    <version>2.18.0</version>
</dependency>

<!-- 缩略图：图片压缩和调整大小 -->
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.20</version>
</dependency>
```

---

### 4.2 实体类 - BlogMedia.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 媒体资源实体类
 */
@Data
@TableName("blog_media")
public class BlogMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 存储路径（相对于上传目录）
     */
    private String filePath;

    /**
     * 访问URL
     */
    private String fileUrl;

    /**
     * 文件类型：image/video
     */
    private String fileType;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 图片宽度（仅图片）
     */
    private Integer width;

    /**
     * 图片高度（仅图片）
     */
    private Integer height;

    /**
     * 视频时长秒数（仅视频）
     */
    private Integer duration;

    /**
     * 视频缩略图URL
     */
    private String thumbUrl;

    /**
     * 状态：1=正常，0=禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}
```

---

### 4.3 实体类 - BlogPostMedia.java

```java
package com.example.edu_project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章媒体关联实体
 */
@Data
@TableName("blog_post_media")
public class BlogPostMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 媒体ID
     */
    private Long mediaId;

    /**
     * 显示顺序
     */
    private Integer displayOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

---

### 4.4 DTO - MediaQueryRequest.java

```java
package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 媒体查询请求 DTO
 */
@Data
@Schema(description = "媒体查询请求")
public class MediaQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "文件类型：image/video，不传则查询全部")
    private String fileType;
}
```

---

### 4.5 VO - MediaVO.java

```java
package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 媒体视图对象
 */
@Data
@Schema(description = "媒体信息")
public class MediaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "媒体ID")
    private Long id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "访问URL")
    private String fileUrl;

    @Schema(description = "文件类型：image/video")
    private String fileType;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "图片宽度")
    private Integer width;

    @Schema(description = "图片高度")
    private Integer height;

    @Schema(description = "视频缩略图URL")
    private String thumbUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
```

---

### 4.6 Mapper - BlogMediaMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.edu_project.entity.BlogMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 媒体 Mapper
 */
@Mapper
public interface BlogMediaMapper extends BaseMapper<BlogMedia> {

    /**
     * 分页查询用户媒体列表
     */
    IPage<BlogMedia> selectByUserId(Page<BlogMedia> page,
                                     @Param("userId") Long userId,
                                     @Param("fileType") String fileType);
}
```

---

### 4.7 Mapper XML - BlogMediaMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.edu_project.mapper.BlogMediaMapper">

    <select id="selectByUserId" resultType="com.example.edu_project.entity.BlogMedia">
        SELECT * FROM blog_media
        WHERE user_id = #{userId}
        AND is_deleted = 0
        <if test="fileType != null and fileType != ''">
            AND file_type = #{fileType}
        </if>
        ORDER BY create_time DESC
    </select>

</mapper>
```

---

### 4.8 Mapper - BlogPostMediaMapper.java

```java
package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogPostMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章媒体关联 Mapper
 */
@Mapper
public interface BlogPostMediaMapper extends BaseMapper<BlogPostMedia> {

    /**
     * 批量插入文章媒体关联
     */
    int batchInsert(@Param("list") List<BlogPostMedia> list);

    /**
     * 查询文章的所有媒体
     */
    List<BlogPostMedia> selectByPostId(@Param("postId") Long postId);

    /**
     * 删除文章的所有媒体关联（不删除媒体本身）
     */
    int deleteByPostId(@Param("postId") Long postId);
}
```

---

### 4.9 Mapper XML - BlogPostMediaMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.edu_project.mapper.BlogPostMediaMapper">

    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO blog_post_media (post_id, media_id, display_order, create_time)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.postId}, #{item.mediaId}, #{item.displayOrder}, NOW())
        </foreach>
    </insert>

    <select id="selectByPostId" resultType="com.example.edu_project.entity.BlogPostMedia">
        SELECT * FROM blog_post_media
        WHERE post_id = #{postId}
        ORDER BY display_order ASC
    </select>

    <delete id="deleteByPostId">
        DELETE FROM blog_post_media WHERE post_id = #{postId}
    </delete>

</mapper>
```

---

### 4.10 Service 接口 - MediaService.java

```java
package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.MediaQueryRequest;
import com.example.edu_project.entity.BlogMedia;
import com.example.edu_project.vo.MediaVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 媒体服务接口
 */
public interface MediaService extends IService<BlogMedia> {

    /**
     * 上传文件
     * @param file 文件
     * @param userId 上传用户ID
     * @return 媒体信息
     */
    MediaVO uploadFile(MultipartFile file, Long userId) throws IOException;

    /**
     * 批量上传文件
     * @param files 文件列表
     * @param userId 上传用户ID
     * @return 媒体信息列表
     */
    List<MediaVO> uploadFiles(MultipartFile[] files, Long userId) throws IOException;

    /**
     * 获取媒体详情
     * @param mediaId 媒体ID
     * @return 媒体信息
     */
    MediaVO getMediaById(Long mediaId);

    /**
     * 分页查询用户媒体
     * @param request 查询请求
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<MediaVO> getUserMedia(MediaQueryRequest request, Long userId);

    /**
     * 删除媒体（物理删除文件，逻辑删除记录）
     * @param mediaId 媒体ID
     * @param userId 操作人ID
     */
    void deleteMedia(Long mediaId, Long userId);

    /**
     * 绑定文章和媒体
     * @param postId 文章ID
     * @param mediaIds 媒体ID列表
     */
    void bindPostMedia(Long postId, List<Long> mediaIds);

    /**
     * 获取文章的所有媒体
     * @param postId 文章ID
     * @return 媒体列表
     */
    List<MediaVO> getPostMedia(Long postId);

    /**
     * 生成视频缩略图
     * @param mediaId 媒体ID
     * @param userId 操作人ID
     */
    void generateVideoThumb(Long mediaId, Long userId);
}
```

---

### 4.11 Service 实现 - MediaServiceImpl.java

```java
package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.MediaQueryRequest;
import com.example.edu_project.entity.BlogMedia;
import com.example.edu_project.entity.BlogPostMedia;
import com.example.edu_project.mapper.BlogMediaMapper;
import com.example.edu_project.mapper.BlogPostMediaMapper;
import com.example.edu_project.service.MediaService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.MediaVO;
import com.drewnoakes.metadataextractor.Metadata;
import com.drewnoakes.metadataextractor.MetadataReader;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 媒体服务实现类
 */
@Slf4j
@Service
public class MediaServiceImpl extends ServiceImpl<BlogMediaMapper, BlogMedia> implements MediaService {

    @Autowired
    private BlogPostMediaMapper blogPostMediaMapper;

    /**
     * 上传根目录
     */
    @Value("${upload.base-path:uploads}")
    private String uploadBasePath;

    /**
     * 图片最大宽度（超过则压缩）
     */
    @Value("${upload.image.max-width:1920}")
    private int maxImageWidth;

    /**
     * 图片质量
     */
    @Value("${upload.image.quality:0.85}")
    private double imageQuality;

    /**
     * 允许的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    /**
     * 允许的视频类型
     */
    private static final List<String> ALLOWED_VIDEO_TYPES = List.of(
            "video/mp4", "video/webm"
    );

    /**
     * 图片大小限制（10MB）
     */
    private static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024;

    /**
     * 视频大小限制（500MB）
     */
    private static final long VIDEO_MAX_SIZE = 500 * 1024 * 1024;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaVO uploadFile(MultipartFile file, Long userId) throws IOException {
        // 校验文件
        validateFile(file);

        // 确定文件类型
        String mimeType = file.getContentType();
        String fileType = mimeType.startsWith("image/") ? "image" : "video";

        // 生成存储路径
        String relativePath = generateFilePath(file.getOriginalFilename(), fileType);
        String absolutePath = uploadBasePath + "/" + relativePath;

        // 确保目录存在
        Path dir = Paths.get(absolutePath).getParent();
        Files.createDirectories(dir);

        // 处理图片：压缩/调整大小
        if ("image".equals(fileType)) {
            processImage(file, absolutePath);
        } else {
            // 直接保存视频
            file.transferTo(Paths.get(absolutePath));
        }

        // 提取 Metadata
        Integer width = null;
        Integer height = null;
        try {
            BufferedImage image = ImageIO.read(new File(absolutePath));
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            }
        } catch (Exception e) {
            log.warn("读取图片尺寸失败: {}", e.getMessage());
        }

        // 构建访问URL
        String fileUrl = "/uploads/" + relativePath.replace("\\", "/");

        // 保存到数据库
        BlogMedia media = new BlogMedia();
        media.setUserId(userId);
        media.setFileName(file.getOriginalFilename());
        media.setFilePath(relativePath);
        media.setFileUrl(fileUrl);
        media.setFileType(fileType);
        media.setMimeType(mimeType);
        media.setFileSize(file.getSize());
        media.setWidth(width);
        media.setHeight(height);
        media.setStatus(1);
        this.save(media);

        return convertToVO(media);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MediaVO> uploadFiles(MultipartFile[] files, Long userId) throws IOException {
        List<MediaVO> results = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    results.add(uploadFile(file, userId));
                } catch (Exception e) {
                    log.error("上传文件失败: {}", file.getOriginalFilename(), e);
                }
            }
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public MediaVO getMediaById(Long mediaId) {
        BlogMedia media = this.getById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体不存在");
        }
        return convertToVO(media);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<MediaVO> getUserMedia(MediaQueryRequest request, Long userId) {
        Page<BlogMedia> page = new Page<>(request.getPage(), request.getPageSize());
        IPage<BlogMedia> mediaPage = baseMapper.selectByUserId(page, userId, request.getFileType());

        IPage<MediaVO> result = new Page<>(mediaPage.getCurrent(), mediaPage.getSize(), mediaPage.getTotal());
        result.setRecords(mediaPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMedia(Long mediaId, Long userId) {
        BlogMedia media = this.getById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体不存在");
        }
        // 校验权限：只有上传者可以删除
        if (!media.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此媒体");
        }

        // 删除物理文件
        String absolutePath = uploadBasePath + "/" + media.getFilePath();
        try {
            Files.deleteIfExists(Paths.get(absolutePath));
            // 删除缩略图（如果有）
            if (media.getThumbUrl() != null) {
                String thumbPath = uploadBasePath + "/" + media.getThumbUrl().replace("/uploads/", "");
                Files.deleteIfExists(Paths.get(thumbPath));
            }
        } catch (IOException e) {
            log.error("删除文件失败: {}", absolutePath, e);
        }

        // 逻辑删除
        this.removeById(mediaId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPostMedia(Long postId, List<Long> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return;
        }

        // 先删除旧关联
        blogPostMediaMapper.deleteByPostId(postId);

        // 批量插入新关联
        List<BlogPostMedia> list = new ArrayList<>();
        for (int i = 0; i < mediaIds.size(); i++) {
            BlogPostMedia pm = new BlogPostMedia();
            pm.setPostId(postId);
            pm.setMediaId(mediaIds.get(i));
            pm.setDisplayOrder(i);
            list.add(pm);
        }
        blogPostMediaMapper.batchInsert(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaVO> getPostMedia(Long postId) {
        List<BlogPostMedia> pms = blogPostMediaMapper.selectByPostId(postId);
        if (pms.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> mediaIds = pms.stream()
                .map(BlogPostMedia::getMediaId)
                .collect(Collectors.toList());

        return this.listByIds(mediaIds).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateVideoThumb(Long mediaId, Long userId) {
        BlogMedia media = this.getById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体不存在");
        }
        if (!"video".equals(media.getFileType())) {
            throw new BusinessException(400, "只有视频才能生成缩略图");
        }
        if (!media.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权操作此媒体");
        }

        // TODO: 视频缩略图生成（需要 FFmpeg）
        // 目前简单实现：创建默认缩略图或标记待处理
        log.info("视频缩略图生成待实现: mediaId={}", mediaId);
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        String mimeType = file.getContentType();
        boolean isImage = mimeType != null && ALLOWED_IMAGE_TYPES.contains(mimeType);
        boolean isVideo = mimeType != null && ALLOWED_VIDEO_TYPES.contains(mimeType);

        if (!isImage && !isVideo) {
            throw new BusinessException(400, "不支持的文件类型，仅支持 JPG、PNG、GIF、WebP 图片和 MP4、WebM 视频");
        }

        if (isImage && file.getSize() > IMAGE_MAX_SIZE) {
            throw new BusinessException(400, "图片大小不能超过 10MB");
        }

        if (isVideo && file.getSize() > VIDEO_MAX_SIZE) {
            throw new BusinessException(400, "视频大小不能超过 500MB");
        }
    }

    /**
     * 生成文件存储路径
     */
    private String generateFilePath(String originalFilename, String fileType) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return fileType + "/" + dateStr + "/" + uuid + ext;
    }

    /**
     * 处理图片：压缩/调整大小
     */
    private void processImage(MultipartFile file, String outputPath) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new BusinessException(400, "无法读取图片文件");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // 如果图片过大，进行压缩
        if (width > maxImageWidth) {
            double ratio = (double) maxImageWidth / width;
            int newHeight = (int) (height * ratio);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .width(maxImageWidth)
                    .height(newHeight)
                    .outputQuality(imageQuality)
                    .outputFormat("jpg")
                    .toOutputStream(baos);

            Files.write(Paths.get(outputPath), baos.toByteArray());
        } else {
            // 小图直接保存，转换为 JPG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .outputQuality(imageQuality)
                    .outputFormat("jpg")
                    .toOutputStream(baos);
            Files.write(Paths.get(outputPath), baos.toByteArray());
        }
    }

    /**
     * 转换为 VO
     */
    private MediaVO convertToVO(BlogMedia media) {
        MediaVO vo = new MediaVO();
        vo.setId(media.getId());
        vo.setFileName(media.getFileName());
        vo.setFileUrl(media.getFileUrl());
        vo.setFileType(media.getFileType());
        vo.setMimeType(media.getMimeType());
        vo.setFileSize(media.getFileSize());
        vo.setWidth(media.getWidth());
        vo.setHeight(media.getHeight());
        vo.setThumbUrl(media.getThumbUrl());
        vo.setCreateTime(media.getCreateTime());
        return vo;
    }
}
```

---

### 4.12 Controller - MediaController.java

```java
package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.MediaQueryRequest;
import com.example.edu_project.service.MediaService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.MediaVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 媒体管理控制器
 */
@Slf4j
@Tag(name = "媒体管理", description = "文件上传、媒体管理相关接口")
@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    /**
     * 上传单个文件
     */
    @Operation(summary = "上传文件", description = "上传图片或视频文件，支持 JPG、PNG、GIF、WebP 图片和 MP4、WebM 视频")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<MediaVO> uploadFile(
            @Parameter(description = "文件")
            @RequestParam("file") MultipartFile file) {

        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        try {
            MediaVO media = mediaService.uploadFile(file, userId);
            return Result.success(media);
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new BusinessException(500, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传文件
     */
    @Operation(summary = "批量上传文件", description = "一次上传多个文件，最大支持 9 个")
    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<List<MediaVO>> uploadMultiple(
            @Parameter(description = "文件列表（最多9个）")
            @RequestParam("files") MultipartFile[] files) {

        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        if (files.length > 9) {
            throw new BusinessException(400, "一次最多上传 9 个文件");
        }

        try {
            List<MediaVO> mediaList = mediaService.uploadFiles(files, userId);
            return Result.success(mediaList);
        } catch (IOException e) {
            log.error("批量上传文件失败", e);
            throw new BusinessException(500, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取媒体详情
     */
    @Operation(summary = "获取媒体详情")
    @GetMapping("/{id}")
    public Result<MediaVO> getMedia(
            @Parameter(description = "媒体ID")
            @PathVariable Long id) {
        MediaVO media = mediaService.getMediaById(id);
        return Result.success(media);
    }

    /**
     * 分页查询用户媒体列表
     */
    @Operation(summary = "获取我的媒体列表")
    @GetMapping("/list")
    public Result<IPage<MediaVO>> getMyMedia(MediaQueryRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        IPage<MediaVO> page = mediaService.getUserMedia(request, userId);
        return Result.success(page);
    }

    /**
     * 删除媒体
     */
    @Operation(summary = "删除媒体")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMedia(
            @Parameter(description = "媒体ID")
            @PathVariable Long id) {

        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        mediaService.deleteMedia(id, userId);
        return Result.success();
    }

    /**
     * 绑定文章和媒体
     */
    @Operation(summary = "绑定文章媒体", description = "将媒体关联到文章，用于文章内嵌多媒体")
    @PostMapping("/bind/{postId}")
    public Result<Void> bindPostMedia(
            @Parameter(description = "文章ID")
            @PathVariable Long postId,
            @Parameter(description = "媒体ID列表")
            @RequestBody List<Long> mediaIds) {

        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        mediaService.bindPostMedia(postId, mediaIds);
        return Result.success();
    }

    /**
     * 获取文章的媒体列表
     */
    @Operation(summary = "获取文章媒体列表")
    @GetMapping("/post/{postId}")
    public Result<List<MediaVO>> getPostMedia(
            @Parameter(description = "文章ID")
            @PathVariable Long postId) {
        List<MediaVO> mediaList = mediaService.getPostMedia(postId);
        return Result.success(mediaList);
    }
}
```

---

### 4.13 配置类 - WebMvcConfig.java

```java
package com.example.edu_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.base-path:uploads}")
    private String uploadBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /uploads/** 到本地文件目录
        // 例如: /uploads/image/2026/04/25/xxx.jpg -> uploads/image/2026/04/25/xxx.jpg
        String resourcePath = "file:" + System.getProperty("user.dir") + "/" + uploadBasePath + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourcePath);
    }
}
```

---

### 4.14 配置文件 - application.yml 添加配置

```yaml
# 添加到 application.yml 末尾

# ==================== 文件上传配置 ====================
upload:
  # 上传文件存储根目录（相对于项目运行目录）
  base-path: uploads
  # 图片最大宽度（像素），超过则自动压缩
  image:
    max-width: 1920
    # 图片压缩质量 0.0-1.0
    quality: 0.85
```

---

## 五、API 接口文档

### 5.1 上传文件

```
POST /api/media/upload
Content-Type: multipart/form-data

参数：
- file: 文件（必填）

响应：
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "fileName": "photo.jpg",
    "fileUrl": "/uploads/image/2026/04/25/abc123.jpg",
    "fileType": "image",
    "mimeType": "image/jpeg",
    "fileSize": 102400,
    "width": 1920,
    "height": 1080,
    "createTime": "2026-04-25T10:00:00"
  }
}
```

### 5.2 批量上传

```
POST /api/media/upload/multiple
Content-Type: multipart/form-data

参数：
- files: 文件数组（必填，最多9个）

响应：
{
  "code": 200,
  "message": "操作成功",
  "data": [
    { "id": 1, "fileName": "1.jpg", ... },
    { "id": 2, "fileName": "2.jpg", ... }
  ]
}
```

### 5.3 获取媒体详情

```
GET /api/media/{id}

响应：
{
  "code": 200,
  "data": {
    "id": 1,
    "fileName": "photo.jpg",
    "fileUrl": "/uploads/image/2026/04/25/abc123.jpg",
    ...
  }
}
```

### 5.4 获取我的媒体列表

```
GET /api/media/list?page=1&pageSize=20&fileType=image

响应：
{
  "code": 200,
  "data": {
    "records": [...],
    "total": 100,
    "size": 20,
    "current": 1,
    "pages": 5
  }
}
```

### 5.5 删除媒体

```
DELETE /api/media/{id}

响应：
{
  "code": 200,
  "message": "操作成功"
}
```

---

## 六、前端集成指南

### 6.1 API 封装（js/api.js 添加）

```javascript
// 在 api.js 的 window.api 对象中添加
media: {
  upload: (file, onProgress) => {
    const formData = new FormData();
    formData.append('file', file);
    return axiosInstance.post('/media/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: onProgress
    });
  },
  uploadMultiple: (files, onProgress) => {
    const formData = new FormData();
    files.forEach((file, i) => formData.append('files', file));
    return axiosInstance.post('/media/upload/multiple', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: onProgress
    });
  },
  getList: (params) => axiosInstance.get('/media/list', { params }),
  delete: (id) => axiosInstance.delete('/media/' + id),
  bindPost: (postId, mediaIds) => axiosInstance.post('/media/bind/' + postId, mediaIds),
  getPostMedia: (postId) => axiosInstance.get('/media/post/' + postId)
}
```

### 6.2 图片上传组件示例

```html
<!-- 图片上传按钮 -->
<button type="button" class="btn btn-outline-primary" onclick="document.getElementById('imageInput').click()">
  📷 上传图片
</button>
<input type="file" id="imageInput" accept="image/*" style="display:none" onchange="handleImageUpload(this.files[0])">

<script>
// 图片上传处理
async function handleImageUpload(file) {
  if (!file) return;
  
  try {
    // 压缩图片
    const compressedFile = await compressImage(file);
    
    // 上传
    const res = await api.media.upload(compressedFile, (progress) => {
      console.log('上传进度:', progress.loaded / progress.total * 100 + '%');
    });
    
    // 插入 Markdown 图片到编辑器
    const imgMarkdown = `![${file.name}](${res.data.fileUrl})`;
    insertTextToEditor(imgMarkdown);
    
    utils.showToast('上传成功', 'success');
  } catch (err) {
    utils.showToast('上传失败', 'danger');
  }
}

// 图片压缩（使用 canvas）
function compressImage(file, maxWidth = 1920, quality = 0.85) {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;
        
        if (width > maxWidth) {
          height = height * maxWidth / width;
          width = maxWidth;
        }
        
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob((blob) => {
          resolve(new File([blob], file.name, { type: file.type }));
        }, file.type, quality);
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  });
}

// 插入文本到 Markdown 编辑器
function insertTextToEditor(text) {
  const textarea = document.getElementById('content');
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const before = textarea.value.substring(0, start);
  const after = textarea.value.substring(end);
  textarea.value = before + text + after;
  textarea.selectionStart = textarea.selectionEnd = start + text.length;
  textarea.focus();
}
</script>
```

---

## 七、实现工作量

| 阶段 | 内容 | 工作量 | 状态 |
|------|------|--------|------|
| **Phase 1** | 数据库 SQL 执行 | 30min | ⏳ |
| **Phase 2** | Entity/Mapper/Service/Controller | 4h | ⏳ |
| **Phase 3** | 配置文件更新 | 15min | ⏳ |
| **Phase 4** | 本地测试验证 | 1h | ⏳ |
| **Phase 5** | 前端集成 | 2h | ⏳ |
| **总计** | | **~8h** | |

---

## 八、注意事项

### 8.1 生产环境建议

1. **对象存储**：开发阶段使用本地存储，生产环境建议使用 OSS/COS/S3
2. **CDN 加速**：媒体文件使用 CDN 分发
3. **防盗链**：配置 Referer 检查
4. **文件清理**：定期清理孤立的媒体文件
5. **存储配额**：限制用户存储空间

### 8.2 安全考虑

1. 文件类型检查（不只是扩展名）
2. 文件名 sanitization（防止路径穿越）
3. 用户配额限制
4. 敏感操作需要登录验证

### 8.3 视频缩略图

当前实现未包含视频缩略图生成，需要 FFmpeg 支持。如需该功能，可扩展。

---

## 九、SQL 执行脚本汇总

```sql
-- 1. 创建媒体表
CREATE TABLE `blog_media` (...); -- 见 2.1

-- 2. 创建文章媒体关联表
CREATE TABLE `blog_post_media` (...); -- 见 2.2

-- 3. 添加文章封面图字段
ALTER TABLE `blog_post` ADD COLUMN `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL' AFTER `summary`;

-- 4. 添加用户头像字段（如果还没有）
ALTER TABLE `sys_user` ADD COLUMN `avatar` VARCHAR(500) DEFAULT NULL COMMENT '用户头像URL' AFTER `nickname`;
```

---

**文档版本**：v1.0
**创建日期**：2026-04-25
**负责人**：Claude AI
