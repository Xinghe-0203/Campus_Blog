package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 校友圈评论 Mapper
 */
@Mapper
public interface CircleCommentMapper extends BaseMapper<CircleComment> {
}
