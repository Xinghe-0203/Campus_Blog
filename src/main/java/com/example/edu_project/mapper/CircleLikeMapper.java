package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 校友圈点赞 Mapper
 */
@Mapper
public interface CircleLikeMapper extends BaseMapper<CircleLike> {
    /**
     * 物理删除记录（用于解决软删除+唯一约束冲突问题）
     */
    @Delete("DELETE FROM blog_circle_like WHERE id = #{id}")
    void physicalDeleteById(Long id);

    /**
     * 根据动态ID删除所有点赞记录（物理删除）
     */
    @Delete("DELETE FROM blog_circle_like WHERE post_id = #{postId}")
    void deleteByPostId(Long postId);
}
