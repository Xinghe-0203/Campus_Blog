package com.example.edu_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.MediaQueryRequest;
import com.example.edu_project.entity.Media;
import com.example.edu_project.vo.MediaVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 媒体服务接口
 */
public interface MediaService extends IService<Media> {

    /**
     * 上传媒体文件
     * @param file 文件
     * @param userId 上传用户ID
     * @return 媒体信息
     */
    MediaVO uploadMedia(MultipartFile file, Long userId);

    /**
     * 批量上传媒体文件
     * @param files 文件数组（最多9个）
     * @param userId 上传用户ID
     * @return 媒体信息列表
     */
    List<MediaVO> uploadFiles(MultipartFile[] files, Long userId);

    /**
     * 删除媒体文件
     * @param mediaId 媒体ID
     * @param userId 操作用户ID
     */
    void deleteMedia(Long mediaId, Long userId);

    /**
     * 获取媒体信息
     * @param mediaId 媒体ID
     * @return 媒体信息
     */
    MediaVO getMediaInfo(Long mediaId);

    /**
     * 分页查询用户媒体
     * @param request 查询请求
     * @param userId 用户ID
     * @return 媒体信息列表
     */
    List<MediaVO> getUserMedia(MediaQueryRequest request, Long userId);

    /**
     * 绑定文章媒体关联
     * @param postId 文章ID
     * @param mediaIds 媒体ID列表
     */
    void bindPostMedia(Long postId, List<Long> mediaIds);

    /**
     * 获取文章的媒体列表
     * @param postId 文章ID
     * @return 媒体列表
     */
    List<MediaVO> getPostMedia(Long postId);
}