package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.edu_project.entity.*;
import com.example.edu_project.mapper.*;
import com.example.edu_project.service.StatisticsService;
import com.example.edu_project.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private BlogCommentMapper blogCommentMapper;

    @Autowired
    private BlogLikeMapper blogLikeMapper;

    @Autowired
    private BlogCollectMapper blogCollectMapper;

    @Autowired
    private BlogFollowMapper blogFollowMapper;

    @Autowired
    private BlogNotificationMapper notificationMapper;

    @Autowired
    private BlogReportMapper reportMapper;

    @Autowired
    private CirclePostMapper circlePostMapper;

    @Autowired
    private CircleCommentMapper circleCommentMapper;

    @Autowired
    private CircleLikeMapper circleLikeMapper;

    @Autowired
    private CircleRepostMapper circleRepostMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public StatisticsVO getPlatformStatistics() {
        StatisticsVO vo = new StatisticsVO();

        // 用户统计
        vo.setUserStats(getUserStats());

        // 文章统计
        vo.setPostStats(getPostStats());

        // 互动统计
        vo.setEngagementStats(getEngagementStats());

        // 校友圈统计
        vo.setCircleStats(getCircleStats());

        // 举报统计
        vo.setReportStats(getReportStats());

        // 趋势数据
        vo.setUserGrowthTrend(getUserGrowthTrend());
        vo.setPostGrowthTrend(getPostGrowthTrend());

        // 统计时间
        vo.setStatsTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return vo;
    }

    private StatisticsVO.UserStats getUserStats() {
        StatisticsVO.UserStats stats = new StatisticsVO.UserStats();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthStart = now.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 总用户数
        stats.setTotalUsers(sysUserMapper.selectCount(null));

        // 今日新增
        LambdaQueryWrapper<SysUser> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(SysUser::getCreateTime, todayStart);
        stats.setTodayNewUsers(sysUserMapper.selectCount(todayWrapper));

        // 本周新增
        LambdaQueryWrapper<SysUser> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.ge(SysUser::getCreateTime, weekStart);
        stats.setWeekNewUsers(sysUserMapper.selectCount(weekWrapper));

        // 本月新增
        LambdaQueryWrapper<SysUser> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.ge(SysUser::getCreateTime, monthStart);
        stats.setMonthNewUsers(sysUserMapper.selectCount(monthWrapper));

        // 活跃用户（本周有操作，这里简化为本周登录的用户或发文章/评论的用户）
        // 由于没有登录日志，我们用本周发文章或评论的用户数近似
        stats.setActiveUsers(getActiveUsersThisWeek());

        return stats;
    }

    private Long getActiveUsersThisWeek() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);

        // 发文章的用户
        LambdaQueryWrapper<BlogPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.ge(BlogPost::getCreateTime, weekStart)
                   .eq(BlogPost::getIsDeleted, 0);
        List<BlogPost> posts = blogPostMapper.selectList(postWrapper);
        List<Long> activeUserIds = posts.stream()
                .map(BlogPost::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 发评论的用户
        LambdaQueryWrapper<BlogComment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.ge(BlogComment::getCreateTime, weekStart)
                      .eq(BlogComment::getIsDeleted, 0);
        List<BlogComment> comments = blogCommentMapper.selectList(commentWrapper);
        activeUserIds.addAll(comments.stream()
                .map(BlogComment::getUserId)
                .distinct()
                .collect(Collectors.toList()));

        // 发校友圈动态的用户
        LambdaQueryWrapper<CirclePost> circleWrapper = new LambdaQueryWrapper<>();
        circleWrapper.ge(CirclePost::getCreateTime, weekStart)
                     .eq(CirclePost::getStatus, 1);
        List<CirclePost> circlePosts = circlePostMapper.selectList(circleWrapper);
        activeUserIds.addAll(circlePosts.stream()
                .map(CirclePost::getUserId)
                .distinct()
                .collect(Collectors.toList()));

        return (long) activeUserIds.stream().distinct().count();
    }

    private StatisticsVO.PostStats getPostStats() {
        StatisticsVO.PostStats stats = new StatisticsVO.PostStats();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthStart = now.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 总文章数
        LambdaQueryWrapper<BlogPost> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(BlogPost::getIsDeleted, 0);
        stats.setTotalPosts(blogPostMapper.selectCount(totalWrapper));

        // 今日新增
        LambdaQueryWrapper<BlogPost> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(BlogPost::getIsDeleted, 0)
                    .ge(BlogPost::getCreateTime, todayStart);
        stats.setTodayNewPosts(blogPostMapper.selectCount(todayWrapper));

        // 本周新增
        LambdaQueryWrapper<BlogPost> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.eq(BlogPost::getIsDeleted, 0)
                   .ge(BlogPost::getCreateTime, weekStart);
        stats.setWeekNewPosts(blogPostMapper.selectCount(weekWrapper));

        // 本月新增
        LambdaQueryWrapper<BlogPost> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(BlogPost::getIsDeleted, 0)
                    .ge(BlogPost::getCreateTime, monthStart);
        stats.setMonthNewPosts(blogPostMapper.selectCount(monthWrapper));

        // 总评论数
        LambdaQueryWrapper<BlogComment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(BlogComment::getIsDeleted, 0);
        stats.setTotalComments(blogCommentMapper.selectCount(commentWrapper));

        // 总点赞数
        LambdaQueryWrapper<BlogLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(BlogLike::getIsDeleted, 0);
        stats.setTotalLikes(blogLikeMapper.selectCount(likeWrapper));

        // 总收藏数
        LambdaQueryWrapper<BlogCollect> collectWrapper = new LambdaQueryWrapper<>();
        collectWrapper.eq(BlogCollect::getIsDeleted, 0);
        stats.setTotalCollects(blogCollectMapper.selectCount(collectWrapper));

        return stats;
    }

    private StatisticsVO.EngagementStats getEngagementStats() {
        StatisticsVO.EngagementStats stats = new StatisticsVO.EngagementStats();

        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

        // 总关注数
        stats.setTotalFollows(blogFollowMapper.selectCount(null));

        // 今日新增关注
        LambdaQueryWrapper<BlogFollow> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(BlogFollow::getCreateTime, todayStart);
        stats.setTodayNewFollows(blogFollowMapper.selectCount(todayWrapper));

        // 总通知数
        stats.setTotalNotifications(notificationMapper.selectCount(null));

        // 未读通知数
        LambdaQueryWrapper<BlogNotification> unreadWrapper = new LambdaQueryWrapper<>();
        unreadWrapper.eq(BlogNotification::getIsRead, 0);
        stats.setUnreadNotifications(notificationMapper.selectCount(unreadWrapper));

        return stats;
    }

    private StatisticsVO.CircleStats getCircleStats() {
        StatisticsVO.CircleStats stats = new StatisticsVO.CircleStats();

        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

        // 总动态数
        LambdaQueryWrapper<CirclePost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(CirclePost::getStatus, 1);
        stats.setTotalPosts((long) circlePostMapper.selectCount(postWrapper));

        // 今日新增动态
        LambdaQueryWrapper<CirclePost> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(CirclePost::getStatus, 1)
                    .ge(CirclePost::getCreateTime, todayStart);
        stats.setTodayNewPosts((long) circlePostMapper.selectCount(todayWrapper));

        // 总评论数
        LambdaQueryWrapper<CircleComment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(CircleComment::getIsDeleted, 0);
        stats.setTotalComments((long) circleCommentMapper.selectCount(commentWrapper));

        // 总点赞数
        LambdaQueryWrapper<CircleLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(CircleLike::getIsDeleted, 0);
        stats.setTotalLikes((long) circleLikeMapper.selectCount(likeWrapper));

        // 总转发数
        LambdaQueryWrapper<CircleRepost> repostWrapper = new LambdaQueryWrapper<>();
        repostWrapper.eq(CircleRepost::getIsDeleted, 0);
        stats.setTotalReposts((long) circleRepostMapper.selectCount(repostWrapper));

        return stats;
    }

    private StatisticsVO.ReportStats getReportStats() {
        StatisticsVO.ReportStats stats = new StatisticsVO.ReportStats();

        LocalDateTime monthStart = LocalDateTime.now().minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 待处理举报
        LambdaQueryWrapper<BlogReport> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(BlogReport::getStatus, 0);
        stats.setPendingReports(reportMapper.selectCount(pendingWrapper));

        // 本月处理举报
        LambdaQueryWrapper<BlogReport> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.ge(BlogReport::getHandleTime, monthStart)
                    .ne(BlogReport::getStatus, 0);
        stats.setMonthHandledReports(reportMapper.selectCount(monthWrapper));

        // 总举报数
        stats.setTotalReports(reportMapper.selectCount(null));

        return stats;
    }

    private List<StatisticsVO.DailyCount> getUserGrowthTrend() {
        List<StatisticsVO.DailyCount> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(SysUser::getCreateTime, dayStart)
                   .lt(SysUser::getCreateTime, dayEnd);

            StatisticsVO.DailyCount dailyCount = new StatisticsVO.DailyCount();
            dailyCount.setDate(date.format(DATE_FORMATTER));
            dailyCount.setCount(sysUserMapper.selectCount(wrapper));
            trend.add(dailyCount);
        }

        return trend;
    }

    private List<StatisticsVO.DailyCount> getPostGrowthTrend() {
        List<StatisticsVO.DailyCount> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogPost::getIsDeleted, 0)
                   .ge(BlogPost::getCreateTime, dayStart)
                   .lt(BlogPost::getCreateTime, dayEnd);

            StatisticsVO.DailyCount dailyCount = new StatisticsVO.DailyCount();
            dailyCount.setDate(date.format(DATE_FORMATTER));
            dailyCount.setCount(blogPostMapper.selectCount(wrapper));
            trend.add(dailyCount);
        }

        return trend;
    }
}
