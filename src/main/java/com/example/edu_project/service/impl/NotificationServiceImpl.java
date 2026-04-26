package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogNotification;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogNotificationMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.NotificationService;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.NotificationVO;
import com.example.edu_project.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 通知服务实现类
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<BlogNotificationMapper, BlogNotification> implements NotificationService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationVO> getNotificationList(Integer pageNum, Integer pageSize, Long userId) {
        Page<BlogNotification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BlogNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogNotification::getToUserId, userId)
               .orderByDesc(BlogNotification::getCreateTime);

        Page<BlogNotification> notificationPage = this.page(page, wrapper);

        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        voPage.setRecords(notificationPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<BlogNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogNotification::getToUserId, userId)
               .eq(BlogNotification::getIsRead, 0);
        return this.count(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        BlogNotification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException(404, "通知不存在");
        }
        // 允许管理员或通知接收者标记为已读
        if (!notification.getToUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权操作此通知");
        }
        notification.setIsRead(1);
        this.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<BlogNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogNotification::getToUserId, userId)
               .eq(BlogNotification::getIsRead, 0);

        BlogNotification update = new BlogNotification();
        update.setIsRead(1);
        this.update(update, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId, Long userId) {
        BlogNotification notification = this.getById(notificationId);
        if (notification == null) {
            throw new BusinessException(404, "通知不存在");
        }
        // 允许管理员删除任意通知，或通知所有者删除自己的通知
        if (!notification.getToUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权删除此通知");
        }
        // 逻辑删除
        this.removeById(notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(String type, String title, String content, Long fromUserId, Long toUserId, String targetType, Long targetId) {
        // 不通知自己
        if (fromUserId != null && fromUserId.equals(toUserId)) {
            return;
        }

        // 校验目标用户是否存在
        if (toUserId != null) {
            SysUser targetUser = sysUserMapper.selectById(toUserId);
            if (targetUser == null) {
                return; // 用户不存在，不发送通知
            }
        }

        BlogNotification notification = new BlogNotification();
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setFromUserId(fromUserId);
        notification.setToUserId(toUserId);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setIsRead(0);

        this.save(notification);
    }

    /**
     * 转换实体为VO
     * 注意：每次单独查询fromUser存在N+1问题，但计划书已有此问题暂不优化
     */
    private NotificationVO convertToVO(BlogNotification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);

        // 查询发送者信息
        if (notification.getFromUserId() != null) {
            SysUser fromUser = sysUserMapper.selectById(notification.getFromUserId());
            if (fromUser != null) {
                UserVO userVO = new UserVO();
                userVO.setId(fromUser.getId());
                userVO.setUsername(fromUser.getUsername());
                userVO.setNickname(fromUser.getNickname());
                userVO.setAvatar(fromUser.getAvatar());
                userVO.setRole(fromUser.getRole());
                userVO.setStatus(fromUser.getStatus());
                vo.setFromUser(userVO);
            }
        }

        // 计算timeAgo
        vo.setTimeAgo(getTimeAgo(notification.getCreateTime()));

        return vo;
    }

    /**
     * 计算相对时间描述
     */
    private String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        long seconds = Duration.between(dateTime, now).getSeconds();

        if (seconds < 60) {
            return "刚刚";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "分钟前";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "小时前";
        }
        long days = hours / 24;
        if (days < 30) {
            return days + "天前";
        }
        long months = days / 30;
        if (months < 12) {
            return months + "个月前";
        }
        long years = days / 365;
        return years + "年前";
    }
}
