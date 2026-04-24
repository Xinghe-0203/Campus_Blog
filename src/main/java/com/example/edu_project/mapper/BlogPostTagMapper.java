package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogPostTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogPostTagMapper extends BaseMapper<BlogPostTag> {

    @Insert("INSERT INTO blog_post_tag (post_id, tag_id) VALUES (#{postId}, #{tagId})")
    void insertPostTag(@Param("postId") Long postId, @Param("tagId") Long tagId);
}
