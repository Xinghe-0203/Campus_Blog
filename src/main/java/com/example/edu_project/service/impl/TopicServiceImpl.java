package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.Topic;
import com.example.edu_project.mapper.TopicMapper;
import com.example.edu_project.service.TopicService;
import com.example.edu_project.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 话题服务实现类
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTopic(String name, String description) {
        // 校验是否管理员
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "仅管理员可创建话题");
        }

        // 校验话题名称
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(400, "话题名称不能为空");
        }
        if (name.length() > 50) {
            throw new BusinessException(400, "话题名称不能超过50字符");
        }

        // 检查话题是否已存在
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getName, name.trim());
        Topic existingTopic = this.getOne(wrapper);
        if (existingTopic != null) {
            throw new BusinessException(400, "话题已存在");
        }

        // 创建话题
        Topic topic = new Topic();
        topic.setName(name.trim());
        topic.setDescription(description);
        topic.setPostCount(0);
        topic.setTrendingScore(0);
        topic.setStatus(1);

        this.save(topic);
        return topic.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Topic> getTopicList(Integer pageNum, Integer pageSize) {
        Page<Topic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getStatus, 1) // 只查询正常状态的话题
                .orderByDesc(Topic::getTrendingScore) // 按热度排序
                .orderByDesc(Topic::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Topic> getHotTopics(int limit) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getStatus, 1) // 只查询正常状态的话题
                .orderByDesc(Topic::getTrendingScore) // 按热度排序
                .last("LIMIT " + limit);

        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getOrCreateTopic(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String trimmedName = name.trim();
        // 去除#号
        if (trimmedName.startsWith("#")) {
            trimmedName = trimmedName.substring(1);
        }

        if (trimmedName.isEmpty()) {
            return null;
        }

        // 限制话题名称长度
        if (trimmedName.length() > 50) {
            trimmedName = trimmedName.substring(0, 50);
        }

        // 查询已存在的话题
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getName, trimmedName);
        Topic existingTopic = this.getOne(wrapper);

        if (existingTopic != null) {
            return existingTopic.getId();
        }

        // 创建新话题
        Topic topic = new Topic();
        topic.setName(trimmedName);
        topic.setDescription(null);
        topic.setPostCount(0);
        topic.setTrendingScore(0);
        topic.setStatus(1);

        this.save(topic);
        return topic.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Topic getTopicById(Long topicId) {
        Topic topic = this.getById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException(404, "话题不存在");
        }
        return topic;
    }
}