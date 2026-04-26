package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.dao.DuplicateKeyException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogCollect;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogCollectMapper;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.BlogCollectService;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.vo.CollectItemVO;
import com.example.edu_project.vo.CollectResultVO;
import com.example.edu_project.vo.CollectStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Service
public class BlogCollectServiceImpl extends ServiceImpl<BlogCollectMapper, BlogCollect> implements BlogCollectService {

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogPostTagMapper blogPostTagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private BlogPostService blogPostService;

    /**
     * 细粒度锁映射表：key="userId-postId"，value=锁对象
     * 用于解决同一用户对同一文章的收藏/取消收藏操作的并发问题
     * 使用带过期时间的LRU缓存，避免内存泄漏
     */
    private static final int MAX_LOCKS_SIZE = 10000;
    private static final long LOCK_EXPIRE_MS = 300000; // 5分钟
    private final ConcurrentMap<String, LockEntry> collectLocks = new ConcurrentHashMap<>();

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
    private Object getCollectLock(String lockKey) {
        // 先清理过期锁，避免内存泄漏
        if (collectLocks.size() >= MAX_LOCKS_SIZE) {
            collectLocks.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }

        LockEntry entry = collectLocks.compute(lockKey, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new LockEntry(new Object());
            }
            return existing;
        });
        return entry.lock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CollectResultVO toggleCollect(Long postId, Long userId) {
        // 检查文章是否存在
        BlogPost post = blogPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }

        CollectResultVO result = new CollectResultVO();

        // 使用细粒度锁：同一用户对同一文章的收藏操作串行执行
        String lockKey = userId + "-" + postId;
        synchronized (getCollectLock(lockKey)) {
            // 检查是否已收藏
            LambdaQueryWrapper<BlogCollect> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogCollect::getUserId, userId)
                  .eq(BlogCollect::getPostId, postId);
            BlogCollect existingCollect = this.getOne(wrapper);

            if (existingCollect != null) {
                // 取消收藏：逻辑删除记录（解决软删除+唯一约束冲突）
                ((BlogCollectMapper) this.baseMapper).logicalDeleteById(existingCollect.getId());
                blogPostService.decrementCollectCount(postId);
                result.setAction("uncollect");
            } else {
                // 收藏：尝试添加记录，使用 try-catch 处理并发插入
                BlogCollect newCollect = new BlogCollect();
                newCollect.setUserId(userId);
                newCollect.setPostId(postId);
                try {
                    this.save(newCollect);
                    blogPostService.incrementCollectCount(postId);
                    result.setAction("collect");
                } catch (DuplicateKeyException e) {
                    // 并发情况下另一个请求已经插入了，直接视为取消收藏（再执行一次取消）
                    // 查询当前状态
                    BlogCollect concurrentCollect = this.getOne(wrapper);
                    if (concurrentCollect != null) {
                        // 逻辑删除记录（解决软删除+唯一约束冲突）
                        ((BlogCollectMapper) this.baseMapper).logicalDeleteById(concurrentCollect.getId());
                        blogPostService.decrementCollectCount(postId);
                        result.setAction("uncollect");
                    } else {
                        // 极少数情况：记录刚被删了，那就当作收藏成功
                        blogPostService.incrementCollectCount(postId);
                        result.setAction("collect");
                    }
                }
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CollectStatusVO checkCollectStatus(Long postId, Long userId) {
        CollectStatusVO status = new CollectStatusVO();
        if (userId == null) {
            status.setCollected(false);
            return status;
        }

        LambdaQueryWrapper<BlogCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogCollect::getUserId, userId)
              .eq(BlogCollect::getPostId, postId);
        status.setCollected(this.count(wrapper) > 0);
        return status;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<CollectItemVO> getMyCollections(Long userId, Integer page, Integer pageSize) {
        // 分页查询收藏记录
        Page<BlogCollect> collectPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<BlogCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogCollect::getUserId, userId)
              .orderByDesc(BlogCollect::getCreateTime);
        IPage<BlogCollect> collectResult = this.page(collectPage, wrapper);

        // 如果没有收藏记录
        if (collectResult.getRecords().isEmpty()) {
            return new Page<>(page, pageSize, 0);
        }

        // 获取所有相关文章ID
        List<Long> postIds = collectResult.getRecords().stream()
                .map(BlogCollect::getPostId)
                .collect(Collectors.toList());

        // 批量查询文章
        List<BlogPost> posts = blogPostMapper.selectBatchIds(postIds);
        // 过滤掉未发布的文章（status != 1 表示草稿或已下架）
        posts = posts.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .collect(Collectors.toList());
        Map<Long, BlogPost> postMap = posts.stream()
                .collect(Collectors.toMap(BlogPost::getId, p -> p, (a, b) -> a));

        // 获取所有作者ID
        List<Long> userIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
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
        IPage<CollectItemVO> resultPage = new Page<>(
                collectResult.getCurrent(),
                collectResult.getSize(),
                collectResult.getTotal()
        );

        List<CollectItemVO> items = collectResult.getRecords().stream()
                .map(collect -> {
                    CollectItemVO item = new CollectItemVO();
                    item.setCollectId(collect.getId());
                    item.setCollectTime(collect.getCreateTime());

                    BlogPost post = postMap.get(collect.getPostId());
                    if (post != null) {
                        item.setPostId(post.getId());
                        item.setTitle(post.getTitle());
                        item.setSummary(post.getSummary());
                        item.setCategory(post.getCategory());
                        item.setViewCount(post.getViewCount());
                        item.setLikeCount(post.getLikeCount());
                        item.setCommentCount(post.getCommentCount());
                        item.setCollectCount(post.getCollectCount());

                        SysUser author = userMap.get(post.getUserId());
                        if (author != null) {
                            item.setAuthorNickname(author.getNickname());
                        }

                        item.setTags(postTagsMap.getOrDefault(post.getId(), List.of()));
                    }
                    return item;
                })
                .collect(Collectors.toList());

        resultPage.setRecords(items);
        return resultPage;
    }
}
