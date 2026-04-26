package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BlogLikeMapper extends BaseMapper<BlogLike> {
    /**
     * 逻辑删除记录（用于解决软删除+唯一约束冲突问题）
     */
    @Update("UPDATE blog_like SET is_deleted = 1 WHERE id = #{id}")
    int logicalDeleteById(Long id);
}
