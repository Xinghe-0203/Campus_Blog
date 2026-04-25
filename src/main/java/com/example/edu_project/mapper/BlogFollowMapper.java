package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogFollow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 关注关系 Mapper 接口
 */
@Mapper
public interface BlogFollowMapper extends BaseMapper<BlogFollow> {
    /**
     * 物理删除记录（用于解决软删除+唯一约束冲突问题）
     */
    @Delete("DELETE FROM blog_follow WHERE id = #{id}")
    void physicalDeleteById(Long id);
}