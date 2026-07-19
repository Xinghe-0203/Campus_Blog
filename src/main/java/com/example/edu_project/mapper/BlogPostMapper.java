package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BlogPostMapper extends BaseMapper<BlogPost> {

    @Update("UPDATE blog_post SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViewCount(@Param("postId") Long postId);

    @Update("UPDATE blog_post SET like_count = like_count + 1 WHERE id = #{postId}")
    void incrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE blog_post SET like_count = like_count - 1 WHERE id = #{postId} AND like_count > 0")
    void decrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE blog_post SET comment_count = comment_count + 1 WHERE id = #{postId}")
    void incrementCommentCount(@Param("postId") Long postId);

    @Update("UPDATE blog_post SET comment_count = comment_count - #{count} WHERE id = #{postId} AND comment_count >= #{count}")
    void decrementCommentCount(@Param("postId") Long postId, @Param("count") int count);

    @Update("UPDATE blog_post SET collect_count = collect_count + 1 WHERE id = #{postId}")
    void incrementCollectCount(@Param("postId") Long postId);

    @Update("UPDATE blog_post SET collect_count = collect_count - 1 WHERE id = #{postId} AND collect_count > 0")
    void decrementCollectCount(@Param("postId") Long postId);
}