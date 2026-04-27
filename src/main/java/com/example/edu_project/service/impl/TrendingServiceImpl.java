package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.entity.BlogTrending;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.mapper.BlogTrendingMapper;
import com.example.edu_project.config.CaffeineCacheConfig;
import com.example.edu_project.service.TrendingService;
import com.example.edu_project.vo.HotPostVO;
import com.example.edu_project.vo.HotTagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 趋势/热门内容服务实现类
 */
@Service
public class TrendingServiceImpl extends ServiceImpl<BlogTrendingMapper, BlogTrending> implements TrendingService {

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private BlogPostTagMapper blogPostTagMapper;

    /**
     * 热度计算公式：score = view*1 + like*5 + comment*10
     */
    private static final int VIEW_WEIGHT = 1;
    private static final int LIKE_WEIGHT = 5;
    private static final int COMMENT_WEIGHT = 10;

    /**
     * 热门文章默认获取最近7天的数据
     */
    private static final int TRENDING_DAYS = 7;

    @Override
    @Transactional(readOnly = true)
    public IPage<HotPostVO> getHotPosts(int pageNum, int pageSize) {
        Page<BlogTrending> page = new Page<>(pageNum, pageSize);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateStart = now.minusDays(TRENDING_DAYS);

        IPage<BlogTrending> trendingPage = baseMapper.selectHotPosts(page, dateStart, now);

        if (trendingPage.getRecords().isEmpty()) {
            Page<HotPostVO> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        // 获取文章信息
        List<Long> postIds = trendingPage.getRecords().stream()
                .map(BlogTrending::getPostId)
                .collect(Collectors.toList());

        Map<Long, BlogPost> postMap = blogPostMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(BlogPost::getId, p -> p));

        // 构建响应数据
        List<HotPostVO> result = new ArrayList<>();
        for (BlogTrending trending : trendingPage.getRecords()) {
            BlogPost post = postMap.get(trending.getPostId());
            if (post != null && post.getStatus() == 1 && post.getIsDeleted() == 0) { // 只返回已发布的文章且未被删除
                HotPostVO vo = new HotPostVO();
                vo.setId(post.getId());
                vo.setTitle(post.getTitle());
                vo.setSummary(post.getSummary());
                vo.setCategory(post.getCategory());
                vo.setViewCount(trending.getViewCount());
                vo.setLikeCount(trending.getLikeCount());
                vo.setCommentCount(trending.getCommentCount());
                vo.setScore(trending.getScore());
                vo.setCreateTime(post.getCreateTime());
                result.add(vo);
            }
        }

        // 返回分页结果
        Page<HotPostVO> resultPage = new Page<>(trendingPage.getCurrent(), trendingPage.getSize(), trendingPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CaffeineCacheConfig.HOT_TAGS_CACHE, key = "'hotTags'")
    public IPage<HotTagVO> getHotTags() {
        // 只查询有限的标签（避免全量加载导致OOM），按ID倒序取较新的标签
        LambdaQueryWrapper<BlogTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(BlogTag::getIsDeleted, 0)
                  .orderByDesc(BlogTag::getId)
                  .last("LIMIT 100"); // 最多处理100个标签
        List<BlogTag> limitedTags = blogTagMapper.selectList(tagWrapper);

        if (limitedTags.isEmpty()) {
            Page<HotTagVO> emptyPage = new Page<>(1, 20, 0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        // 收集这批标签的ID
        List<Long> tagIds = limitedTags.stream()
                .map(BlogTag::getId)
                .collect(Collectors.toList());

        // 批量统计每个标签关联的文章数量（只统计我们关心的标签）
        LambdaQueryWrapper<BlogPostTag> postTagWrapper = new LambdaQueryWrapper<>();
        postTagWrapper.in(BlogPostTag::getTagId, tagIds);
        List<BlogPostTag> postTags = blogPostTagMapper.selectList(postTagWrapper);

        Map<Long, Long> tagCountMap = postTags.stream()
                .collect(Collectors.groupingBy(BlogPostTag::getTagId, Collectors.counting()));

        // 转换为响应数据，按文章数量降序
        List<HotTagVO> result = limitedTags.stream()
                .map(tag -> {
                    HotTagVO vo = new HotTagVO();
                    vo.setId(tag.getId());
                    vo.setName(tag.getName());
                    vo.setPostCount(tagCountMap.getOrDefault(tag.getId(), 0L));
                    return vo;
                })
                .sorted((a, b) -> Long.compare(b.getPostCount(), a.getPostCount()))
                .collect(Collectors.toList());

        // 取前20个
        List<HotTagVO> top20 = result.size() > 20 ? result.subList(0, 20) : result;

        // 返回分页结果
        Page<HotTagVO> resultPage = new Page<>(1, 20, top20.size());
        resultPage.setRecords(top20);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePostTrending(Long postId) {
        BlogPost post = blogPostMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return;
        }

        // 计算热度评分
        int score = post.getViewCount() * VIEW_WEIGHT
                + post.getLikeCount() * LIKE_WEIGHT
                + post.getCommentCount() * COMMENT_WEIGHT;

        // 查询是否已存在今天的趋势记录
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        List<BlogTrending> existingList = baseMapper.selectByPostIdAndDate(postId, todayStart, todayEnd);

        BlogTrending trending = new BlogTrending();
        trending.setPostId(postId);
        trending.setScore((double)score);
        trending.setViewCount(post.getViewCount());
        trending.setLikeCount(post.getLikeCount());
        trending.setCommentCount(post.getCommentCount());
        trending.setStatDate(todayStart.toLocalDate());

        if (existingList.isEmpty()) {
            // 新增记录
            baseMapper.insert(trending);
        } else {
            // 更新已有记录
            trending.setId(existingList.get(0).getId());
            baseMapper.updateById(trending);
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    @Transactional(rollbackFor = Exception.class)
    public void scheduledUpdateAllTrending() {
        // 分页查询未删除的文章，避免一次性加载所有文章导致OOM
        int pageSize = 1000;
        int pageNum = 1;

        while (true) {
            Page<BlogPost> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogPost::getIsDeleted, 0);
            wrapper.eq(BlogPost::getStatus, 1); // 只统计已发布的文章

            Page<BlogPost> postPage = blogPostMapper.selectPage(page, wrapper);

            if (postPage.getRecords().isEmpty()) {
                break;
            }

            for (BlogPost post : postPage.getRecords()) {
                updatePostTrending(post.getId());
            }

            if (!postPage.hasNext()) {
                break;
            }
            pageNum++;
        }
    }
}