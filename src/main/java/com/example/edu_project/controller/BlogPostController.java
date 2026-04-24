package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.PostCreateRequest;
import com.example.edu_project.dto.PostQueryRequest;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.vo.PostDetailResponse;
import com.example.edu_project.vo.PostListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章管理控制器
 */
@Tag(name = "文章管理", description = "文章相关接口")
@RestController
@RequestMapping("/post")
@CrossOrigin
public class BlogPostController {

    @Autowired
    private BlogPostService blogPostService;

    /**
     * 创建文章
     */
    @Operation(summary = "发布文章")
    @PostMapping
    public Result<Long> createPost(@RequestBody PostCreateRequest request,
                                   @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            // [安全警告] 当前通过请求头传递用户ID存在身份伪造风险
            // TODO: JWT实现后，从Token中解析用户ID，移除X-User-Id请求头
            userId = 1L;
        }
        Long postId = blogPostService.createPost(request, userId);
        return Result.success(postId);
    }

    /**
     * 更新文章
     */
    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> updatePost(@PathVariable Long id,
                                    @RequestBody PostCreateRequest request,
                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            // [安全警告] 当前通过请求头传递用户ID存在身份伪造风险
            // TODO: JWT实现后，从Token中解析用户ID，移除X-User-Id请求头
            userId = 1L;
        }
        request.setId(id);
        blogPostService.updatePost(request, userId);
        return Result.success(null);
    }

    /**
     * 删除文章
     */
    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id,
                                   @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            // [安全警告] 当前通过请求头传递用户ID存在身份伪造风险
            // TODO: JWT实现后，从Token中解析用户ID，移除X-User-Id请求头
            userId = 1L;
        }
        blogPostService.deletePost(id, userId);
        return Result.success(null);
    }

    /**
     * 获取文章详情
     */
    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<PostDetailResponse> getPostDetail(@PathVariable Long id) {
        PostDetailResponse detail = blogPostService.getPostDetail(id);
        return Result.success(detail);
    }

    /**
     * 获取文章列表（分页）
     */
    @Operation(summary = "获取文章列表")
    @GetMapping("/list")
    public Result<IPage<PostListResponse>> getPostList(PostQueryRequest request) {
        IPage<PostListResponse> list = blogPostService.getPostList(request);
        return Result.success(list);
    }

    /**
     * 增加阅读量
     */
    @Operation(summary = "增加阅读量")
    @PutMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        blogPostService.incrementViewCount(id);
        return Result.success(null);
    }
}
