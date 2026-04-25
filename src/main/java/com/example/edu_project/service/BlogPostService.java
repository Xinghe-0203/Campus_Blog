package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.PostAdvancedSearchRequest;
import com.example.edu_project.dto.PostCreateRequest;
import com.example.edu_project.dto.PostQueryRequest;
import com.example.edu_project.dto.SaveDraftRequest;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.vo.PostDetailResponse;
import com.example.edu_project.vo.PostListResponse;

import java.util.List;

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

    /**
     * 减少评论数（支持批量）
     * @param postId 文章ID
     * @param count 减少数量
     */
    void decrementCommentCount(Long postId, int count);

    /**
     * 增加收藏数
     * @param postId 文章ID
     */
    void incrementCollectCount(Long postId);

    /**
     * 减少收藏数
     * @param postId 文章ID
     */
    void decrementCollectCount(Long postId);

    /**
     * 保存草稿
     * @param userId 用户ID
     * @param request 草稿请求
     * @return 草稿ID
     */
    Long saveDraft(Long userId, SaveDraftRequest request);

    /**
     * 获取我的最新草稿
     * @param userId 用户ID
     * @return 最新草稿
     */
    SaveDraftRequest getLatestDraft(Long userId);

    /**
     * 删除草稿
     * @param draftId 草稿ID
     * @param userId 操作人ID
     */
    void deleteDraft(Long draftId, Long userId);

    /**
     * 获取指定草稿
     * @param draftId 草稿ID
     * @param userId 操作人ID
     * @return 草稿详情
     */
    SaveDraftRequest getDraft(Long draftId, Long userId);

    /**
     * 文章高级搜索
     * @param request 高级搜索请求
     * @return 分页结果
     */
    IPage<PostListResponse> advancedSearch(PostAdvancedSearchRequest request);

    /**
     * 获取搜索建议（标题自动补全）
     * @param keyword 关键词
     * @return 建议标题列表
     */
    List<String> getSearchSuggestions(String keyword);
}
