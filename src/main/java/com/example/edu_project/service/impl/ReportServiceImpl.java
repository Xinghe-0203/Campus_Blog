package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.dto.HandleReportRequest;
import com.example.edu_project.dto.ReportRequest;
import com.example.edu_project.entity.BlogComment;
import com.example.edu_project.entity.BlogPost;
import com.example.edu_project.entity.BlogReport;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogCommentMapper;
import com.example.edu_project.mapper.BlogPostMapper;
import com.example.edu_project.mapper.BlogReportMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.ReportService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.utils.UserConverter;
import com.example.edu_project.vo.ReportVO;
import com.example.edu_project.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 举报服务实现类
 */
@Service
public class ReportServiceImpl extends ServiceImpl<BlogReportMapper, BlogReport> implements ReportService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private BlogCommentMapper blogCommentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReport(ReportRequest request, Long reporterId) {
        // 参数校验
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new BusinessException(400, "举报原因不能为空");
        }
        if (request.getReason().length() > 1000) {
            throw new BusinessException(400, "举报原因不能超过1000字符");
        }

        // 校验 targetType
        String targetType = request.getTargetType();
        if (!isValidTargetType(targetType)) {
            throw new BusinessException(400, "无效的举报目标类型");
        }

        // 校验 targetId
        if (request.getTargetId() == null || request.getTargetId() <= 0) {
            throw new BusinessException(400, "无效的举报目标ID");
        }

        // 获取被举报用户ID
        Long reportedUserId = getReportedUserId(targetType, request.getTargetId());
        if (reportedUserId == null) {
            throw new BusinessException(404, "举报目标不存在");
        }

        // 不能举报自己
        if (reporterId.equals(reportedUserId)) {
            throw new BusinessException(400, "不能举报自己");
        }

        // 检查是否对待处理举报
        LambdaQueryWrapper<BlogReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogReport::getReporterId, reporterId)
               .eq(BlogReport::getTargetType, targetType)
               .eq(BlogReport::getTargetId, request.getTargetId())
               .eq(BlogReport::getStatus, 0); // 待处理状态
        if (this.count(wrapper) > 0) {
            throw new BusinessException(400, "您已提交过待处理举报，请等待处理");
        }

        // 创建举报记录
        BlogReport report = new BlogReport();
        report.setReporterId(reporterId);
        report.setReportedUserId(reportedUserId);
        report.setTargetType(targetType);
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason().trim());
        report.setStatus(0); // 待处理

        this.save(report);
        return report.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<ReportVO> getMyReports(Integer page, Integer pageSize, Long reporterId) {
        Page<BlogReport> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<BlogReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogReport::getReporterId, reporterId)
                .orderByDesc(BlogReport::getCreateTime);

        IPage<BlogReport> reportPage = this.page(pageParam, wrapper);
        return convertToVOPage(reportPage);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<ReportVO> getPendingReports(Integer page, Integer pageSize) {
        // 管理员权限校验
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "需要管理员权限");
        }
        Page<BlogReport> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<BlogReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogReport::getStatus, 0) // 待处理状态
                .orderByAsc(BlogReport::getCreateTime); // 按时间正序，先举报先处理

        IPage<BlogReport> reportPage = this.page(pageParam, wrapper);
        return convertToVOPage(reportPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportVO getReportDetail(Long reportId) {
        // 管理员权限校验
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "需要管理员权限");
        }
        BlogReport report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException(404, "举报记录不存在");
        }
        // 使用单条记录转换（复用batch逻辑以保证一致性）
        return convertSingleToVO(report, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleReport(Long reportId, HandleReportRequest request, Long handlerId) {
        // 管理员权限校验
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "需要管理员权限");
        }
        // 参数校验
        if (request.getStatus() == null) {
            throw new BusinessException(400, "处理状态不能为空");
        }
        if (request.getStatus() != 1 && request.getStatus() != 2) {
            throw new BusinessException(400, "处理状态必须是1（已驳回）或2（已核实）");
        }

        BlogReport report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException(404, "举报记录不存在");
        }
        if (report.getStatus() != 0) {
            throw new BusinessException(400, "该举报已被处理");
        }

        // 更新举报状态
        report.setStatus(request.getStatus());
        report.setHandlerId(handlerId);
        report.setHandlerResult(request.getHandlerResult());
        report.setHandleTime(LocalDateTime.now());

        this.updateById(report);

        // 如果是已核实状态，对被举报内容进行相应处理
        if (request.getStatus() == 2) {
            String targetType = report.getTargetType();
            Long targetId = report.getTargetId();

            switch (targetType) {
                case "post":
                    // 下架文章
                    BlogPost post = blogPostMapper.selectById(targetId);
                    if (post != null) {
                        post.setStatus(0); // 设为隐藏状态
                        blogPostMapper.updateById(post);
                    }
                    break;
                case "comment":
                    // 删除评论（逻辑删除）
                    blogCommentMapper.deleteById(targetId);
                    break;
                case "user":
                    // 封禁用户
                    SysUser user = sysUserMapper.selectById(targetId);
                    if (user != null) {
                        user.setStatus(0); // 设为禁用状态
                        sysUserMapper.updateById(user);
                    }
                    break;
            }
        }
    }

    /**
     * 校验目标类型是否合法
     */
    private boolean isValidTargetType(String targetType) {
        return "post".equals(targetType) || "comment".equals(targetType) || "user".equals(targetType);
    }

    /**
     * 根据目标类型和ID获取被举报用户ID
     */
    private Long getReportedUserId(String targetType, Long targetId) {
        switch (targetType) {
            case "post":
                BlogPost post = blogPostMapper.selectById(targetId);
                return post != null ? post.getUserId() : null;
            case "comment":
                BlogComment comment = blogCommentMapper.selectById(targetId);
                return comment != null ? comment.getUserId() : null;
            case "user":
                SysUser user = sysUserMapper.selectById(targetId);
                return user != null ? user.getId() : null;
            default:
                return null;
        }
    }

    /**
     * 转换单条举报记录为VO（使用批量查询避免N+1问题）
     * @param report 举报记录
     * @param userMap 预加载的用户Map（可选，用于批量转换场景）
     */
    private ReportVO convertSingleToVO(BlogReport report, Map<Long, SysUser> userMap) {
        ReportVO vo = new ReportVO();
        vo.setId(report.getId());
        vo.setTargetType(report.getTargetType());
        vo.setTargetId(report.getTargetId());
        vo.setReason(report.getReason());
        vo.setStatus(report.getStatus());
        vo.setHandlerResult(report.getHandlerResult());
        vo.setHandleTime(report.getHandleTime());
        vo.setCreateTime(report.getCreateTime());

        // 如果没有传入userMap，则使用批量查询（避免N+1）
        if (userMap == null) {
            userMap = new HashMap<>();
            Set<Long> userIds = new HashSet<>();
            userIds.add(report.getReporterId());
            userIds.add(report.getReportedUserId());
            if (report.getHandlerId() != null) {
                userIds.add(report.getHandlerId());
            }
            List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
            for (SysUser user : users) {
                userMap.put(user.getId(), user);
            }
        }

        // 设置举报人信息
        SysUser reporter = userMap.get(report.getReporterId());
        if (reporter != null) {
            vo.setReporter(UserConverter.toUserVO(reporter));
        }

        // 设置被举报用户信息
        SysUser reportedUser = userMap.get(report.getReportedUserId());
        if (reportedUser != null) {
            vo.setReportedUser(UserConverter.toUserVO(reportedUser));
        }

        // 设置处理人信息
        if (report.getHandlerId() != null) {
            vo.setHandlerId(report.getHandlerId());
            SysUser handler = userMap.get(report.getHandlerId());
            if (handler != null) {
                vo.setHandler(UserConverter.toUserVO(handler));
            }
        }

        return vo;
    }

    /**
     * 转换分页结果（优化：批量查询用户信息避免N+1）
     */
    private IPage<ReportVO> convertToVOPage(IPage<BlogReport> reportPage) {
        List<BlogReport> reports = reportPage.getRecords();
        if (reports.isEmpty()) {
            return new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        }

        // 收集所有需要的用户ID（包括handler）
        Map<Long, SysUser> userMap = new HashMap<>();
        for (BlogReport report : reports) {
            userMap.put(report.getReporterId(), null);
            userMap.put(report.getReportedUserId(), null);
            if (report.getHandlerId() != null) {
                userMap.put(report.getHandlerId(), null);
            }
        }

        // 批量查询用户信息（一次查询替代N次）
        if (!userMap.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(userMap.keySet());
            for (SysUser user : users) {
                userMap.put(user.getId(), user);
            }
        }

        // 转换为VO
        Page<ReportVO> result = new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        result.setRecords(reports.stream()
                .map(report -> convertSingleToVO(report, userMap))
                .collect(java.util.stream.Collectors.toList()));

        return result;
    }
}