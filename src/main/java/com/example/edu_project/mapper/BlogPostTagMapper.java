package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogPostTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogPostTagMapper extends BaseMapper<BlogPostTag> {

    /**
     * 批量插入文章标签关联
     * @param postId 文章ID
     * @param tagIds 标签ID列表
     */
    @Insert("<script>" +
            "INSERT INTO blog_post_tag (post_id, tag_id) VALUES " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{postId}, #{tagId})" +
            "</foreach>" +
            "</script>")
    void batchInsertPostTags(@Param("postId") Long postId, @Param("tagIds") java.util.List<Long> tagIds);
}
