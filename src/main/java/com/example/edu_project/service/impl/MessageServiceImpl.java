package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogNotification;
import com.example.edu_project.entity.Message;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogNotificationMapper;
import com.example.edu_project.mapper.MessageMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.MessageService;
import com.example.edu_project.utils.HtmlSanitizer;
import com.example.edu_project.utils.SecurityUtils;
import com.example.edu_project.vo.MessageVO;
import com.example.edu_project.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 私信服务实现类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BlogNotificationMapper blogNotificationMapper;

    @Autowired
    private HtmlSanitizer htmlSanitizer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        // 不能给自己发私信
        if (senderId.equals(receiverId)) {
            throw new BusinessException(400, "不能给自己发私信");
        }

        // 校验接收者是否存在
        SysUser receiver = sysUserMapper.selectById(receiverId);
        if (receiver == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 创建私信
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(htmlSanitizer.sanitizePlainText(content));
        message.setIsRead(0);

        this.save(message);

        // 发送系统通知（可选功能）
        // 查询发送者信息
        SysUser sender = sysUserMapper.selectById(senderId);
        if (sender != null) {
            String notificationType = "MESSAGE";
            String title = "收到新私信";
            String notificationContent = sender.getNickname() + " 给你发送了一条私信";
            sendNotificationIfEnabled(notificationType, title, notificationContent, senderId, receiverId, "MESSAGE", message.getId());
        }

        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageVO> getReceivedMessages(Integer pageNum, Integer pageSize, Long userId) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
               .orderByDesc(Message::getCreateTime);

        Page<Message> messagePage = this.page(page, wrapper);

        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        voPage.setRecords(convertToVOList(messagePage.getRecords(), true));

        return voPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageVO> getSentMessages(Integer pageNum, Integer pageSize, Long userId) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, userId)
               .orderByDesc(Message::getCreateTime);

        Page<Message> messagePage = this.page(page, wrapper);

        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        voPage.setRecords(convertToVOList(messagePage.getRecords(), false));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId, Long userId) {
        Message message = this.getById(messageId);
        if (message == null) {
            throw new BusinessException(404, "私信不存在");
        }
        // 接收者才能标记已读
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException(403, "无权操作此私信");
        }
        message.setIsRead(1);
        this.updateById(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long messageId, Long userId) {
        Message message = this.getById(messageId);
        if (message == null) {
            throw new BusinessException(404, "私信不存在");
        }
        // 发送者或接收者都可以删除
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new BusinessException(403, "无权删除此私信");
        }
        // 逻辑删除
        this.removeById(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
               .eq(Message::getIsRead, 0);
        return this.count(wrapper);
    }

    /**
     * 批量转换实体为VO
     * @param messages 私信列表
     * @param isReceived 是否是收到的私信（用于判断是否需要显示receiver信息）
     */
    private List<MessageVO> convertToVOList(List<Message> messages, boolean isReceived) {
        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集所有用户ID
        Set<Long> userIds = new HashSet<>();
        for (Message message : messages) {
            userIds.add(message.getSenderId());
            userIds.add(message.getReceiverId());
        }

        // 批量查询用户信息
        Map<Long, SysUser> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            sysUserMapper.selectBatchIds(userIds)
                    .forEach(user -> userMap.put(user.getId(), user));
        }

        // 转换VO
        return messages.stream().map(message -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(message, vo);
            vo.setTimeAgo(getTimeAgo(message.getCreateTime()));

            // 设置发送者信息
            SysUser sender = userMap.get(message.getSenderId());
            if (sender != null) {
                vo.setSender(convertToUserVO(sender));
            }

            // 设置接收者信息
            SysUser receiver = userMap.get(message.getReceiverId());
            if (receiver != null) {
                vo.setReceiver(convertToUserVO(receiver));
            }

            return vo;
        }).toList();
    }

    /**
     * 转换用户实体为UserVO
     */
    private UserVO convertToUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
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

    /**
     * 发送系统通知（可选功能）
     */
    private void sendNotificationIfEnabled(String type, String title, String content, Long fromUserId, Long toUserId, String targetType, Long targetId) {
        try {
            // 不通知自己
            if (fromUserId != null && fromUserId.equals(toUserId)) {
                return;
            }

            // 校验目标用户是否存在
            if (toUserId != null) {
                SysUser targetUser = sysUserMapper.selectById(toUserId);
                if (targetUser == null) {
                    return;
                }
            }

            // 创建通知记录
            BlogNotification notification = new BlogNotification();
            notification.setType(type);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setFromUserId(fromUserId);
            notification.setToUserId(toUserId);
            notification.setTargetType(targetType);
            notification.setTargetId(targetId);
            notification.setIsRead(0);

            // 保存通知
            blogNotificationMapper.insert(notification);

        } catch (Exception e) {
            // 通知发送失败不影响私信功能
            log.warn("发送通知失败: {}", e.getMessage());
        }
    }
}