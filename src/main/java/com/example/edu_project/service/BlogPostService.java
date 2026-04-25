package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.PostCreateRequest;
import com.example.edu_project.dto.PostQueryRequest;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.vo.PostDetailResponse;
import com.example.edu_project.vo.PostListResponse;

/**
 * 文章服务接口
 */
public interface BlogPostService extends IService<BlogPost> {

    /**
     * 创建文章
     * @param request 创建请求
     * @param userId 作者ID
     * @return 创建的文章ID
     */
    Long createPost(PostCreateRequest request, Long userId);

    /**
     * 更新文章
     * @param request 更新请求
     * @param userId 操作人ID
     */
    void updatePost(PostCreateRequest request, Long userId);

    /**
     * 删除文章
     * @param postId 文章ID
     * @param userId 操作人ID
     */
    void deletePost(Long postId, Long userId);

    /**
     * 获取文章详情
     * @param postId 文章ID
     * @return 文章详情
     */
    PostDetailResponse getPostDetail(Long postId);

    /**
     * 分页查询文章列表
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<PostListResponse> getPostList(PostQueryRequest request);

    /**
     * 增加阅读量
     * @param postId 文章ID
     */
    void incrementViewCount(Long postId);

    /**
     * 增加点赞数
     * @param postId 文章ID
     */
    void incrementLikeCount(Long postId);

    /**
     * 减少点赞数
     * @param postId 文章ID
     */
    void decrementLikeCount(Long postId);

    /**
     * 增加评论数
     * @param postId 文章ID
     */
    void incrementCommentCount(Long postId);

    /**
     * 减少评论数
     * @param postId 文章ID
     */
    void decrementCommentCount(Long postId);
}
