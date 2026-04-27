package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.AdminPostQueryRequest;
import com.example.edu_project.dto.PostAdvancedSearchRequest;
import com.example.edu_project.dto.PostCreateRequest;
import com.example.edu_project.dto.PostQueryRequest;
import com.example.edu_project.dto.SaveDraftRequest;
import com.example.edu_project.entity.BlogCollect;
import com.example.edu_project.entity.BlogComment;
import com.example.edu_project.entity.BlogDraft;
import com.example.edu_project.entity.BlogLike;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogCollectMapper;
import com.example.edu_project.mapper.BlogCommentMapper;
import com.example.edu_project.mapper.BlogDraftMapper;
import com.example.edu_project.mapper.BlogLikeMapper;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.utils.HtmlSanitizer;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.PostDetailResponse;
import com.example.edu_project.vo.PostListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
public class BlogPostServiceImpl extends ServiceImpl<BlogPostMapper, BlogPost> implements BlogPostService {

    @Autowired
    private BlogPostTagMapper blogPostTagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogCommentMapper blogCommentMapper;

    @Autowired
    private BlogLikeMapper blogLikeMapper;

    @Autowired
    private BlogCollectMapper blogCollectMapper;

    @Autowired
    private HtmlSanitizer htmlSanitizer;

    @Autowired
    private BlogDraftMapper blogDraftMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPost(PostCreateRequest request, Long userId) {
        // 参数校验
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException(400, "文章标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "文章内容不能为空");
        }
        if (request.getTitle().length() > 200) {
            throw new BusinessException(400, "文章标题不能超过200字符");
        }
        if (request.getContent() != null && request.getContent().length() > 50000) {
            throw new BusinessException(400, "文章内容不能超过50000字符");
        }

        // XSS 防护：对用户输入进行 HTML 过滤
        String sanitizedTitle = htmlSanitizer.sanitizeRichText(request.getTitle());
        String sanitizedSummary = htmlSanitizer.sanitizeRichText(request.getSummary());
        String sanitizedContent = htmlSanitizer.sanitizeRichText(request.getContent());

        // 校验标签ID有效性
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            validateTagIds(request.getTagIds());
        }

        // 创建文章
        BlogPost post = new BlogPost();
        post.setUserId(userId);
        post.setTitle(sanitizedTitle);
        post.setSummary(sanitizedSummary);
        post.setContent(sanitizedContent);
        post.setCategory(request.getCategory() != null ? htmlSanitizer.sanitizePlainText(request.getCategory()) : "默认分类");
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCollectCount(0);
        // 普通用户发布设置status=0（待审核），管理员发布设置status=1（直接发布）
        post.setStatus(SecurityUtils.isCurrentUserAdmin() ? 1 : 0);

        this.save(post);

