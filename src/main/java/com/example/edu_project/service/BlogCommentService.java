package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.CommentCreateRequest;
import com.example.edu_project.entity.BlogComment;
import com.example.edu_project.vo.CommentVO;
import com.example.edu_project.vo.CommentWithPostVO;

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

    /**
     * 获取我的评论列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    IPage<CommentWithPostVO> getMyComments(Long userId, Integer page, Integer pageSize);

    /**
     * 管理员删除评论（级联删除子评论，更新文章评论数）
     * @param commentId 评论ID
     * @param adminId 管理员ID
     */
    void adminDeleteComment(Long commentId, Long adminId);
}
