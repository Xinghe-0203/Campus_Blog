package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogCollect;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogCollectMapper extends BaseMapper<BlogCollect> {
    /**
     * 物理删除记录（用于解决软删除+唯一约束冲突问题）
     */
    @Delete("DELETE FROM blog_collect WHERE id = #{id}")
    void physicalDeleteById(Long id);
}
