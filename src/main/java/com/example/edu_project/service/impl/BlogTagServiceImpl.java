package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogTag;
import com.example.edu_project.mapper.BlogTagMapper;
import com.example.edu_project.service.BlogTagService;
import com.example.edu_project.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {

    @Override
    @Transactional(readOnly = true)
    public List<BlogTag> listAllTags() {
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BlogTag::getName);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlogTag createTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(400, "标签名称不能为空");
        }
        String trimmedName = name.trim();
        if (trimmedName.length() > 20) {
            throw new BusinessException(400, "标签名称不能超过20个字符");
        }
        // 检查标签是否已存在
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogTag::getName, trimmedName);
        BlogTag existingTag = this.getOne(wrapper);
        if (existingTag != null) {
            throw new BusinessException(409, "标签已存在");
        }
        BlogTag tag = new BlogTag();
        tag.setName(trimmedName);
        this.save(tag);
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        // 仅管理员可删除标签
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new BusinessException(403, "仅管理员可删除标签");
        }
        BlogTag tag = this.getById(tagId);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }
        // 删除标签（软删除）
        this.removeById(tagId);
    }
}