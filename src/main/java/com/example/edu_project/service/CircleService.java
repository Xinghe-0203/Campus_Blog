package com.example.edu_project.service;

import com.example.edu_project.vo.CircleCommentVO;
import com.example.edu_project.vo.CirclePostVO;

import java.util.List;

/**
 * 校友圈服务接口
 */
public interface CircleService {

    /**
     * 发布动态
     * @param content 内容
     * @param imageUrls 图片URL列表
     * @param location 位置
     * @param repostId 转发来源ID
     * @param tags 标签列表
     * @param userId 用户ID
     * @param visibility 可见性
     * @param allowComment 是否允许评论
     * @param allowRepost 是否允许转发
     * @return 动态ID
     */
    Long createPost(String content, List<String> imageUrls, String location, Long repostId,
                    List<String> tags, Long userId, String visibility, Integer allowComment, Integer allowRepost);

    /**
     * 删除动态
     * @param postId 动态ID
     * @param userId 用户ID
     */
    void deletePost(Long postId, Long userId);

    /**
     * 获取动态详情
     * @param postId 动态ID
     * @param userId 当前用户ID
     * @return 动态详情
     */
    CirclePostVO getPostDetail(Long postId, Long userId);

    /**
     * 点赞/取消点赞
     * @param postId 动态ID
     * @param userId 用户ID
     */
    void toggleLike(Long postId, Long userId);

    /**
     * 检查是否已点赞
     * @param postId 动态ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    Boolean checkLikeStatus(Long postId, Long userId);

    /**
     * 获取动态评论列表
     * @param postId 动态ID
     * @param userId 当前用户ID
     * @return 评论列表
     */
    List<CircleCommentVO> getComments(Long postId, Long userId);

    /**
     * 发表评论
     * @param postId 动态ID
     * @param content 评论内容
     * @param parentId 父评论ID
     * @param replyToUserId 回复的用户ID
     * @param userId 评论者ID
     * @return 评论ID
     */
    Long createComment(Long postId, String content, Long parentId, Long replyToUserId, Long userId);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 转发动态
     * @param postId 原动态ID
     * @param content 转发内容
     * @param userId 用户ID
     * @return 新动态ID
     */
    Long repostPost(Long postId, String content, Long userId);

    /**
     * 搜索动态
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 当前用户ID
     * @return 动态列表
     */
    List<CirclePostVO> searchPosts(String keyword, int page, int pageSize, Long userId);

    /**
     * 获取关注流
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 当前用户ID
     * @return 动态列表
     */
    List<CirclePostVO> getFollowingFeed(int page, int pageSize, Long userId);

    /**
     * 获取推荐流
     * @param page 页码
     * @param pageSize 每页数量
     * @param userId 当前用户ID
     * @return 动态列表
     */
    List<CirclePostVO> getRecommendFeed(int page, int pageSize, Long userId);
}