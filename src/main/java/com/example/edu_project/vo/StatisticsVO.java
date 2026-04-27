package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 平台统计数据VO
 */
@Data
@Schema(description = "平台统计数据")
public class StatisticsVO {

    @Schema(description = "用户统计数据")
    private UserStats userStats;

    @Schema(description = "文章统计数据")
    private PostStats postStats;

    @Schema(description = "互动统计数据")
    private EngagementStats engagementStats;

    @Schema(description = "校友圈统计数据")
    private CircleStats circleStats;

    @Schema(description = "举报统计数据")
    private ReportStats reportStats;

    @Schema(description = "每日用户增长趋势")
    private List<DailyCount> userGrowthTrend;

    @Schema(description = "每日文章发布趋势")
    private List<DailyCount> postGrowthTrend;

    @Schema(description = "统计时间点")
    private String statsTime;

    @Data
    @Schema(description = "用户统计数据")
    public static class UserStats {
        @Schema(description = "总用户数")
        private Long totalUsers;

        @Schema(description = "今日新增用户数")
        private Long todayNewUsers;

        @Schema(description = "本周新增用户数")
        private Long weekNewUsers;

        @Schema(description = "本月新增用户数")
        private Long monthNewUsers;

        @Schema(description = "活跃用户数（本周有操作）")
        private Long activeUsers;
    }

    @Data
    @Schema(description = "文章统计数据")
    public static class PostStats {
        @Schema(description = "总文章数")
        private Long totalPosts;

        @Schema(description = "今日新增文章数")
        private Long todayNewPosts;

        @Schema(description = "本周新增文章数")
        private Long weekNewPosts;

        @Schema(description = "本月新增文章数")
        private Long monthNewPosts;

        @Schema(description = "总评论数")
        private Long totalComments;

        @Schema(description = "总点赞数")
        private Long totalLikes;

        @Schema(description = "总收藏数")
        private Long totalCollects;
    }

    @Data
    @Schema(description = "互动统计数据")
    public static class EngagementStats {
        @Schema(description = "总关注数")
        private Long totalFollows;

        @Schema(description = "今日新增关注数")
        private Long todayNewFollows;

        @Schema(description = "总通知数")
        private Long totalNotifications;

        @Schema(description = "未读通知数")
        private Long unreadNotifications;
    }

    @Data
    @Schema(description = "校友圈统计数据")
    public static class CircleStats {
        @Schema(description = "总动态数")
        private Long totalPosts;

        @Schema(description = "今日新增动态数")
        private Long todayNewPosts;

        @Schema(description = "总评论数")
        private Long totalComments;

        @Schema(description = "总点赞数")
        private Long totalLikes;

        @Schema(description = "总转发数")
        private Long totalReposts;
    }

    @Data
    @Schema(description = "举报统计数据")
    public static class ReportStats {
        @Schema(description = "待处理举报数")
        private Long pendingReports;

        @Schema(description = "本月处理举报数")
        private Long monthHandledReports;

        @Schema(description = "总举报数")
        private Long totalReports;
    }

    @Data
    @Schema(description = "每日数量统计")
    public static class DailyCount {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "数量")
        private Long count;
    }
}