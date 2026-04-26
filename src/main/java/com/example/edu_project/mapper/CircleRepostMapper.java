package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleRepost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 校友圈转发 Mapper
 */
@Mapper
public interface CircleRepostMapper extends BaseMapper<CircleRepost> {
    /**
     * 根据原动态ID删除所有转发记录（逻辑删除）
     */
    @Update("UPDATE blog_circle_repost SET is_deleted = 1 WHERE original_post_id = #{postId}")
    int logicalDeleteByOriginalPostId(Long postId);
}
