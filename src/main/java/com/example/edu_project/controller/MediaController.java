package com.example.edu_project.controller;

import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.MediaQueryRequest;
import com.example.edu_project.service.MediaService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.MediaVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 媒体控制器
 */
@Tag(name = "媒体管理", description = "媒体文件上传相关接口")
@RestController
@RequestMapping("/media")
@Validated
public class MediaController {

    @Autowired
    private MediaService mediaService;

    /**
     * 上传媒体文件
     */
    @Operation(summary = "上传媒体文件")
    @PostMapping("/upload")
    public Result<MediaVO> uploadMedia(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        MediaVO media = mediaService.uploadMedia(file, userId);
        return Result.success(media);
    }

    /**
     * 删除媒体文件
     */
    @Operation(summary = "删除媒体文件")
    @DeleteMapping("/{mediaId}")
    public Result<Void> deleteMedia(@PathVariable Long mediaId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        mediaService.deleteMedia(mediaId, userId);
        return Result.success(null);
    }

    /**
     * 获取媒体信息
     */
    @Operation(summary = "获取媒体信息")
    @GetMapping("/{mediaId}")
    public Result<MediaVO> getMediaInfo(@PathVariable Long mediaId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        MediaVO media = mediaService.getMediaInfo(mediaId);
        return Result.success(media);
    }

    /**
     * 绑定文章媒体
     */
    @Operation(summary = "绑定文章媒体")
    @PostMapping("/bind/{postId}")
    public Result<Void> bindPostMedia(@PathVariable Long postId, @Valid @RequestBody @Size(max = 20, message = "最多绑定20个媒体") List<Long> mediaIds) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        mediaService.bindPostMedia(postId, mediaIds);
        return Result.success(null);
    }

    /**
     * 获取文章的媒体列表
     */
    @Operation(summary = "获取文章的媒体列表")
    @GetMapping("/post/{postId}")
    public Result<List<MediaVO>> getPostMedia(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        List<MediaVO> mediaList = mediaService.getPostMedia(postId);
        return Result.success(mediaList);
    }

    /**
     * 批量上传媒体文件
     */
    @Operation(summary = "批量上传媒体文件（最多9个）")
    @PostMapping("/upload/multiple")
    public Result<List<MediaVO>> uploadMultipleMedia(@RequestParam("files") MultipartFile[] files) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        List<MediaVO> mediaList = mediaService.uploadFiles(files, userId);
        return Result.success(mediaList);
    }

    /**
     * 分页查询用户媒体列表
     */
    @Operation(summary = "分页查询用户媒体列表")
    @GetMapping("/list")
    public Result<List<MediaVO>> getUserMediaList(@Valid MediaQueryRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }

        List<MediaVO> mediaList = mediaService.getUserMedia(request, userId);
        return Result.success(mediaList);
    }
}