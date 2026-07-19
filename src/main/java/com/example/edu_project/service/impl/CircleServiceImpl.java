package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.CircleService;
import com.example.edu_project.vo.CircleCommentVO;
import com.example.edu_project.vo.CirclePostVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 校友圈服务实现类
 */
@Slf4j
@Service
public class CircleServiceImpl implements CircleService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Long createPost(String content, List<String> imageUrls, String location, Long repostId,
                           List<String> tags, Long userId, String visibility, Integer allowComment, Integer allowRepost) {
        // TODO: 实现发布动态
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        // TODO: 实现删除动态
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public CirclePostVO getPostDetail(Long postId, Long userId) {
        // TODO: 实现获取动态详情
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public void toggleLike(Long postId, Long userId) {
        // TODO: 实现点赞/取消点赞
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public Boolean checkLikeStatus(Long postId, Long userId) {
        // TODO: 实现检查点赞状态
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public List<CircleCommentVO> getComments(Long postId, Long userId) {
        // TODO: 实现获取评论列表
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public Long createComment(Long postId, String content, Long parentId, Long replyToUserId, Long userId) {
        // TODO: 实现发表评论
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        // TODO: 实现删除评论
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public Long repostPost(Long postId, String content, Long userId) {
        // TODO: 实现转发动态
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public List<CirclePostVO> searchPosts(String keyword, int page, int pageSize, Long userId) {
        // TODO: 实现搜索动态
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public List<CirclePostVO> getFollowingFeed(int page, int pageSize, Long userId) {
        // TODO: 实现获取关注流
        throw new BusinessException(500, "功能暂未实现");
    }

    @Override
    public List<CirclePostVO> getRecommendFeed(int page, int pageSize, Long userId) {
        // TODO: 实现获取推荐流
        throw new BusinessException(500, "功能暂未实现");
    }
}