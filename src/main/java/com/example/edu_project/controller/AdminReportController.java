package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.HandleReportRequest;
import com.example.edu_project.service.ReportService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.ReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员举报管理控制器
 */
@Tag(name = "管理员-举报管理", description = "管理员举报管理接口")
@RestController
@RequestMapping("/admin/reports")
@Validated
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取待处理的举报列表（仅管理员）
     */
    @Operation(summary = "获取待处理举报列表")
    @GetMapping("/pending")
    public Result<IPage<ReportVO>> getPendingReports(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize) {
        // 检查管理员权限
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权限访问此接口");
        }
        IPage<ReportVO> reports = reportService.getPendingReports(page, pageSize);
        return Result.success(reports);
    }

    /**
     * 获取举报详情（仅管理员）
     */
    @Operation(summary = "获取举报详情")
    @GetMapping("/{reportId}")
    public Result<ReportVO> getReportDetail(@PathVariable Long reportId) {
        // 检查管理员权限
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权限访问此接口");
        }
        ReportVO report = reportService.getReportDetail(reportId);
        return Result.success(report);
    }

    /**
     * 处理举报（仅管理员）
     */
    @Operation(summary = "处理举报")
    @PutMapping("/{reportId}")
    public Result<Void> handleReport(
            @PathVariable Long reportId,
            @Valid @RequestBody HandleReportRequest request) {
        // 检查管理员权限
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权限访问此接口");
        }
        Long handlerId = SecurityUtils.getCurrentUserId();
        reportService.handleReport(reportId, request, handlerId);
        return Result.success(null);
    }
}