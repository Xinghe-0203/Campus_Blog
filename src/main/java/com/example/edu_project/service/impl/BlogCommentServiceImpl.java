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
import com.example.edu_project.utils.HtmlSanitizer;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.event.CommentCreatedEvent;
import com.example.edu_project.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    @Autowired
    private HtmlSanitizer htmlSanitizer;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
            if (!request.getPostId().equals(parentComment.getPostId())) {
                throw new BusinessException(400, "父评论不属于该文章");
            }
        }

        // 创建评论
        // XSS 防护：评论使用严格策略，只保留纯文本，移除所有 HTML 标签
        String sanitizedContent = htmlSanitizer.sanitizePlainText(request.getContent());

        BlogComment comment = new BlogComment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(sanitizedContent);
        this.save(comment);

        // 更新文章评论数
        blogPostService.incrementCommentCount(request.getPostId());

        // 发布评论创建事件，事务提交后异步发送通知
        eventPublisher.publishEvent(new CommentCreatedEvent(
                comment.getId(),
                userId,
                post.getUserId(),
                request.getPostId(),
                sanitizedContent,
                request.getParentId() != null
        ));

        return comment.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentVO> getCommentsByPostId(Long postId) {
        // 先检查文章是否存在且已发布
        BlogPost post = blogPostService.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 只有已发布的文章才能查看评论
        if (post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException(403, "文章未发布或已下架");
        }

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

        // 先将评论列表转为 Map，ID -> Comment，用于 O(1) 查找父评论
        Map<Long, BlogComment> commentMapById = comments.stream()
                .collect(Collectors.toMap(BlogComment::getId, c -> c));

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
                    // 如果是回复，使用 Map O(1) 查找父评论（替代原来的 O(n) stream filter）
                    if (comment.getParentId() != null) {
                        BlogComment parentComment = commentMapById.get(comment.getParentId());
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

        // 构建树形结构（使用已有的 commentVOs 构建 Map）
        List<CommentVO> rootComments = new ArrayList<>();
        Map<Long, CommentVO> voMap = commentVOs.stream()
                .collect(Collectors.toMap(CommentVO::getId, c -> c));

        for (CommentVO vo : commentVOs) {
            if (vo.getParentId() == null) {
                // 一级评论
                rootComments.add(vo);
            } else {
                // 子评论，添加到父评论的replies中
                CommentVO parent = voMap.get(vo.getParentId());
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

        // 查找所有要删除的评论ID（包括当前评论及其所有子评论）
        List<Long> commentIdsToDelete = new ArrayList<>();
        commentIdsToDelete.add(commentId);
        collectChildCommentIds(commentId, commentIdsToDelete, 1);

        // 批量删除所有相关评论（逻辑删除）
        this.removeByIds(commentIdsToDelete);

        // 更新文章评论数（减去实际删除的数量）
        blogPostService.decrementCommentCount(comment.getPostId(), commentIdsToDelete.size());
    }

    private static final int MAX_RECURSION_DEPTH = 100; // 最大递归深度，防止栈溢出

    /**
     * 递归收集所有子评论ID
     */
    private void collectChildCommentIds(Long parentId, List<Long> result, int currentDepth) {
        if (currentDepth > MAX_RECURSION_DEPTH) {
            throw new BusinessException(400, "评论层级过深，请简化回复结构");
        }
        LambdaQueryWrapper<BlogComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogComment::getParentId, parentId);
        List<BlogComment> childComments = this.list(wrapper);

        for (BlogComment child : childComments) {
            result.add(child.getId());
            // 递归收集子评论的子评论
            collectChildCommentIds(child.getId(), result, currentDepth + 1);
        }
    }
}
