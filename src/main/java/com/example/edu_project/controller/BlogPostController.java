package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.PostAdvancedSearchRequest;
import com.example.edu_project.dto.PostCreateRequest;
import com.example.edu_project.dto.PostQueryRequest;
import com.example.edu_project.dto.SaveDraftRequest;
import com.example.edu_project.service.BlogPostService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.PostDetailResponse;
import com.example.edu_project.vo.PostListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文章管理控制器
 */
@Tag(name = "文章管理", description = "文章相关接口")
@RestController
@RequestMapping("/post")
public class BlogPostController {

    @Autowired
    private BlogPostService blogPostService;

    /**
     * 阅读量防刷缓存：key="用户标识-文章ID"，value=上次访问时间
     * 用户标识：已登录用户用userId，未登录用户用IP地址
     * 使用带容量限制的LRU缓存，避免内存泄漏
     */
    private static final int MAX_VIEW_COUNT_CACHE_SIZE = 10000;
    private final Map<String, AtomicLong> viewCountCache = Collections.synchronizedMap(
        new LinkedHashMap<String, AtomicLong>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, AtomicLong> eldest) {
                return size() > MAX_VIEW_COUNT_CACHE_SIZE;
            }
        }
    );

    /**
     * 阅读量增加间隔时间（毫秒），防止同一用户频繁刷新
     */
    private static final long VIEW_COUNT_INTERVAL_MS = 60000; // 1分钟内只计算一次

    /**
     * 获取用户标识：优先用userId，未登录用指纹（IP + User-Agent组合）
     * 注意：IP可能被伪造，但结合User-Agent会增加伪造成本
     */
    private String getUserIdentifier(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId != null) {
            return "user-" + userId;
        }
        // 未登录用户使用指纹：IP + User-Agent 组合
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) {
            ip = "unknown";
        }
        // 不直接信任 X-Forwarded-For，它容易被伪造
        // 只从 request.getRemoteAddr() 获取

        // 使用 User-Agent 作为辅助标识
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            userAgent = "unknown";
        }
        // 对 User-Agent 简短哈希以减少存储长度
        int userAgentHash = userAgent.hashCode();

        return "guest-" + ip + "-" + userAgentHash;
    }

    /**
     * 创建文章
     */
    @Operation(summary = "发布文章")
    @PostMapping
    public Result<Long> createPost(@Valid @RequestBody PostCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
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
                                    @Valid @RequestBody PostCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
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
    public Result<Void> deletePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
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
    public Result<IPage<PostListResponse>> getPostList(@Valid PostQueryRequest request) {
        IPage<PostListResponse> list = blogPostService.getPostList(request);
        return Result.success(list);
    }

    /**
     * 增加阅读量
     */
    @Operation(summary = "增加阅读量")
    @PutMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id, HttpServletRequest request) {
        String userKey = getUserIdentifier(request) + "-" + id;
        long now = System.currentTimeMillis();
        AtomicLong lastViewTime = viewCountCache.computeIfAbsent(userKey, k -> new AtomicLong(0));

        // 使用 CAS 操作解决 TOCTOU 竞态条件
        while (true) {
            long lastTime = lastViewTime.get();
            if (now - lastTime < VIEW_COUNT_INTERVAL_MS) {
                // 距离上次访问不足1分钟，不增加阅读量
                return Result.success(null);
            }
            // 尝试原子更新：如果 lastTime 没变，则更新为 now
            if (lastViewTime.compareAndSet(lastTime, now)) {
                break;
            }
            // 否则说明其他线程已经修改，重新读取
        }

        blogPostService.incrementViewCount(id);
        return Result.success(null);
    }

    /**
     * 保存草稿
     */
    @Operation(summary = "保存草稿")
    @PostMapping("/draft")
    public Result<Long> saveDraft(@Valid @RequestBody SaveDraftRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Long draftId = blogPostService.saveDraft(userId, request);
        return Result.success(draftId);
    }

    /**
     * 获取我的最新草稿
     */
    @Operation(summary = "获取我的最新草稿")
    @GetMapping("/draft/latest")
    public Result<SaveDraftRequest> getLatestDraft() {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        SaveDraftRequest draft = blogPostService.getLatestDraft(userId);
        return Result.success(draft);
    }

    /**
     * 删除草稿
     */
    @Operation(summary = "删除草稿")
    @DeleteMapping("/draft/{draftId}")
    public Result<Void> deleteDraft(@PathVariable Long draftId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        blogPostService.deleteDraft(draftId, userId);
        return Result.success(null);
    }

    /**
     * 获取指定草稿
     */
    @Operation(summary = "获取指定草稿")
    @GetMapping("/draft/{draftId}")
    public Result<SaveDraftRequest> getDraft(@PathVariable Long draftId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        SaveDraftRequest draft = blogPostService.getDraft(draftId, userId);
        return Result.success(draft);
    }

    /**
     * 文章高级搜索（公开接口）
     */
    @Operation(summary = "文章高级搜索")
    @GetMapping("/search/advanced")
    public Result<IPage<PostListResponse>> advancedSearch(@Valid PostAdvancedSearchRequest request) {
        IPage<PostListResponse> result = blogPostService.advancedSearch(request);
        return Result.success(result);
    }

    /**
     * 获取搜索建议（标题自动补全）
     */
    @Operation(summary = "获取搜索建议")
    @GetMapping("/search/suggest")
    public Result<List<String>> getSearchSuggestions(@RequestParam(required = false) String keyword) {
        // 搜索建议是公开功能，不需要登录
        List<String> suggestions = blogPostService.getSearchSuggestions(keyword);
        return Result.success(suggestions);
    }
}
