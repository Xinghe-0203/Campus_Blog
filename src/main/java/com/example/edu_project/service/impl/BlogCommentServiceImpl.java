package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.CommentCreateRequest;
import com.example.edu_project.entity.BlogComment;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogCommentMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.BlogCommentService;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentCreateRequest request, Long userId) {
        // 检查文章是否存在
        BlogPost post = blogPostService.getById(request.getPostId());
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }

        // 如果是回复，检查父评论是否存在
        if (request.getParentId() != null) {
            BlogComment parentComment = this.getById(request.getParentId());
            if (parentComment == null) {
                throw new BusinessException(404, "父评论不存在");
            }
            // 检查父评论是否属于同一篇文章
            if (!parentComment.getPostId().equals(request.getPostId())) {
                throw new BusinessException(400, "父评论不属于该文章");
            }
        }

        // 创建评论
        BlogComment comment = new BlogComment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        this.save(comment);

        // 更新文章评论数
        blogPostService.incrementCommentCount(request.getPostId());

        return comment.getId();
    }

    @Override
    public List<CommentVO> getCommentsByPostId(Long postId) {
        // 查询该文章的所有评论
        LambdaQueryWrapper<BlogComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogComment::getPostId, postId)
              .orderByAsc(BlogComment::getCreateTime);
        List<BlogComment> comments = this.list(wrapper);

        if (comments.isEmpty()) {
            return List.of();
        }

        // 获取所有评论者用户ID
        List<Long> userIds = comments.stream()
                .map(BlogComment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        // 转换为VO
        List<CommentVO> commentVOs = comments.stream()
                .map(comment -> {
                    CommentVO vo = new CommentVO();
                    BeanUtils.copyProperties(comment, vo);
                    SysUser user = userMap.get(comment.getUserId());
                    if (user != null) {
                        vo.setUserNickname(user.getNickname());
                        vo.setUserAvatar(user.getAvatar());
                    }
                    // 如果是回复，设置回复的用户昵称
                    if (comment.getParentId() != null) {
                        BlogComment parentComment = comments.stream()
                                .filter(c -> c.getId().equals(comment.getParentId()))
                                .findFirst()
                                .orElse(null);
                        if (parentComment != null) {
                            SysUser parentUser = userMap.get(parentComment.getUserId());
                            if (parentUser != null) {
                                vo.setReplyToNickname(parentUser.getNickname());
                            }
                        }
                    }
                    vo.setReplies(new ArrayList<>());
                    return vo;
                })
                .collect(Collectors.toList());

        // 构建树形结构
        List<CommentVO> rootComments = new ArrayList<>();
        Map<Long, CommentVO> commentMap = commentVOs.stream()
                .collect(Collectors.toMap(CommentVO::getId, c -> c));

        for (CommentVO vo : commentVOs) {
            if (vo.getParentId() == null) {
                // 一级评论
                rootComments.add(vo);
            } else {
                // 子评论，添加到父评论的replies中
                CommentVO parent = commentMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getReplies().add(vo);
                }
            }
        }

        return rootComments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        BlogComment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }

        // 检查权限：作者本人或管理员可删除
        if (!comment.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此评论");
        }

        // 删除评论（逻辑删除）
        this.removeById(commentId);

        // 更新文章评论数
        blogPostService.decrementCommentCount(comment.getPostId());
    }
}
