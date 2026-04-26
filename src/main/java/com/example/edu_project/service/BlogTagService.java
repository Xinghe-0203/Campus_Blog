package com.example.edu_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.entity.BlogTag;

/**
 * 标签服务接口
 */
public interface BlogTagService extends IService<BlogTag> {

    /**
     * 获取所有标签列表
     * @return 标签列表
     */
    java.util.List<BlogTag> listAllTags();

    /**
     * 创建标签
     * @param name 标签名称
     * @return 创建的标签
     */
    BlogTag createTag(String name);

    /**
     * 删除标签（仅管理员可操作）
     * @param tagId 标签ID
     */
    void deleteTag(Long tagId);
}