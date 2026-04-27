package com.example.edu_project.service;

import com.example.edu_project.vo.StatisticsVO;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取平台完整统计数据
     * @return 平台统计数据
     */
    StatisticsVO getPlatformStatistics();
}
