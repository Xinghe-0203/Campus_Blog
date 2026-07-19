package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateWrapper;
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
import com.example.edu_project.utils.TimeUtils;
import com.example.edu_project.utils.UserConverter;
import com.example.edu_project.vo.ConversationVO;
import com.example.edu_project.vo.MessageVO;
import com.example.edu_project.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 私信服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

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
            vo.setTimeAgo(TimeUtils.getTimeAgo(message.getCreateTime()));

            // 设置发送者信息
            SysUser sender = userMap.get(message.getSenderId());
            if (sender != null) {
                vo.setSender(UserConverter.toUserVO(sender));
            }

            // 设置接收者信息
            SysUser receiver = userMap.get(message.getReceiverId());
            if (receiver != null) {
                vo.setReceiver(UserConverter.toUserVO(receiver));
            }

            return vo;
        }).toList();
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
            blogNotificationMapper.insert(notification);
        } catch (Exception e) {
            log.warn("发送系统通知失败: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationVO> getConversations(Long userId) {
        // 查找与该用户相关的所有消息（发送或接收），按时间倒序
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId).or().eq(Message::getReceiverId, userId))
               .orderByDesc(Message::getCreateTime);

        List<Message> allMessages = this.list(wrapper);

        // 按对方用户ID分组，每组取最新一条消息
        Map<Long, Message> latestMessageMap = new LinkedHashMap<>();
        for (Message msg : allMessages) {
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            if (otherUserId != null && !latestMessageMap.containsKey(otherUserId)) {
                latestMessageMap.put(otherUserId, msg);
            }
        }

        // 收集所有对方用户ID
        Set<Long> otherUserIds = latestMessageMap.keySet();
        Map<Long, SysUser> userMap = new HashMap<>();
        if (!otherUserIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(otherUserIds);
            for (SysUser u : users) {
                userMap.put(u.getId(), u);
            }
        }

        // 批量获取未读数和总会话消息数（2个查询替代2N个查询）
        Map<Long, Integer> unreadCountMap = new HashMap<>();
        Map<Long, Integer> totalCountMap = new HashMap<>();

        if (!otherUserIds.isEmpty()) {
            // 批量查询未读数
            LambdaQueryWrapper<Message> unreadWrapper = new LambdaQueryWrapper<>();
            unreadWrapper.eq(Message::getReceiverId, userId)
                         .eq(Message::getIsRead, 0)
                         .in(Message::getSenderId, otherUserIds);
            List<Message> unreadMessages = this.list(unreadWrapper);
            for (Message msg : unreadMessages) {
                Long senderId = msg.getSenderId();
                unreadCountMap.merge(senderId, 1, Integer::sum);
            }

            // 批量查询总会话消息数
            LambdaQueryWrapper<Message> totalWrapper = new LambdaQueryWrapper<>();
            totalWrapper.and(w -> w.and(a -> a.eq(Message::getSenderId, userId).in(Message::getReceiverId, otherUserIds))
                                    .or(a -> a.eq(Message::getReceiverId, userId).in(Message::getSenderId, otherUserIds)));
            List<Message> allConversationMessages = this.list(totalWrapper);
            for (Message msg : allConversationMessages) {
                Long otherId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
                totalCountMap.merge(otherId, 1, Integer::sum);
            }
        }

        // 统计每个会话的未读数和消息总数
        List<ConversationVO> result = new ArrayList<>();
        for (Map.Entry<Long, Message> entry : latestMessageMap.entrySet()) {
            Long otherId = entry.getKey();
            Message lastMsg = entry.getValue();

            ConversationVO vo = new ConversationVO();
            vo.setConversationId(otherId);
            vo.setLastMessage(lastMsg.getContent());
            vo.setLastMessageTime(lastMsg.getCreateTime());
            vo.setTimeAgo(TimeUtils.getTimeAgo(lastMsg.getCreateTime()));
            vo.setUnreadCount(unreadCountMap.getOrDefault(otherId, 0));
            vo.setMessageCount(totalCountMap.getOrDefault(otherId, 0));

            SysUser otherUser = userMap.get(otherId);
            if (otherUser != null) {
                UserVO userVO = UserConverter.toUserVO(otherUser);
                vo.setUser(userVO);
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<MessageVO> getConversationMessages(Long userId, Long otherUserId, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId).eq(Message::getReceiverId, otherUserId)
                          .or().eq(Message::getSenderId, otherUserId).eq(Message::getReceiverId, userId))
               .orderByDesc(Message::getCreateTime);

        Page<Message> messagePage = this.page(page, wrapper);
        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        voPage.setRecords(convertToVOList(messagePage.getRecords(), true));

        // 将与该用户的未读消息标记为已读
        LambdaQueryWrapper<Message> unreadWrapper = new LambdaQueryWrapper<>();
        unreadWrapper.eq(Message::getSenderId, otherUserId)
                     .eq(Message::getReceiverId, userId)
                     .eq(Message::getIsRead, 0);
        List<Message> unreadList = this.list(unreadWrapper);
        if (!unreadList.isEmpty()) {
            LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Message::getId, unreadList.stream().map(Message::getId).collect(Collectors.toList()));
            updateWrapper.set(Message::getIsRead, 1);
            this.update(updateWrapper);
        }

        return voPage;
    }
}
