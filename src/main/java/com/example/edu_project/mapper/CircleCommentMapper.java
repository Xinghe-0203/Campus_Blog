package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CircleComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 校友圈评论 Mapper
 */
@Mapper
public interface CircleCommentMapper extends BaseMapper<CircleComment> {
    /**
     * 根据动态ID删除所有评论（物理删除）
     */
    @Delete("DELETE FROM blog_circle_comment WHERE post_id = #{postId}")
    void deleteByPostId(Long postId);
}
