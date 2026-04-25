package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogDraft;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章草稿 Mapper 接口
 */
@Mapper
public interface BlogDraftMapper extends BaseMapper<BlogDraft> {
}
