package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogLike;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.event.LikeCreatedEvent;
import com.example.edu_project.mapper.BlogLikeMapper;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.BlogLikeService;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.vo.LikeItemVO;
import com.example.edu_project.vo.LikeResultVO;
import com.example.edu_project.vo.LikeStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 点赞服务实现类
 */
@Slf4j
@Service
public class BlogLikeServiceImpl extends ServiceImpl<BlogLikeMapper, BlogLike> implements BlogLikeService {

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogPostTagMapper blogPostTagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    /**
     * 细粒度锁映射表：key="userId-postId"，value=锁对象
     * 用于解决同一用户对同一文章的点赞/取消点赞操作的并发问题
     * 使用带过期时间的LRU缓存，避免内存泄漏
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
     * 当锁数量超过MAX_LOCKS_SIZE时，触发主动清理移除过期锁
     */
    private Object getLock(String lockKey) {
        // 先清理过期锁，避免内存泄漏
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
    public LikeResultVO toggleLike(Long postId, Long userId) {
        // 检查文章是否存在
        BlogPost post = blogPostService.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }

        LikeResultVO result = new LikeResultVO();

        // 使用细粒度锁：同一用户对同一文章的点赞操作串行执行
        String lockKey = userId + "-" + postId;
        synchronized (getLock(lockKey)) {
            // 检查是否已点赞
            LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogLike::getUserId, userId)
                  .eq(BlogLike::getPostId, postId);
            BlogLike existingLike = this.getOne(wrapper);

            if (existingLike != null) {
                // 取消点赞：逻辑删除记录（解决软删除+唯一约束冲突）
                ((BlogLikeMapper) this.baseMapper).logicalDeleteById(existingLike.getId());
                // 更新文章点赞数-1
                blogPostService.decrementLikeCount(postId);
                result.setAction("unlike");
            } else {
                // 点赞：尝试添加记录，使用 try-catch 处理并发插入
                BlogLike newLike = new BlogLike();
                newLike.setUserId(userId);
                newLike.setPostId(postId);
                try {
                    this.save(newLike);
                    // 更新文章点赞数+1
                    blogPostService.incrementLikeCount(postId);
                    result.setAction("like");
                    // 发布点赞事件，事务提交后异步发送通知
                    eventPublisher.publishEvent(new LikeCreatedEvent(userId, post.getUserId(), postId, post.getTitle()));
                } catch (DuplicateKeyException e) {
                    // 并发情况下另一个请求已经插入了，直接视为取消点赞（再执行一次取消）
                    // 查询当前状态
                    BlogLike concurrentLike = this.getOne(wrapper);
                    if (concurrentLike != null) {
                        // 逻辑删除记录（解决软删除+唯一约束冲突）
                        ((BlogLikeMapper) this.baseMapper).logicalDeleteById(concurrentLike.getId());
                        blogPostService.decrementLikeCount(postId);
                        result.setAction("unlike");
                    } else {
                        // 极少数情况：记录刚被删了，那就当作点赞成功
                        blogPostService.incrementLikeCount(postId);
                        result.setAction("like");
                    }
                }
            }

            // 获取最新点赞数
            BlogPost updatedPost = blogPostService.getById(postId);
            result.setLikeCount(updatedPost != null ? updatedPost.getLikeCount() : 0);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public boolean hasLiked(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getUserId, userId)
              .eq(BlogLike::getPostId, postId);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<LikeItemVO> getMyLikes(Long userId, Integer page, Integer pageSize) {
        Page<BlogLike> likePage = new Page<>(page, pageSize);

        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getUserId, userId)
                .orderByDesc(BlogLike::getCreateTime);

        IPage<BlogLike> likeResult = this.page(likePage, wrapper);

        if (likeResult.getRecords().isEmpty()) {
            return new Page<>(page, pageSize, 0);
        }

        // 获取所有文章ID
        List<Long> postIds = likeResult.getRecords().stream()
                .map(BlogLike::getPostId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询文章
        List<BlogPost> posts = blogPostMapper.selectBatchIds(postIds);
        // 过滤掉未发布的文章
        posts = posts.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .collect(Collectors.toList());
        Map<Long, BlogPost> postMap = posts.stream()
                .collect(Collectors.toMap(BlogPost::getId, p -> p, (a, b) -> a));

        // 获取所有作者ID
        List<Long> authorIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<SysUser> users = sysUserMapper.selectBatchIds(authorIds);
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));

        // 获取所有文章标签
        LambdaQueryWrapper<BlogPostTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(BlogPostTag::getPostId, postIds);
        List<BlogPostTag> postTags = blogPostTagMapper.selectList(tagWrapper);

        // 获取标签详情
        List<Long> tagIds = postTags.stream()
                .map(BlogPostTag::getTagId)
                .distinct()
                .collect(Collectors.toList());
        List<BlogTag> tags = tagIds.isEmpty() ? List.of() : blogTagMapper.selectBatchIds(tagIds);
        Map<Long, String> tagNameMap = tags.stream()
                .collect(Collectors.toMap(BlogTag::getId, BlogTag::getName, (a, b) -> a));

        // 按文章分组标签
        Map<Long, List<String>> postTagsMap = postTags.stream()
                .collect(Collectors.groupingBy(
                        BlogPostTag::getPostId,
                        Collectors.mapping(pt -> tagNameMap.get(pt.getTagId()), Collectors.toList())
                ));

        // 构建返回结果
        IPage<LikeItemVO> resultPage = new Page<>(
                likeResult.getCurrent(),
                likeResult.getSize(),
                likeResult.getTotal()
        );

        List<LikeItemVO> items = likeResult.getRecords().stream()
                .map(like -> {
                    LikeItemVO item = new LikeItemVO();
                    item.setLikeId(like.getId());
                    item.setPostId(like.getPostId());
                    item.setLikeTime(like.getCreateTime());

                    BlogPost post = postMap.get(like.getPostId());
                    if (post != null) {
                        item.setTitle(post.getTitle());
                        item.setSummary(post.getSummary());
                        item.setCategory(post.getCategory());
                        item.setViewCount(post.getViewCount());
                        item.setLikeCount(post.getLikeCount());
                        item.setCommentCount(post.getCommentCount());
                        item.setCollectCount(post.getCollectCount());

                        SysUser author = userMap.get(post.getUserId());
                        if (author != null) {
                            item.setAuthorId(author.getId());
                            item.setAuthorNickname(author.getNickname());
                            item.setAuthorAvatar(author.getAvatar());
                        }
                    }
                    return item;
                })
                .collect(Collectors.toList());

        resultPage.setRecords(items);
        return resultPage;
    }
}
