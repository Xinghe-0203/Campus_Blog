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
}