        // 保存标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            savePostTags(post.getId(), request.getTagIds());
        }

        log.info("文章创建成功: postId={}, userId={}, title={}", post.getId(), userId, post.getTitle());
        return post.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(PostCreateRequest request, Long userId) {
        // 参数校验
        if (request.getId() == null) {
            throw new BusinessException(400, "文章ID不能为空");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException(400, "文章标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "文章内容不能为空");
        }
        if (request.getTitle().length() > 200) {
            throw new BusinessException(400, "文章标题不能超过200字符");
        }
        if (request.getContent() != null && request.getContent().length() > 50000) {
            throw new BusinessException(400, "文章内容不能超过50000字符");
        }

        // 校验标签ID有效性
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            validateTagIds(request.getTagIds());
        }

        // XSS 防护：对用户输入进行 HTML 过滤
        String sanitizedTitle = htmlSanitizer.sanitizeRichText(request.getTitle());
        String sanitizedSummary = htmlSanitizer.sanitizeRichText(request.getSummary());
        String sanitizedContent = htmlSanitizer.sanitizeRichText(request.getContent());

        BlogPost post = this.getById(request.getId());
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 检查权限：作者本人或管理员可以修改
        if (!post.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权修改此文章");
        }

        post.setTitle(sanitizedTitle);
        post.setSummary(sanitizedSummary);
        post.setContent(sanitizedContent);
        if (request.getCategory() != null) {
            post.setCategory(htmlSanitizer.sanitizePlainText(request.getCategory()));
        }

        this.updateById(post);

        // 更新标签关联 - 先删后插，保证事务原子性
        LambdaQueryWrapper<BlogPostTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTag::getPostId, post.getId());
        blogPostTagMapper.delete(wrapper);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            savePostTags(post.getId(), request.getTagIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 检查是否已被删除
        if (post.getIsDeleted() != null && post.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        // 检查权限：作者本人或管理员可以删除
        if (!post.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此文章");
        }

        // 删除文章
        this.removeById(postId);

        // 删除标签关联（标签是共享资源，文章删除后关联关系需要清除）
        LambdaQueryWrapper<BlogPostTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTag::getPostId, postId);
        blogPostTagMapper.delete(wrapper);

        log.info("文章删除成功: postId={}, userId={}, title={}", postId, userId, post.getTitle());
        // 保留关联数据（评论、点赞、收藏），
        // 评论/点赞/收藏保留便于数据恢复或审计，关联的文章ID在展示时做判断即可
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 检查是否已被删除
        if (post.getIsDeleted() != null && post.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        // 检查文章是否已发布（status为1）
        if (post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException(404, "文章未发布");
        }

        PostDetailResponse response = new PostDetailResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setTitle(post.getTitle());
        response.setSummary(post.getSummary());
        response.setContent(post.getContent());
        response.setCategory(post.getCategory());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setCollectCount(post.getCollectCount());
        response.setStatus(post.getStatus());
        response.setCreateTime(post.getCreateTime());
        response.setUpdateTime(post.getUpdateTime());

        // 获取作者信息
        SysUser user = sysUserMapper.selectById(post.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setAvatar(user.getAvatar());
        }

        // 获取标签列表
        response.setTags(getTagsByPostId(post.getId()));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<PostListResponse> getPostList(PostQueryRequest request) {
        Page<BlogPost> page = new Page<>(request.getPage(), request.getPageSize());

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getStatus, 1) // 只查询已发布的文章
                .ne(BlogPost::getIsDeleted, 1); // 排除已删除的文章

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(BlogPost::getTitle, request.getKeyword())
                    .or()
                    .like(BlogPost::getContent, request.getKeyword()));
        }

        // 分类筛选
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            wrapper.eq(BlogPost::getCategory, request.getCategory());
        }

        // 作者筛选
        if (request.getUserId() != null) {
            wrapper.eq(BlogPost::getUserId, request.getUserId());
        }

        // 标签筛选
        if (request.getTagId() != null) {
            // 查询指定标签关联的文章ID列表
            LambdaQueryWrapper<BlogPostTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(BlogPostTag::getTagId, request.getTagId());
            List<BlogPostTag> taggedPosts = blogPostTagMapper.selectList(tagWrapper);
            if (taggedPosts.isEmpty()) {
                // 没有文章匹配该标签，返回空结果
                return new Page<>(request.getPage(), request.getPageSize(), 0);
            }
            List<Long> taggedPostIds = taggedPosts.stream()
                    .map(BlogPostTag::getPostId)
                    .collect(Collectors.toList());
            wrapper.in(BlogPost::getId, taggedPostIds);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(BlogPost::getCreateTime);

        IPage<BlogPost> postPage = this.page(page, wrapper);

        // 批量获取用户信息和标签信息，避免 N+1 查询
        List<BlogPost> posts = postPage.getRecords();
        if (posts.isEmpty()) {
            return new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        }

        // 收集所有需要的用户ID
        List<Long> userIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 收集所有文章ID
        List<Long> postIds = posts.stream()
                .map(BlogPost::getId)
                .collect(Collectors.toList());

        // 批量查询用户信息
        Map<Long, SysUser> userMap = sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));

        // 批量查询标签信息
        Map<Long, List<PostDetailResponse.TagVO>> postTagsMap = getTagsMapByPostIds(postIds);

        // 转换为列表响应
        IPage<PostListResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(posts.stream()
                .map(post -> convertToListResponse(post, userMap.get(post.getUserId()), postTagsMap.get(post.getId())))
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long postId) {
        // 直接执行 SQL 实现原子增加
        baseMapper.incrementViewCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementLikeCount(Long postId) {
        baseMapper.incrementLikeCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementLikeCount(Long postId) {
        baseMapper.decrementLikeCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementCommentCount(Long postId) {
        baseMapper.incrementCommentCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementCommentCount(Long postId, int count) {
        baseMapper.decrementCommentCount(postId, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementCollectCount(Long postId) {
        baseMapper.incrementCollectCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementCollectCount(Long postId) {
        baseMapper.decrementCollectCount(postId);
    }

    private void savePostTags(Long postId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        blogPostTagMapper.batchInsertPostTags(postId, tagIds);
    }

    private void validateTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<BlogTag> existingTags = blogTagMapper.selectBatchIds(tagIds);
        if (existingTags.size() != tagIds.size()) {
            throw new BusinessException(400, "部分标签ID不存在");
        }
    }

    private Map<Long, List<PostDetailResponse.TagVO>> getTagsMapByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询所有相关的文章-标签关联
        LambdaQueryWrapper<BlogPostTag> postTagWrapper = new LambdaQueryWrapper<>();
        postTagWrapper.in(BlogPostTag::getPostId, postIds);
        List<BlogPostTag> postTags = blogPostTagMapper.selectList(postTagWrapper);

        if (postTags == null || postTags.isEmpty()) {
            return Collections.emptyMap();
        }

        // 收集所有标签ID
        List<Long> tagIds = postTags.stream()
                .map(BlogPostTag::getTagId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询标签信息
        List<BlogTag> tags = blogTagMapper.selectBatchIds(tagIds);
        Map<Long, String> tagNameMap = tags.stream()
                .collect(Collectors.toMap(BlogTag::getId, BlogTag::getName, (a, b) -> a));

        // 按文章ID分组
        return postTags.stream()
                .collect(Collectors.groupingBy(
                        BlogPostTag::getPostId,
                        Collectors.mapping(tag -> {
                            PostDetailResponse.TagVO tagVO = new PostDetailResponse.TagVO();
                            tagVO.setId(tag.getTagId());
                            tagVO.setName(tagNameMap.get(tag.getTagId()));
                            return tagVO;
                        }, Collectors.toList())
                ));
    }

    private PostListResponse convertToListResponse(BlogPost post, SysUser user, List<PostDetailResponse.TagVO> tags) {
        PostListResponse response = new PostListResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setTitle(post.getTitle());
        response.setSummary(post.getSummary());
        response.setCategory(post.getCategory());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setCollectCount(post.getCollectCount());
        response.setCreateTime(post.getCreateTime());

        // 使用预获取的作者信息
        if (user != null) {
            response.setNickname(user.getNickname());
            response.setAvatar(user.getAvatar());
        }

        // 使用预获取的标签信息
        if (tags != null) {
            response.setTags(tags.stream()
                    .map(PostDetailResponse.TagVO::getName)
                    .collect(Collectors.toList()));
        } else {
            response.setTags(Collections.emptyList());
        }

        return response;
    }

    private List<PostDetailResponse.TagVO> getTagsByPostId(Long postId) {
        LambdaQueryWrapper<BlogPostTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTag::getPostId, postId);
        List<BlogPostTag> postTags = blogPostTagMapper.selectList(wrapper);

        if (postTags.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> tagIds = postTags.stream()
                .map(BlogPostTag::getTagId)
                .collect(Collectors.toList());

        List<BlogTag> tags = blogTagMapper.selectBatchIds(tagIds);

        return tags.stream()
                .map(tag -> {
                    PostDetailResponse.TagVO tagVO = new PostDetailResponse.TagVO();
                    tagVO.setId(tag.getId());
                    tagVO.setName(tag.getName());
                    return tagVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(Long userId, SaveDraftRequest request) {
        // XSS 防护
        String sanitizedTitle = request.getTitle() != null ? htmlSanitizer.sanitizeRichText(request.getTitle()) : null;
        String sanitizedSummary = request.getSummary() != null ? htmlSanitizer.sanitizeRichText(request.getSummary()) : null;
        String sanitizedContent = request.getContent() != null ? htmlSanitizer.sanitizeRichText(request.getContent()) : null;
        String sanitizedCategory = request.getCategory() != null ? htmlSanitizer.sanitizePlainText(request.getCategory()) : null;

        // 校验标签ID有效性
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            validateTagIds(request.getTagIds());
        }

        // 将 tagIds 列表转换为逗号分隔字符串
        String tagIdsStr = null;
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            tagIdsStr = request.getTagIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        // 如果指定了 draftId，则更新指定草稿
        if (request.getDraftId() != null) {
            BlogDraft targetDraft = blogDraftMapper.selectById(request.getDraftId());
            if (targetDraft == null) {
                throw new BusinessException(404, "草稿不存在");
            }
            if (!targetDraft.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权修改此草稿");
            }
            targetDraft.setTitle(sanitizedTitle);
            targetDraft.setContent(sanitizedContent);
            targetDraft.setSummary(sanitizedSummary);
            targetDraft.setCategory(sanitizedCategory);
            targetDraft.setTagIds(tagIdsStr);
            targetDraft.setPostId(request.getPostId());
            blogDraftMapper.updateById(targetDraft);
            return targetDraft.getId();
        }

        // 查询用户的最新草稿（按 update_time 倒序）
        LambdaQueryWrapper<BlogDraft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogDraft::getUserId, userId)
                .orderByDesc(BlogDraft::getUpdateTime)
                .last("LIMIT 1");
        BlogDraft existingDraft = blogDraftMapper.selectOne(wrapper);

        if (existingDraft != null) {
            // 更新已有草稿
            existingDraft.setTitle(sanitizedTitle);
            existingDraft.setContent(sanitizedContent);
            existingDraft.setSummary(sanitizedSummary);
            existingDraft.setCategory(sanitizedCategory);
            existingDraft.setTagIds(tagIdsStr);
            existingDraft.setPostId(request.getPostId());
            blogDraftMapper.updateById(existingDraft);
            return existingDraft.getId();
        } else {
            // 创建新草稿
            BlogDraft draft = new BlogDraft();
            draft.setUserId(userId);
            draft.setTitle(sanitizedTitle);
            draft.setContent(sanitizedContent);
            draft.setSummary(sanitizedSummary);
            draft.setCategory(sanitizedCategory);
            draft.setTagIds(tagIdsStr);
            draft.setPostId(request.getPostId());
            blogDraftMapper.insert(draft);
            return draft.getId();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SaveDraftRequest getLatestDraft(Long userId) {
        LambdaQueryWrapper<BlogDraft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogDraft::getUserId, userId)
                .orderByDesc(BlogDraft::getUpdateTime)
                .last("LIMIT 1");
        BlogDraft draft = blogDraftMapper.selectOne(wrapper);

        if (draft == null) {
            throw new BusinessException(404, "没有草稿");
        }

        return convertToSaveDraftRequest(draft);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDraft(Long draftId, Long userId) {
        BlogDraft draft = blogDraftMapper.selectById(draftId);
        if (draft == null) {
            throw new BusinessException(404, "草稿不存在");
        }
        // 检查权限：只能删除自己的草稿，管理员除外
        if (!draft.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此草稿");
        }

        // 逻辑删除
        blogDraftMapper.deleteById(draftId);
    }

    @Override
    @Transactional(readOnly = true)
    public SaveDraftRequest getDraft(Long draftId, Long userId) {
        BlogDraft draft = blogDraftMapper.selectById(draftId);
        if (draft == null) {
            throw new BusinessException(404, "草稿不存在");
        }
        // 检查权限：只能查看自己的草稿
        if (!draft.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看此草稿");
        }

        return convertToSaveDraftRequest(draft);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<PostListResponse> advancedSearch(PostAdvancedSearchRequest request) {
        Page<BlogPost> page = new Page<>(request.getPage(), request.getPageSize());

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getStatus, 1) // 只查询已发布的文章
                .ne(BlogPost::getIsDeleted, 1); // 排除已删除的文章

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            wrapper.and(w -> w.like(BlogPost::getTitle, request.getKeyword())
                    .or()
                    .like(BlogPost::getContent, request.getKeyword()));
        }

        // 分类筛选
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            wrapper.eq(BlogPost::getCategory, request.getCategory());
        }

        // 作者筛选
        if (request.getUserId() != null) {
            wrapper.eq(BlogPost::getUserId, request.getUserId());
        }

        // 标签筛选
        if (request.getTagId() != null) {
            LambdaQueryWrapper<BlogPostTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(BlogPostTag::getTagId, request.getTagId());
            List<BlogPostTag> taggedPosts = blogPostTagMapper.selectList(tagWrapper);
            if (taggedPosts.isEmpty()) {
                return new Page<>(request.getPage(), request.getPageSize(), 0);
            }
            List<Long> taggedPostIds = taggedPosts.stream()
                    .map(BlogPostTag::getPostId)
                    .collect(Collectors.toList());
            wrapper.in(BlogPost::getId, taggedPostIds);
        }

        // 排序
        String sortBy = request.getSortBy();
        if ("view".equalsIgnoreCase(sortBy)) {
            wrapper.orderByDesc(BlogPost::getViewCount);
        } else if ("like".equalsIgnoreCase(sortBy)) {
            wrapper.orderByDesc(BlogPost::getLikeCount);
        } else {
            // 默认按时间
            wrapper.orderByDesc(BlogPost::getCreateTime);
        }

        IPage<BlogPost> postPage = this.page(page, wrapper);

        List<BlogPost> posts = postPage.getRecords();
        if (posts.isEmpty()) {
            return new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        }

        // 批量获取用户信息和标签信息
        List<Long> userIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> postIds = posts.stream()
                .map(BlogPost::getId)
                .collect(Collectors.toList());

        Map<Long, SysUser> userMap = sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));
        Map<Long, List<PostDetailResponse.TagVO>> postTagsMap = getTagsMapByPostIds(postIds);

        IPage<PostListResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(posts.stream()
                .map(post -> convertToListResponse(post, userMap.get(post.getUserId()), postTagsMap.get(post.getId())))
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 限制关键词长度，防止过长关键词导致性能问题
        if (keyword.trim().length() > 200) {
            keyword = keyword.trim().substring(0, 200);
        }

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getStatus, 1)
                .ne(BlogPost::getIsDeleted, 1)
                .like(BlogPost::getTitle, keyword.trim())
                .select(BlogPost::getTitle)
                .orderByDesc(BlogPost::getViewCount);

        List<BlogPost> posts = this.page(new Page<>(1, 10), wrapper).getRecords();
        return posts.stream()
                .map(BlogPost::getTitle)
                .collect(Collectors.toList());
    }

    private SaveDraftRequest convertToSaveDraftRequest(BlogDraft draft) {
        SaveDraftRequest request = new SaveDraftRequest();
        request.setDraftId(draft.getId());
        request.setTitle(draft.getTitle());
        request.setContent(draft.getContent());
        request.setSummary(draft.getSummary());
        request.setCategory(draft.getCategory());
        request.setPostId(draft.getPostId());

        // 将逗号分隔的 tagIds 转换为列表
        if (draft.getTagIds() != null && !draft.getTagIds().isEmpty()) {
            try {
                String[] tagIdStrs = draft.getTagIds().split(",");
                request.setTagIds(java.util.Arrays.stream(tagIdStrs)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));
            } catch (NumberFormatException e) {
                // 忽略格式错误的 tagIds
            }
        }

        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<PostListResponse> getMyPosts(Long userId, Integer page, Integer pageSize) {
        Page<BlogPost> pageObj = new Page<>(page, pageSize);

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getUserId, userId)
                .eq(BlogPost::getStatus, 1) // 只查询已发布的文章
                .ne(BlogPost::getIsDeleted, 1) // 排除已删除的文章
                .orderByDesc(BlogPost::getCreateTime);

        IPage<BlogPost> postPage = this.page(pageObj, wrapper);

        List<BlogPost> posts = postPage.getRecords();
        if (posts.isEmpty()) {
            return new Page<>(page, pageSize, 0);
        }

        // 收集所有文章ID
        List<Long> postIds = posts.stream()
                .map(BlogPost::getId)
                .collect(Collectors.toList());

        // 批量查询标签信息
        Map<Long, List<PostDetailResponse.TagVO>> postTagsMap = getTagsMapByPostIds(postIds);

        // 作者信息（当前用户）
        SysUser user = sysUserMapper.selectById(userId);

        // 转换为列表响应
        IPage<PostListResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(posts.stream()
                .map(post -> convertToListResponse(post, user, postTagsMap.get(post.getId())))
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<PostDetailResponse> getAdminPostList(AdminPostQueryRequest request) {
        Page<BlogPost> page = new Page<>(request.getPage(), request.getPageSize());

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        // 排除已删除的文章
        wrapper.ne(BlogPost::getIsDeleted, 1);

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            wrapper.and(w -> w.like(BlogPost::getTitle, request.getKeyword().trim())
                    .or()
                    .like(BlogPost::getContent, request.getKeyword().trim()));
        }
        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(BlogPost::getStatus, request.getStatus());
        }
        // 用户ID筛选
        if (request.getUserId() != null) {
            wrapper.eq(BlogPost::getUserId, request.getUserId());
        }
        // 分类筛选
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            wrapper.eq(BlogPost::getCategory, request.getCategory().trim());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(BlogPost::getCreateTime);

        IPage<BlogPost> postPage = this.page(page, wrapper);

        List<BlogPost> posts = postPage.getRecords();
        if (posts.isEmpty()) {
            return new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        }

        // 收集所有需要的用户ID
        List<Long> userIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 收集所有文章ID
        List<Long> postIds = posts.stream()
                .map(BlogPost::getId)
                .collect(Collectors.toList());

        // 批量查询用户信息
        Map<Long, SysUser> userMap = sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));

        // 批量查询标签信息
        Map<Long, List<PostDetailResponse.TagVO>> postTagsMap = getTagsMapByPostIds(postIds);

        // 转换为详情响应
        IPage<PostDetailResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(posts.stream()
                .map(post -> convertToDetailResponse(post, userMap.get(post.getUserId()), postTagsMap.get(post.getId())))
                .collect(Collectors.toList()));

        return result;
    }

    private PostDetailResponse convertToDetailResponse(BlogPost post, SysUser user, List<PostDetailResponse.TagVO> tags) {
        PostDetailResponse response = new PostDetailResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setTitle(post.getTitle());
        response.setSummary(post.getSummary());
        response.setContent(post.getContent());
        response.setCategory(post.getCategory());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setCollectCount(post.getCollectCount());
        response.setStatus(post.getStatus());
        response.setReviewerId(post.getReviewerId());
        response.setReviewTime(post.getReviewTime());
        response.setRejectReason(post.getRejectReason());
        response.setCreateTime(post.getCreateTime());
        response.setUpdateTime(post.getUpdateTime());

        if (user != null) {
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setAvatar(user.getAvatar());
        }

        response.setTags(tags != null ? tags : Collections.emptyList());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDeletePost(Long postId, Long adminId) {
        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (post.getIsDeleted() != null && post.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }

        // 删除文章
        this.removeById(postId);

        // 删除标签关联
        LambdaQueryWrapper<BlogPostTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(BlogPostTag::getPostId, postId);
        blogPostTagMapper.delete(tagWrapper);

        // 删除评论
        LambdaQueryWrapper<BlogComment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(BlogComment::getPostId, postId);
        blogCommentMapper.delete(commentWrapper);

        // 删除点赞
        LambdaQueryWrapper<BlogLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(BlogLike::getPostId, postId);
        blogLikeMapper.delete(likeWrapper);

        // 删除收藏
        LambdaQueryWrapper<BlogCollect> collectWrapper = new LambdaQueryWrapper<>();
        collectWrapper.eq(BlogCollect::getPostId, postId);
        blogCollectMapper.delete(collectWrapper);

        log.info("管理员删除文章: postId={}, adminId={}, title={}", postId, adminId, post.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<PostDetailResponse> getReviewList(String keyword, Integer page, Integer pageSize) {
        Page<BlogPost> pageObj = new Page<>(page, pageSize);

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getStatus, 0) // 待审核
                .ne(BlogPost::getIsDeleted, 1); // 排除已删除

        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(BlogPost::getTitle, keyword.trim())
                    .or()
                    .like(BlogPost::getContent, keyword.trim()));
        }

        // 按创建时间倒序
        wrapper.orderByDesc(BlogPost::getCreateTime);

        IPage<BlogPost> postPage = this.page(pageObj, wrapper);

        List<BlogPost> posts = postPage.getRecords();
        if (posts.isEmpty()) {
            return new Page<>(page, pageSize, 0);
        }

        // 收集所有需要的用户ID
        List<Long> userIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 收集所有文章ID
        List<Long> postIds = posts.stream()
                .map(BlogPost::getId)
                .collect(Collectors.toList());

        // 批量查询用户信息
        Map<Long, SysUser> userMap = sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));

        // 批量查询标签信息
        Map<Long, List<PostDetailResponse.TagVO>> postTagsMap = getTagsMapByPostIds(postIds);

        // 转换为详情响应
        IPage<PostDetailResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(posts.stream()
                .map(post -> convertToDetailResponse(post, userMap.get(post.getUserId()), postTagsMap.get(post.getId())))
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvePost(Long postId, Long reviewerId) {
        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (post.getIsDeleted() != null && post.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (post.getStatus() != 0) {
            throw new BusinessException(400, "该文章不在待审核状态");
        }

        post.setStatus(1); // 已发布
        post.setReviewerId(reviewerId);
        post.setReviewTime(LocalDateTime.now());
        post.setRejectReason(null); // 清除驳回原因
        this.updateById(post);

        log.info("文章审核通过: postId={}, reviewerId={}, title={}", postId, reviewerId, post.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectPost(Long postId, Long reviewerId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException(400, "驳回原因不能为空");
        }
        if (reason.length() > 500) {
            throw new BusinessException(400, "驳回原因不能超过500字符");
        }

        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (post.getIsDeleted() != null && post.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (post.getStatus() != 0) {
            throw new BusinessException(400, "该文章不在待审核状态");
        }

        post.setStatus(2); // 已驳回
        post.setReviewerId(reviewerId);
        post.setReviewTime(LocalDateTime.now());
        post.setRejectReason(htmlSanitizer.sanitizePlainText(reason));
        this.updateById(post);

        log.info("文章审核驳回: postId={}, reviewerId={}, reason={}, title={}", postId, reviewerId, reason, post.getTitle());
    }
}
