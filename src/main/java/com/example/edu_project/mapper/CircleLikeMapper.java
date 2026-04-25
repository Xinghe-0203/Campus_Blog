package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 校友圈点赞 Mapper
 */
@Mapper
public interface CircleLikeMapper extends BaseMapper<CircleLike> {
}
