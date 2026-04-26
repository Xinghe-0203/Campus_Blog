package com.example.edu_project.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.*;
import com.example.edu_project.mapper.*;
import com.example.edu_project.service.CircleService;
import com.example.edu_project.service.FollowService;
import com.example.edu_project.utils.HtmlSanitizer;
import com.example.edu_project.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 校友圈服务实现类
 */
@Service
public class CircleServiceImpl extends ServiceImpl<CirclePostMapper, CirclePost> implements CircleService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private FollowService followService;

    @Autowired
    private CircleLikeMapper circleLikeMapper;

    @Autowired
    private CircleCommentMapper circleCommentMapper;

    @Autowired
    private CircleRepostMapper circleRepostMapper;

    @Autowired
    private HtmlSanitizer htmlSanitizer;

    /**
     * 细粒度锁映射表：key="userId-postId"，value=锁对象
     * 用于解决同一用户对同一动态的点赞/取消点赞操作的并发问题
     */
    private static final int MAX_LOCKS_SIZE = 10000;
    private static final long LOCK_EXPIRE_MS = 300000; // 5分钟
    private final ConcurrentMap<String, LockEntry> likeLocks = new ConcurrentHashMap<>();

    private static class LockEntry {
        final Object lock;
        final long createTime;

        LockEntry(Object lock) {
            this.lock = lock;
            this.createTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createTime > LOCK_EXPIRE_MS;
        }
    }

    /**
     * 获取锁，如果锁已过期则移除并返回新锁
     */
    private Object getLock(String lockKey) {
        if (likeLocks.size() >= MAX_LOCKS_SIZE) {
            likeLocks.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }

        LockEntry entry = likeLocks.compute(lockKey, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new LockEntry(new Object());
            }
            return existing;
        });
        return entry.lock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPost(String content, List<String> images, String location, Long repostId,
                           List<String> tags, Long userId,
                           Integer visibility, Integer allowComment, Integer allowRepost) {
        // 参数校验
        if (StrUtil.isBlank(content) && (images == null || images.isEmpty()) && repostId == null) {
            throw new BusinessException(400, "动态内容不能为空");
        }
        if (content != null && content.length() > 2000) {
            throw new BusinessException(400, "动态内容不能超过2000字符");
        }
        if (location != null && location.length() > 100) {
            throw new BusinessException(400, "位置信息不能超过100字符");
        }

        // 如果是转发，校验原动态是否存在
        Integer contentType = 1; // 纯文本
        if (repostId != null) {
            CirclePost originalPost = this.getById(repostId);
            if (originalPost == null || originalPost.getStatus() == 2) {
                throw new BusinessException(404, "原动态不存在");
            }
            // 检查原动态是否允许转发
            if (originalPost.getAllowRepost() != null && originalPost.getAllowRepost() == 0) {
                throw new BusinessException(403, "该动态禁止转发");
            }
            // 检查是否有权限查看原动态（才能转发）
            if (!canViewPost(originalPost, userId)) {
                throw new BusinessException(403, "无权转发此动态");
            }
            // 如果原动态是仅自己可见，转发时自动设为仅关注者可见
            if (originalPost.getVisibility() != null && originalPost.getVisibility() == 2) {
                visibility = 1;
            }
            contentType = 3; // 转发
        } else if (images != null && !images.isEmpty()) {
            contentType = 2; // 图文
        }

        // 创建动态
        CirclePost post = new CirclePost();
        post.setUserId(userId);
        post.setContent(content);
        post.setContentType(contentType);
        post.setLocation(location);
        post.setRepostId(repostId);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setRepostCount(0);
        post.setIsTop(0);
        post.setStatus(1);
        // 可见性设置
        post.setVisibility(visibility != null ? visibility : 0);
        post.setAllowComment(allowComment != null ? allowComment : 1);
        post.setAllowRepost(allowRepost != null ? allowRepost : 1);

        // 图片列表转为 JSON
        if (images != null && !images.isEmpty()) {
            post.setImageUrls(cn.hutool.json.JSONUtil.toJsonStr(images));
        }

        // 标签列表转为 JSON
        if (tags != null && !tags.isEmpty()) {
            post.setTags(cn.hutool.json.JSONUtil.toJsonStr(tags));
        }

        this.save(post);

        // 如果是转发，增加原动态的转发数
        if (repostId != null) {
            baseMapper.incrementRepostCount(repostId);
        }

        return post.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        CirclePost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "动态不存在");
        }
        if (post.getStatus() == 2) {
            throw new BusinessException(404, "动态已删除");
        }
        // 检查权限：作者本人或管理员可以删除
        if (!post.getUserId().equals(userId) && !com.example.edu_project.utils.SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此动态");
        }

        // 如果是转发，减少原动态的转发数
        if (post.getRepostId() != null) {
            baseMapper.decrementRepostCount(post.getRepostId());
        }

        // 级联删除关联数据（物理删除）
        circleCommentMapper.deleteByPostId(postId);
        circleLikeMapper.deleteByPostId(postId);
        circleRepostMapper.deleteByOriginalPostId(postId);

        // 软删除：设置 status = 2
        post.setStatus(2);
        this.updateById(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CirclePostVO> getRecommendFeed(int page, int pageSize, Long currentUserId) {
        LambdaQueryWrapper<CirclePost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CirclePost::getStatus, 1) // 只查询正常状态的动态
                // 可见性过滤：公开动态(visibility=0)对所有人可见，作者本人可看自己的非公开动态
                .and(w -> w.eq(CirclePost::getVisibility, 0) // 公开动态对所有人可见
                        .or(currentUserId != null, w2 -> w2
                                .eq(CirclePost::getUserId, currentUserId)
                                .ne(CirclePost::getVisibility, 0))) // 作者本人可看自己非公开的动态
                .orderByDesc(CirclePost::getIsTop) // 置顶优先
                .orderByDesc(CirclePost::getCreateTime); // 然后按时间

        // 分页查询
        List<CirclePost> posts = this.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                wrapper
        ).getRecords();

        return convertToVOList(posts, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CirclePostVO> getFollowingFeed(int page, int pageSize, Long userId) {
        // 获取当前用户关注的所有用户ID
        List<UserVO> followingList = followService.getFollowing(userId);
        if (followingList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> followingUserIds = followingList.stream()
                .map(UserVO::getId)
                .collect(Collectors.toList());

        // 查询关注用户的动态，考虑可见性：
        // - 公开(visibility=0)：所有人都可见
        // - 仅关注者(visibility=1)：需要关注才能看
        // - 仅自己(visibility=2)：只有作者自己能看
        LambdaQueryWrapper<CirclePost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CirclePost::getStatus, 1) // 只查询正常状态的动态
                .in(CirclePost::getUserId, followingUserIds) // 关注的人的动态
                .and(w -> w
                        .eq(CirclePost::getVisibility, 0) // 公开动态
                        .or() // 或者
                        .eq(CirclePost::getVisibility, 1) // 仅关注者可见的
                        .or() // 或者
                        .eq(CirclePost::getVisibility, 2).eq(CirclePost::getUserId, userId)) // 仅自己可见但作者是自己
                .orderByDesc(CirclePost::getIsTop) // 置顶优先
                .orderByDesc(CirclePost::getCreateTime); // 然后按时间

        // 分页查询
        List<CirclePost> posts = this.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                wrapper
        ).getRecords();

        return convertToVOList(posts, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CirclePostVO getPostDetail(Long postId, Long currentUserId) {
        CirclePost post = this.getById(postId);
        if (post == null || post.getStatus() == 2) {
            throw new BusinessException(404, "动态不存在");
        }

        // 可见性检查
        if (!canViewPost(post, currentUserId)) {
            throw new BusinessException(403, "无权查看此动态");
        }

        // 增加阅读量
        baseMapper.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);

        List<CirclePost> posts = Collections.singletonList(post);
        List<CirclePostVO> voList = convertToVOList(posts, currentUserId);
        return voList.isEmpty() ? null : voList.get(0);
    }

    /**
     * 检查当前用户是否有权查看该动态
     */
    private boolean canViewPost(CirclePost post, Long currentUserId) {
        // 作者本人总是可以看
        if (currentUserId != null && post.getUserId() != null && post.getUserId().equals(currentUserId)) {
            return true;
        }

        // 公开动态谁都可以看
        if (post.getVisibility() == null || post.getVisibility() == 0) {
            return true;
        }

        // 仅关注者可见，需要检查是否关注了作者
        if (post.getVisibility() == 1) {
            if (currentUserId == null) {
                return false;
            }
            return followService.isFollowing(post.getUserId(), currentUserId);
        }

        // 仅自己可见，只有作者能看（已在上面处理）
        return false;
    }

    /**
     * 批量转换为 VO（存在 N+1 问题，计划书已有此问题暂不优化）
     */
    private List<CirclePostVO> convertToVOList(List<CirclePost> posts, Long currentUserId) {
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询当前用户的点赞和转发状态
        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> repostedPostIds = new HashSet<>();
        if (currentUserId != null && !posts.isEmpty()) {
            List<Long> postIds = posts.stream().map(CirclePost::getId).collect(Collectors.toList());

            // 查询点赞状态
            LambdaQueryWrapper<CircleLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(CircleLike::getUserId, currentUserId)
                       .eq(CircleLike::getIsDeleted, 0)
                       .in(CircleLike::getPostId, postIds);
            List<CircleLike> likes = circleLikeMapper.selectList(likeWrapper);
            likedPostIds = new HashSet<>(likes.stream().map(CircleLike::getPostId).toList());

            // 查询转发状态
            LambdaQueryWrapper<CircleRepost> repostWrapper = new LambdaQueryWrapper<>();
            repostWrapper.eq(CircleRepost::getUserId, currentUserId)
                         .eq(CircleRepost::getIsDeleted, 0)
                         .in(CircleRepost::getOriginalPostId, postIds);
            List<CircleRepost> reposts = circleRepostMapper.selectList(repostWrapper);
            repostedPostIds = new HashSet<>(reposts.stream().map(CircleRepost::getOriginalPostId).toList());
        }

        final Set<Long> finalLikedPostIds = likedPostIds;
        final Set<Long> finalRepostedPostIds = repostedPostIds;

        return posts.stream().map(post -> {
            CirclePostVO vo = new CirclePostVO();
            vo.setId(post.getId());
            vo.setContent(post.getContent());
            vo.setContentType(post.getContentType());
            vo.setLocation(post.getLocation());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setRepostCount(post.getRepostCount());
            vo.setViewCount(post.getViewCount());
            vo.setIsTop(post.getIsTop() == 1);
            vo.setIsLiked(finalLikedPostIds.contains(post.getId()));
            vo.setIsReposted(finalRepostedPostIds.contains(post.getId()));
            vo.setVisibility(post.getVisibility());
            vo.setAllowComment(post.getAllowComment());
            vo.setAllowRepost(post.getAllowRepost());
            vo.setCreateTime(post.getCreateTime());
            vo.setTimeAgo(calculateTimeAgo(post.getCreateTime()));

            // 图片列表
            if (StrUtil.isNotBlank(post.getImageUrls())) {
                vo.setImageUrls(cn.hutool.json.JSONUtil.toList(post.getImageUrls(), String.class));
            } else {
                vo.setImageUrls(new ArrayList<>());
            }

            // 标签列表
            if (StrUtil.isNotBlank(post.getTags())) {
                vo.setTags(cn.hutool.json.JSONUtil.toList(post.getTags(), String.class));
            } else {
                vo.setTags(new ArrayList<>());
            }

            // 获取作者信息（每次单独查询，存在 N+1 问题）
            SysUser user = sysUserMapper.selectById(post.getUserId());
            if (user != null) {
                UserVO userVO = new UserVO();
                userVO.setId(user.getId());
                userVO.setUsername(user.getUsername());
                userVO.setNickname(user.getNickname());
                userVO.setAvatar(user.getAvatar());
                vo.setUser(userVO);
            }

            // 如果是转发，获取原动态信息（需检查可见性）
            if (post.getRepostId() != null) {
                CirclePost repostPost = this.getById(post.getRepostId());
                if (repostPost != null && repostPost.getStatus() != 2) {
                    // 检查是否有权限查看原动态
                    if (canViewPost(repostPost, currentUserId)) {
                        CirclePostVO repostVO = new CirclePostVO();
                        repostVO.setId(repostPost.getId());
                        repostVO.setContent(repostPost.getContent());
                        repostVO.setContentType(repostPost.getContentType());
                        repostVO.setVisibility(repostPost.getVisibility());
                        repostVO.setAllowComment(repostPost.getAllowComment());
                        repostVO.setAllowRepost(repostPost.getAllowRepost());

                        // 原动态作者信息
                        SysUser repostUser = sysUserMapper.selectById(repostPost.getUserId());
                        if (repostUser != null) {
                            UserVO repostUserVO = new UserVO();
                            repostUserVO.setId(repostUser.getId());
                            repostUserVO.setUsername(repostUser.getUsername());
                            repostUserVO.setNickname(repostUser.getNickname());
                            repostUserVO.setAvatar(repostUser.getAvatar());
                            repostVO.setUser(repostUserVO);
                        }

                        // 原动态图片
                        if (StrUtil.isNotBlank(repostPost.getImageUrls())) {
                            repostVO.setImageUrls(cn.hutool.json.JSONUtil.toList(repostPost.getImageUrls(), String.class));
                        }

                        repostVO.setCreateTime(repostPost.getCreateTime());
                        repostVO.setTimeAgo(calculateTimeAgo(repostPost.getCreateTime()));

                        vo.setRepostPost(repostVO);
                    } else {
                        // 无权查看原动态，设置一个标记，前端可据此显示"此动态已不可见"
                        vo.setRepostPost(null);
                        vo.setOriginalPostHidden(true);
                    }
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 计算时间 ago 描述
     */
    private String calculateTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        long seconds = Duration.between(dateTime, now).getSeconds();

        if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分钟前";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "小时前";
        } else if (seconds < 604800) {
            return (seconds / 86400) + "天前";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    // ==================== 点赞相关方法 ====================

    /**
     * 点赞/取消点赞
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long postId, Long userId) {
        // 检查动态是否存在
        CirclePost post = this.getById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException(404, "动态不存在");
        }

        // 检查可见性权限
        if (!canViewPost(post, userId)) {
            throw new BusinessException(403, "无权操作此动态");
        }

        // 使用细粒度锁
        String lockKey = userId + "-" + postId;
        synchronized (getLock(lockKey)) {
            LambdaQueryWrapper<CircleLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CircleLike::getUserId, userId)
                   .eq(CircleLike::getPostId, postId);

            CircleLike existingLike = circleLikeMapper.selectOne(wrapper);

            if (existingLike != null) {
                // 取消点赞：物理删除记录（解决软删除+唯一约束冲突）
                circleLikeMapper.physicalDeleteById(existingLike.getId());
                baseMapper.decrementLikeCount(postId);
            } else {
                // 点赞：使用 try-catch 处理并发插入
                CircleLike newLike = new CircleLike();
                newLike.setUserId(userId);
                newLike.setPostId(postId);
                try {
                    circleLikeMapper.insert(newLike);
                    baseMapper.incrementLikeCount(postId);
                } catch (DuplicateKeyException e) {
                    // 并发情况下另一个请求已经插入了，查询当前状态
                    CircleLike concurrentLike = circleLikeMapper.selectOne(wrapper);
                    if (concurrentLike != null) {
                        // 如果已存在，说明另一个请求刚插入，我们执行取消
                        circleLikeMapper.physicalDeleteById(concurrentLike.getId());
                        baseMapper.decrementLikeCount(postId);
                    }
                    // 否则忽略
                }
            }
        }
    }

    /**
     * 检查是否已点赞
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean checkLikeStatus(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<CircleLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleLike::getUserId, userId)
               .eq(CircleLike::getPostId, postId)
               .eq(CircleLike::getIsDeleted, 0);
        return circleLikeMapper.selectCount(wrapper) > 0;
    }

    // ==================== 评论相关方法 ====================

    /**
     * 发表评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(Long postId, String content, Long parentId, Long replyToUserId, Long userId) {
        // 检查动态是否存在
        CirclePost post = this.getById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException(404, "动态不存在");
        }

        // 检查评论权限
        if (post.getAllowComment() != null && post.getAllowComment() == 0) {
            throw new BusinessException(403, "该动态禁止评论");
        }

        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(400, "评论内容不能为空");
        }
        if (content.length() > 500) {
            throw new BusinessException(400, "评论内容不能超过500字符");
        }

        // 如果是回复，检查父评论是否存在
        if (parentId != null) {
            CircleComment parentComment = circleCommentMapper.selectById(parentId);
            if (parentComment == null || parentComment.getIsDeleted() == 1) {
                throw new BusinessException(404, "父评论不存在");
            }
            // 检查父评论是否属于同一动态
            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException(400, "父评论不属于该动态");
            }
        }

        // XSS 防护
        String sanitizedContent = htmlSanitizer.sanitizePlainText(content);

        CircleComment comment = new CircleComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(sanitizedContent);
        comment.setParentId(parentId);
        comment.setReplyToUserId(replyToUserId);
        comment.setLikeCount(0);

        circleCommentMapper.insert(comment);

        // 增加动态评论数
        baseMapper.incrementCommentCount(postId);

        return comment.getId();
    }

    /**
     * 获取动态评论列表（树形结构）
     */
    @Override
    @Transactional(readOnly = true)
    public List<CircleCommentVO> getComments(Long postId, Long currentUserId) {
        // 检查动态是否存在且用户有权限查看
        CirclePost post = this.getById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException(404, "动态不存在");
        }
        if (!canViewPost(post, currentUserId)) {
            throw new BusinessException(403, "无权查看此动态的评论");
        }

        // 查询该动态的所有评论
        LambdaQueryWrapper<CircleComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleComment::getPostId, postId)
               .eq(CircleComment::getIsDeleted, 0)
               .orderByAsc(CircleComment::getCreateTime);
        List<CircleComment> comments = circleCommentMapper.selectList(wrapper);

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取所有评论者用户ID
        List<Long> userIds = comments.stream()
                .map(CircleComment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 添加被回复的用户ID
        List<Long> replyToUserIds = comments.stream()
                .map(CircleComment::getReplyToUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        userIds.addAll(replyToUserIds);

        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        // 转换为VO
        List<CircleCommentVO> commentVOs = comments.stream()
                .map(comment -> convertToCommentVO(comment, userMap))
                .collect(Collectors.toList());

        // 构建树形结构
        Map<Long, CircleCommentVO> voMap = commentVOs.stream()
                .collect(Collectors.toMap(CircleCommentVO::getId, c -> c));

        List<CircleCommentVO> rootComments = new ArrayList<>();
        for (CircleCommentVO vo : commentVOs) {
            if (vo.getParentId() == null) {
                rootComments.add(vo);
            } else {
                CircleCommentVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    parent.getReplies().add(vo);
                }
            }
        }

        return rootComments;
    }

    /**
     * 删除评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        CircleComment comment = circleCommentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BusinessException(404, "评论不存在");
        }

        // 检查权限：作者本人或管理员可删除
        if (!comment.getUserId().equals(userId) && !com.example.edu_project.utils.SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此评论");
        }

        // 查找所有要删除的评论ID（包括子评论）
        List<Long> commentIdsToDelete = new ArrayList<>();
        commentIdsToDelete.add(commentId);
        collectChildCommentIds(commentId, commentIdsToDelete, 1);

        // 批量删除（逻辑删除）
        circleCommentMapper.deleteBatchIds(commentIdsToDelete);

        // 更新动态评论数
        baseMapper.decrementCommentCount(comment.getPostId(), commentIdsToDelete.size());
    }

    private static final int MAX_RECURSION_DEPTH = 100;

    /**
     * 递归收集所有子评论ID
     */
    private void collectChildCommentIds(Long parentId, List<Long> result, int currentDepth) {
        if (currentDepth > MAX_RECURSION_DEPTH) {
            throw new BusinessException(400, "评论层级过深");
        }
        LambdaQueryWrapper<CircleComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleComment::getParentId, parentId)
               .eq(CircleComment::getIsDeleted, 0);
        List<CircleComment> childComments = circleCommentMapper.selectList(wrapper);

        for (CircleComment child : childComments) {
            if (child.getIsDeleted() == 0) {
                result.add(child.getId());
                collectChildCommentIds(child.getId(), result, currentDepth + 1);
            }
        }
    }

    /**
     * 将评论转换为VO
     */
    private CircleCommentVO convertToCommentVO(CircleComment comment, Map<Long, SysUser> userMap) {
        CircleCommentVO vo = new CircleCommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreateTime(comment.getCreateTime());
        vo.setTimeAgo(calculateTimeAgo(comment.getCreateTime()));
        vo.setReplies(new ArrayList<>());

        // 获取评论者信息
        SysUser user = userMap.get(comment.getUserId());
        if (user != null) {
            vo.setUser(convertToUserVO(user));
        }

        // 获取回复目标用户昵称
        if (comment.getReplyToUserId() != null) {
            SysUser replyUser = userMap.get(comment.getReplyToUserId());
            if (replyUser != null) {
                vo.setReplyToNickname(replyUser.getNickname());
            }
        }

        return vo;
    }

    /**
     * 将用户转换为UserVO
     */
    private UserVO convertToUserVO(SysUser user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        userVO.setRole(user.getRole());
        return userVO;
    }

    // ==================== 转发相关方法 ====================

    /**
     * 转发动态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long repostPost(Long originalPostId, String content, Long userId) {
        // 检查原动态是否存在
        CirclePost originalPost = this.getById(originalPostId);
        if (originalPost == null || originalPost.getStatus() != 1) {
            throw new BusinessException(404, "原动态不存在");
        }

        // 检查是否允许转发
        if (originalPost.getAllowRepost() != null && originalPost.getAllowRepost() == 0) {
            throw new BusinessException(403, "该动态禁止转发");
        }

        // 检查是否有权限查看原动态（才能转发）
        if (!canViewPost(originalPost, userId)) {
            throw new BusinessException(403, "无权转发此动态");
        }

        // XSS 防护
        String sanitizedContent = content != null ? htmlSanitizer.sanitizePlainText(content) : null;

        // 创建新动态
        CirclePost newPost = new CirclePost();
        newPost.setUserId(userId);
        newPost.setContent(sanitizedContent != null ? sanitizedContent : "");
        newPost.setContentType(3); // 转发类型
        newPost.setRepostId(originalPostId);
        // 转发时记录被转发者的用户ID
        newPost.setRepostUserId(originalPost.getUserId());
        newPost.setLikeCount(0);
        newPost.setCommentCount(0);
        newPost.setRepostCount(0);
        newPost.setViewCount(0);
        newPost.setIsTop(0);
        newPost.setStatus(1);
        // 转发默认公开可见，但继承原动态的评论/转发限制
        newPost.setVisibility(0); // 默认公开
        newPost.setAllowComment(originalPost.getAllowComment()); // 继承原动态的评论设置
        newPost.setAllowRepost(originalPost.getAllowRepost()); // 继承原动态的转发设置

        this.save(newPost);

        // 在 circle_repost 表记录转发
        CircleRepost repost = new CircleRepost();
        repost.setUserId(userId);
        repost.setOriginalPostId(originalPostId);
        repost.setNewPostId(newPost.getId());
        circleRepostMapper.insert(repost);

        // 增加原动态的转发数
        baseMapper.incrementRepostCount(originalPostId);

        return newPost.getId();
    }

    /**
     * 检查是否已转发
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean checkRepostStatus(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<CircleRepost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleRepost::getUserId, userId)
               .eq(CircleRepost::getOriginalPostId, postId)
               .eq(CircleRepost::getIsDeleted, 0);
        return circleRepostMapper.selectCount(wrapper) > 0;
    }

    // ==================== 搜索相关方法 ====================

    /**
     * 搜索动态
     */
    @Override
    @Transactional(readOnly = true)
    public List<CirclePostVO> searchPosts(String keyword, int page, int pageSize, Long currentUserId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // 限制搜索关键词长度，防止数据库性能问题
        if (keyword.length() > 200) {
            throw new BusinessException(400, "搜索关键词不能超过200字符");
        }

        LambdaQueryWrapper<CirclePost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CirclePost::getStatus, 1) // 只查询正常状态的动态
                // 可见性过滤：公开动态 或 作者本人查看自己的公开动态
                .and(w -> w.eq(CirclePost::getVisibility, 0) // 公开
                        .or() // 或者作者本人查看自己公开的
                        .and(w2 -> w2.eq(CirclePost::getUserId, currentUserId)
                                    .eq(CirclePost::getVisibility, 0))) // 作者本人的公开动态
                .like(CirclePost::getContent, keyword.trim()) // 搜索内容
                .orderByDesc(CirclePost::getCreateTime); // 按时间排序

        // 分页查询
        List<CirclePost> posts = this.page(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                wrapper
        ).getRecords();

        return convertToVOList(posts, currentUserId);
    }
}