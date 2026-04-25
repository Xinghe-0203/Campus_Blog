package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 关注关系 Mapper 接口
 */
@Mapper
public interface BlogFollowMapper extends BaseMapper<BlogFollow> {
}