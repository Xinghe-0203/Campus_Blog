package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleRepost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 校友圈转发 Mapper
 */
@Mapper
public interface CircleRepostMapper extends BaseMapper<CircleRepost> {
    /**
     * 根据原动态ID删除所有转发记录（物理删除）
     */
    @Delete("DELETE FROM blog_circle_repost WHERE original_post_id = #{postId}")
    void deleteByOriginalPostId(Long postId);
}
