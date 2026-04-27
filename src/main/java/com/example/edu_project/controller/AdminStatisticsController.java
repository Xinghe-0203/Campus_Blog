package com.example.edu_project.controller;

import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.StatisticsService;
import com.example.edu_project.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员数据统计控制器
 */
@Tag(name = "管理员-数据统计", description = "平台数据统计接口")
@RestController
@RequestMapping("/admin/statistics")
@PreAuthorize("hasRole('admin')")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取平台完整统计数据
     */
    @Operation(summary = "获取平台统计数据")
    @GetMapping
    public Result<StatisticsVO> getPlatformStatistics() {
        StatisticsVO statistics = statisticsService.getPlatformStatistics();
        return Result.success(statistics);
    }
}
