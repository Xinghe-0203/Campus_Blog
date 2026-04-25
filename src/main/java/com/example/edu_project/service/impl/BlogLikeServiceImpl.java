package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogLike;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.mapper.BlogLikeMapper;
import com.example.edu_project.service.BlogLikeService;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.vo.LikeResultVO;
import com.example.edu_project.vo.LikeStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务实现类
 */
@Service
public class BlogLikeServiceImpl extends ServiceImpl<BlogLikeMapper, BlogLike> implements BlogLikeService {

    @Autowired
    private BlogPostService blogPostService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeResultVO toggleLike(Long postId, Long userId) {
        // 检查文章是否存在
        BlogPost post = blogPostService.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }

        // 检查是否已点赞
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getUserId, userId)
              .eq(BlogLike::getPostId, postId);
        BlogLike existingLike = this.getOne(wrapper);

        LikeResultVO result = new LikeResultVO();

        if (existingLike != null) {
            // 取消点赞：删除记录
            this.removeById(existingLike.getId());
            // 更新文章点赞数-1
            blogPostService.decrementLikeCount(postId);
            result.setAction("unlike");
        } else {
            // 点赞：添加记录
            BlogLike newLike = new BlogLike();
            newLike.setUserId(userId);
            newLike.setPostId(postId);
            this.save(newLike);
            // 更新文章点赞数+1
            blogPostService.incrementLikeCount(postId);
            result.setAction("like");
        }

        // 获取最新点赞数
        BlogPost updatedPost = blogPostService.getById(postId);
        result.setLikeCount(updatedPost.getLikeCount());

        return result;
    }

    @Override
    public LikeStatusVO checkLikeStatus(Long postId, Long userId) {
        LikeStatusVO status = new LikeStatusVO();

        // 检查是否已点赞
        boolean liked = hasLiked(postId, userId);
        status.setLiked(liked);

        // 获取文章点赞数
        BlogPost post = blogPostService.getById(postId);
        if (post != null) {
            status.setLikeCount(post.getLikeCount());
        } else {
            status.setLikeCount(0);
        }

        return status;
    }

    @Override
    public boolean hasLiked(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getUserId, userId)
              .eq(BlogLike::getPostId, postId);
        return this.count(wrapper) > 0;
    }
}
