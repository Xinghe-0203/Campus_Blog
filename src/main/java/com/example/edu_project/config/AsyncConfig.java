package com.example.edu_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置类
 *
 * 【功能说明】
 * - 用于异步执行耗时操作，提升系统响应速度
 * - 典型场景：发送通知、发送邮件、异步日志记录等
 *
 * 【线程池参数说明】
 * - corePoolSize: 核心线程数，常驻线程池
 * - maxPoolSize: 最大线程数，处理突发流量
 * - queueCapacity: 阻塞队列容量，存储等待执行的任务
 * - threadNamePrefix: 线程名前缀，便于日志追踪
 * - rejectedExecutionHandler: 拒绝策略，队列满时由调用线程执行
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 通用异步任务执行器
     * 适用于：通知发送、邮件发送等一般异步任务
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：常驻线程数量
        executor.setCorePoolSize(5);

        // 最大线程数：处理突发流量时的上限
        executor.setMaxPoolSize(10);

        // 阻塞队列容量：超过核心线程数的任务在此排队
        executor.setQueueCapacity(200);

        // 线程名前缀：便于日志追踪和问题排查
        executor.setThreadNamePrefix("async-task-");

        // 空闲线程存活时间：超过核心线程数的线程在空闲时会被回收
        executor.setKeepAliveSeconds(60);

        // 拒绝策略：队列满时由调用线程执行（同步执行，不丢失任务）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 优雅关闭时的最大等待时间
        executor.setAwaitTerminationSeconds(60);

        // 初始化线程池
        executor.initialize();

        return executor;
    }

    /**
     * 通知发送专用执行器
     * 适用于：站内通知等对时效性要求较高的任务
     */
    @Bean("notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 通知任务相对重要，可以多分配一些线程
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("notification-");
        executor.setKeepAliveSeconds(30);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        return executor;
    }
}
