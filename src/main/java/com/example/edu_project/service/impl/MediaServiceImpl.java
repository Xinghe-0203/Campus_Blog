package com.example.edu_project.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.MediaQueryRequest;
import com.example.edu_project.entity.BlogPostMedia;
import com.example.edu_project.entity.Media;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.MediaMapper;
import com.example.edu_project.mapper.BlogPostMediaMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.service.MediaService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.MediaVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 媒体服务实现类
 */
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements MediaService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogPostMediaMapper blogPostMediaMapper;

    @Autowired
    private BlogPostService blogPostService;

    @Value("${upload.base-path:./uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Value("${upload.image.max-width:1920}")
    private int maxImageWidth;

    @Value("${upload.image.quality:0.85}")
    private double imageQuality;

    // 允许的图片类型
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    ));

    // 允许的视频类型
    private static final Set<String> ALLOWED_VIDEO_TYPES = new HashSet<>(Arrays.asList(
            "video/mp4", "video/webm"
    ));

    // 图片大小限制 10MB
    private static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024;

    // 视频大小限制 500MB
    private static final long VIDEO_MAX_SIZE = 500 * 1024 * 1024;

    // Magic Number 定义（文件头字节）
    // 图片
    private static final byte[] MAGIC_JPEG = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] MAGIC_PNG = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] MAGIC_GIF = new byte[]{0x47, 0x49, 0x46, 0x38};
    private static final byte[] MAGIC_WEBP_RIFF = new byte[]{0x52, 0x49, 0x46, 0x46}; // WebP starts with RIFF

    // 视频
    private static final byte[] MAGIC_MP4_FTYP = new byte[]{0x66, 0x74, 0x79, 0x70}; // ftyp box for MP4
    private static final byte[] MAGIC_WEBM = new byte[]{0x1A, 0x45, (byte) 0xDF, (byte) 0xA3}; // WebM EBML header

    /**
     * 校验文件 Magic Number（文件头字节）
     * 用于防止通过伪造 Content-Type 绕过文件类型检查
     *
     * @param file    上传的文件
     * @param isImage true=图片，false=视频
     * @throws BusinessException 如果文件类型不匹配
     */
    private void validateMagicNumber(MultipartFile file, boolean isImage) {
        try {
            byte[] header = new byte[12];
            byte[] fileBytes = file.getBytes();
            int headerLen = Math.min(12, fileBytes.length);
            System.arraycopy(fileBytes, 0, header, 0, headerLen);

            if (isImage) {
                // 图片校验：JPEG, PNG, GIF, WebP
                if (matchesMagic(header, MAGIC_JPEG) || matchesMagic(header, MAGIC_PNG)
                        || matchesMagic(header, MAGIC_GIF) || matchesWebp(header)) {
                    return;
                }
                throw new BusinessException(400, "图片文件格式无效，请上传真实的 jpg、png、gif 或 webp 图片");
            } else {
                // 视频校验：MP4, WebM
                if (matchesMagic(header, MAGIC_MP4_FTYP) || matchesWebm(header)) {
                    return;
                }
                throw new BusinessException(400, "视频文件格式无效，请上传真实的 mp4 或 webm 视频");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(400, "文件读取失败，无法校验文件类型");
        }
    }

    /**
     * 检查字节数组是否以指定的 magic number 开头
     */
    private boolean matchesMagic(byte[] header, byte[] magic) {
        if (header.length < magic.length) {
            return false;
        }
        for (int i = 0; i < magic.length; i++) {
            if (header[i] != magic[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否是 WebP 文件（RIFF....WEBP）
     */
    private boolean matchesWebp(byte[] header) {
        // WebP 格式：RIFF + 4字节长度 + WEBP
        if (!matchesMagic(header, MAGIC_WEBP_RIFF)) {
            return false;
        }
        // 检查第8-11字节是否为 "WEBP" (0x57 0x45 0x42 0x50)
        return header.length >= 12
                && header[8] == 0x57 && header[9] == 0x45 && header[10] == 0x42 && header[11] == 0x50;
    }

    /**
     * 检查是否是 WebM 文件
     */
    private boolean matchesWebm(byte[] header) {
        return matchesMagic(header, MAGIC_WEBM);
    }

    /**
     * 图片压缩处理
     * 如果图片宽度超过 maxImageWidth，进行比例缩放
     * 输出为 JPG 格式，质量为 imageQuality
     *
     * @param inputFile  原始图片文件
     * @param outputFile 压缩后的图片文件
     * @return 压缩后的文件
     * @throws IOException 如果压缩失败
     */
    private File processImage(File inputFile, File outputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new BusinessException(400, "无法读取图片文件");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        if (width > maxImageWidth) {
            // 宽度超过限制，按比例缩放
            double ratio = (double) maxImageWidth / width;
            int newHeight = (int) (height * ratio);
            Thumbnails.of(inputFile)
                    .width(maxImageWidth)
                    .height(newHeight)
                    .outputFormat("jpg")
                    .outputQuality(imageQuality)
                    .toFile(outputFile);
        } else {
            // 宽度未超过限制，只转换格式并压缩
            Thumbnails.of(inputFile)
                    .outputFormat("jpg")
                    .outputQuality(imageQuality)
                    .toFile(outputFile);
        }

        return outputFile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaVO uploadMedia(MultipartFile file, Long userId) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        boolean isImage = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType);
        boolean isVideo = contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType);

        if (!isImage && !isVideo) {
            throw new BusinessException(400, "只支持上传图片或视频文件，支持的图片格式：jpg、png、gif、webp，支持的视频格式：mp4、webm");
        }

        // 校验文件 Magic Number（防止伪造 Content-Type）
        validateMagicNumber(file, isImage);

        // 根据类型校验文件大小
        long maxSize = isVideo ? VIDEO_MAX_SIZE : IMAGE_MAX_SIZE;
        if (file.getSize() > maxSize) {
            String limitStr = isVideo ? "500MB" : "10MB";
            throw new BusinessException(400, "文件大小不能超过" + limitStr);
        }

        // 创建上传目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File uploadDir = new File(uploadPath, datePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 目标文件
        File destFile;
        long fileSize;
        String newFilename;

        if (isImage) {
            // 图片：压缩处理后保存
            newFilename = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            // 临时文件用于保存原始上传内容
            File tempFile = new File(uploadDir, UUID.randomUUID().toString().replace("-", "") + "_temp" + extension);
            destFile = new File(uploadDir, newFilename);

            try {
                // 先保存原始文件到临时文件
                file.transferTo(tempFile);

                // 调用图片压缩处理
                processImage(tempFile, destFile);

                // 删除临时文件
                tempFile.delete();

                // 获取压缩后的文件大小
                fileSize = destFile.length();
            } catch (IOException e) {
                // 清理临时文件
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                throw new BusinessException(500, "图片处理失败");
            }
        } else {
            // 视频：直接保存
            newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            destFile = new File(uploadDir, newFilename);
            try {
                file.transferTo(destFile);
            } catch (IOException e) {
                throw new BusinessException(500, "文件保存失败");
            }
            fileSize = file.getSize();
        }

        // 获取媒体尺寸信息
        Integer width = null;
        Integer height = null;

        if (isImage) {
            // 获取图片尺寸
            try {
                BufferedImage image = ImageIO.read(destFile);
                if (image != null) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
            } catch (IOException e) {
                // 忽略获取尺寸失败的错误
            }
        } else if (isVideo) {
            // 获取视频尺寸（需要引入 JCodec 或 FFmpeg 才能完整支持）
            try {
                int[] videoSize = getVideoSize(destFile);
                if (videoSize != null) {
                    width = videoSize[0];
                    height = videoSize[1];
                }
            } catch (Exception e) {
                // 忽略获取视频信息失败的错误
            }
        }

        // 构建访问URL
        String fileUrl = urlPrefix + "/" + datePath + "/" + newFilename;

        // 保存媒体记录
        Media media = new Media();
        media.setUserId(userId);
        media.setOriginalName(originalFilename);
        media.setFilePath(destFile.getAbsolutePath());
        media.setFileUrl(fileUrl);
        media.setThumbUrl(fileUrl); // TODO: 后续可生成缩略图
        media.setFileSize(fileSize);
        media.setMimeType(isImage ? "image/jpeg" : contentType);
        media.setWidth(width);
        media.setHeight(height);
        media.setStatus(1);

        this.save(media);

        // 返回 VO
        MediaVO vo = new MediaVO();
        vo.setId(media.getId());
        vo.setFileUrl(fileUrl);
        vo.setThumbUrl(media.getThumbUrl());
        vo.setFileSize(fileSize);
        vo.setWidth(width);
        vo.setHeight(height);

        return vo;
    }

    /**
     * 获取视频尺寸 [width, height]
     * 注意：完整实现需要引入 JCodec (http://jcodec.org/) 或使用 FFmpeg
     * 此处返回 null，后续可扩展
     */
    private int[] getVideoSize(File videoFile) {
        // 视频尺寸提取需要专门的库（如 JCodec），此处简化处理
        // 如需完整实现，可引入 JCodec 依赖：
        // <dependency>
        //     <groupId>org.jcodec</groupId>
        //     <artifactId>jcodec-javase</artifactId>
        //     <version>0.2.5</version>
        // </dependency>
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMedia(Long mediaId, Long userId) {
        Media media = this.getById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体文件不存在");
        }

        // 检查权限：上传者本人或管理员可以删除
        if (!media.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此媒体文件");
        }

        // 删除文件
        File file = new File(media.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        // 使用 MyBatis Plus 逻辑删除（@TableLogic）
        this.removeById(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaVO getMediaInfo(Long mediaId) {
        Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();

        Media media = this.getById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体文件不存在");
        }

        // 权限校验：上传者本人、管理员可查看，或者公开媒体（此处简化为需登录）
        if (currentUserId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (!media.getUserId().equals(currentUserId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权查看此媒体文件");
        }

        MediaVO vo = new MediaVO();
        vo.setId(media.getId());
        vo.setFileUrl(media.getFileUrl());
        vo.setThumbUrl(media.getThumbUrl());
        vo.setFileSize(media.getFileSize());
        vo.setWidth(media.getWidth());
        vo.setHeight(media.getHeight());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPostMedia(Long postId, List<Long> mediaIds) {
        if (postId == null) {
            throw new BusinessException(400, "文章ID不能为空");
        }

        // 权限校验：只有文章作者或管理员才能绑定媒体
        com.example.edu_project.entity.BlogPost post = blogPostService.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();
        if (currentUserId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (!post.getUserId().equals(currentUserId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权操作此文章的媒体");
        }

        if (mediaIds == null || mediaIds.isEmpty()) {
            // 如果媒体列表为空，则清除所有关联
            blogPostMediaMapper.deleteByPostId(postId);
            return;
        }

        // 删除旧关联
        blogPostMediaMapper.deleteByPostId(postId);

        // 插入新关联
        blogPostMediaMapper.batchInsert(postId, mediaIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaVO> getPostMedia(Long postId) {
        if (postId == null) {
            throw new BusinessException(400, "文章ID不能为空");
        }

        // 检查文章是否存在且已发布
        com.example.edu_project.entity.BlogPost post = blogPostService.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 只有已发布的文章才能查看媒体
        if (post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException(403, "文章未发布或已下架");
        }

        List<BlogPostMedia> postMediaList = blogPostMediaMapper.selectByPostId(postId);
        List<Long> mediaIds = postMediaList.stream()
                .map(BlogPostMedia::getMediaId)
                .collect(Collectors.toList());
        List<Media> mediaList = mediaIds.isEmpty() ? Collections.emptyList() : this.listByIds(mediaIds);

        return mediaList.stream().map(media -> {
            MediaVO vo = new MediaVO();
            vo.setId(media.getId());
            vo.setFileUrl(media.getFileUrl());
            vo.setThumbUrl(media.getThumbUrl());
            vo.setFileSize(media.getFileSize());
            vo.setWidth(media.getWidth());
            vo.setHeight(media.getHeight());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MediaVO> uploadFiles(MultipartFile[] files, Long userId) {
        if (files == null || files.length == 0) {
            throw new BusinessException(400, "文件不能为空");
        }
        if (files.length > 9) {
            throw new BusinessException(400, "最多只能上传9个文件");
        }

        List<MediaVO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                MediaVO mediaVO = uploadMedia(file, userId);
                result.add(mediaVO);
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaVO> getUserMedia(MediaQueryRequest request, Long userId) {
        Integer pageNum = request.getPage() != null ? request.getPage() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Page<Media> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Media> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Media::getUserId, userId);

        // 根据文件类型过滤
        if (StrUtil.isNotBlank(request.getFileType())) {
            wrapper.likeRight(Media::getMimeType, request.getFileType() + "/");
        }

        Page<Media> resultPage = this.page(page, wrapper);

        return resultPage.getRecords().stream().map(media -> {
            MediaVO vo = new MediaVO();
            vo.setId(media.getId());
            vo.setFileUrl(media.getFileUrl());
            vo.setThumbUrl(media.getThumbUrl());
            vo.setFileSize(media.getFileSize());
            vo.setWidth(media.getWidth());
            vo.setHeight(media.getHeight());
            return vo;
        }).collect(Collectors.toList());
    }
}
