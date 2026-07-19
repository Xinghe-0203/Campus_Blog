package com.example.edu_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.common.result.Result;
import com.example.edu_project.dto.HandleReportRequest;
import com.example.edu_project.dto.ReportRequest;
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
 * 举报控制器
 */
@Tag(name = "举报管理", description = "举报相关接口")
@RestController
@RequestMapping("/report")
@Validated
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 举报内容（需登录）
     */
    @Operation(summary = "举报内容")
    @PostMapping
    public Result<Long> createReport(@Valid @RequestBody ReportRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Long reportId = reportService.createReport(request, userId);
        return Result.success(reportId);
    }

    /**
     * 获取当前用户的举报记录（需登录）
     */
    @Operation(summary = "获取我的举报记录")
    @GetMapping("/my")
    public Result<IPage<ReportVO>> getMyReports(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<ReportVO> reports = reportService.getMyReports(page, pageSize, userId);
        return Result.success(reports);
    }
}