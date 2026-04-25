package com.example.edu_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.CommentCreateRequest;
import com.example.edu_project.entity.BlogComment;
import com.example.edu_project.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface BlogCommentService extends IService<BlogComment> {

    /**
     * 发表评论
     * @param request 评论请求
     * @param userId 评论者ID
     * @return 评论ID
     */
    Long createComment(CommentCreateRequest request, Long userId);

    /**
     * 获取文章评论列表（树形结构）
     * @param postId 文章ID
     * @return 评论列表
     */
    List<CommentVO> getCommentsByPostId(Long postId);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 操作人ID
     */
    void deleteComment(Long commentId, Long userId);
}
