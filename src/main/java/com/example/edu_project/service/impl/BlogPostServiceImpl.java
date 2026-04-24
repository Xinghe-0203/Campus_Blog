package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.PostCreateRequest;
import com.example.edu_project.dto.PostQueryRequest;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogPostTag;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogPostTagMapper;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.vo.PostDetailResponse;
import com.example.edu_project.vo.PostListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class BlogPostServiceImpl extends ServiceImpl<BlogPostMapper, BlogPost> implements BlogPostService {

    @Autowired
    private BlogPostTagMapper blogPostTagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public Long createPost(PostCreateRequest request, Long userId) {
        // 创建文章
        BlogPost post = new BlogPost();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory() != null ? request.getCategory() : "默认分类");
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1); // 已发布

        this.save(post);

        // 保存标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            savePostTags(post.getId(), request.getTagIds());
        }

        return post.getId();
    }

    @Override
    @Transactional
    public void updatePost(PostCreateRequest request, Long userId) {
        BlogPost post = this.getById(request.getId());
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权修改此文章");
        }

        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }

        this.updateById(post);

        // 更新标签关联
        LambdaQueryWrapper<BlogPostTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTag::getPostId, post.getId());
        blogPostTagMapper.delete(wrapper);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            savePostTags(post.getId(), request.getTagIds());
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此文章");
        }

        // 删除文章
        this.removeById(postId);

        // 删除标签关联
        LambdaQueryWrapper<BlogPostTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTag::getPostId, postId);
        blogPostTagMapper.delete(wrapper);
    }

    @Override
    public PostDetailResponse getPostDetail(Long postId) {
        BlogPost post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (post.getStatus() != 1) {
            throw new BusinessException(404, "文章不存在");
        }

        // 增加阅读量
        incrementViewCount(postId);

        return convertToDetailResponse(post);
    }

    @Override
    public IPage<PostListResponse> getPostList(PostQueryRequest request) {
        Page<BlogPost> page = new Page<>(request.getPage(), request.getPageSize());

        LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPost::getStatus, 1); // 只查询已发布的文章

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

        // 按创建时间倒序
        wrapper.orderByDesc(BlogPost::getCreateTime);

        IPage<BlogPost> postPage = this.page(page, wrapper);

        // 转换为列表响应
        IPage<PostListResponse> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(postPage.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public void incrementViewCount(Long postId) {
        BlogPost post = this.getById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
            this.updateById(post);
        }
    }

    private void savePostTags(Long postId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            BlogPostTag postTag = new BlogPostTag();
            postTag.setPostId(postId);
            postTag.setTagId(tagId);
            blogPostTagMapper.insert(postTag);
        }
    }

    private PostDetailResponse convertToDetailResponse(BlogPost post) {
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

    private PostListResponse convertToListResponse(BlogPost post) {
        PostListResponse response = new PostListResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setTitle(post.getTitle());
        response.setSummary(post.getSummary());
        response.setCategory(post.getCategory());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setCreateTime(post.getCreateTime());

        // 获取作者昵称和头像
        SysUser user = sysUserMapper.selectById(post.getUserId());
        if (user != null) {
            response.setNickname(user.getNickname());
            response.setAvatar(user.getAvatar());
        }

        // 获取标签名称列表
        List<String> tagNames = getTagsByPostId(post.getId()).stream()
                .map(PostDetailResponse.TagVO::getName)
                .collect(Collectors.toList());
        response.setTags(tagNames);

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
}
