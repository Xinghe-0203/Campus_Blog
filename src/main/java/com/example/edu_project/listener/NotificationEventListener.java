package com.example.edu_project.listener;

import com.example.edu_project.event.CommentCreatedEvent;
import com.example.edu_project.event.FollowCreatedEvent;
import com.example.edu_project.event.LikeCreatedEvent;
import com.example.edu_project.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 通知事件监听器，处理评论、点赞、关注等事件的异步通知发送
 * 使用 @TransactionalEventListener 确保在事务提交后才发送通知
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    @Autowired
    @Qualifier("notificationServiceImpl")
    private NotificationService notificationService;

    /**
     * 处理评论创建事件
     * TransactionPhase.AFTER_COMMIT 确保事务提交后才执行
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {
        try {
            // 不给自己发通知
            if (event.getCommenterUserId().equals(event.getPostAuthorUserId())) {
                return;
            }

            String notifyType = event.isReply() ? "reply" : "comment";
            String notifyTitle = event.isReply() ? "有人回复了你的评论" : "有人评论了你的文章";
            String notifyContent = event.isReply()
                    ? "用户回复了你的评论：" + event.getContent()
                    : "用户评论了你的文章：" + event.getPostTitle();

            notificationService.sendNotification(
                    notifyType,
                    notifyTitle,
                    notifyContent,
                    event.getCommenterUserId(),
                    event.getPostAuthorUserId(),
                    "post",
                    event.getPostId()
            );
        } catch (Exception e) {
            // 记录日志但不要影响业务流程，通知失败不应该影响评论创建
            log.error("发送评论通知失败: commentId={}, error={}", event.getCommentId(), e.getMessage());
        }
    }

    /**
     * 处理点赞创建事件
     * TransactionPhase.AFTER_COMMIT 确保事务提交后才执行
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCreated(LikeCreatedEvent event) {
        try {
            // 不给自己发通知
            if (event.getUserId().equals(event.getPostAuthorId())) {
                return;
            }

            notificationService.sendNotification(
                    "like",
                    "有人点赞了你的文章",
                    "用户点赞了你的文章：" + event.getPostTitle(),
                    event.getUserId(),
                    event.getPostAuthorId(),
                    "post",
                    event.getPostId()
            );
        } catch (Exception e) {
            // 记录日志但不要影响业务流程
            log.error("发送点赞通知失败: postId={}, error={}", event.getPostId(), e.getMessage());
        }
    }

    /**
     * 处理关注创建事件
     * TransactionPhase.AFTER_COMMIT 确保事务提交后才执行
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowCreated(FollowCreatedEvent event) {
        try {
            // 不给自己发通知
            if (event.getFollowerId().equals(event.getFollowingId())) {
                return;
            }

            notificationService.sendNotification(
                    "follow",
                    "有人关注了你",
                    "用户关注了你",
                    event.getFollowerId(),
                    event.getFollowingId(),
                    "user",
                    event.getFollowingId()
            );
        } catch (Exception e) {
            // 记录日志但不要影响业务流程
            log.error("发送关注通知失败: followerId={}, followingId={}, error={}",
                    event.getFollowerId(), event.getFollowingId(), e.getMessage());
        }
    }
}
