package com.example.edu_project.controller;

import com.example.edu_project.common.result.Result;
import com.example.edu_project.service.StatisticsService;
import com.example.edu_project.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据统计控制器（公开）
 */
@Tag(name = "数据统计", description = "公开数据统计接口")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取社区统计数据（公开接口）
     */
    @Operation(summary = "获取社区统计数据")
    @GetMapping("/community")
    public Result<StatisticsVO> getCommunityStats() {
        StatisticsVO statistics = statisticsService.getCommunityStats();
        return Result.success(statistics);
    }
}
