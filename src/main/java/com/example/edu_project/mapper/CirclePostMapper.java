package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CirclePost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CirclePostMapper extends BaseMapper<CirclePost> {

    @Update("UPDATE blog_circle_post SET view_count = view_count + 1 WHERE id = #{id} AND status != 2")
    void incrementViewCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET like_count = like_count + 1 WHERE id = #{id} AND status != 2")
    void incrementLikeCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET like_count = like_count - 1 WHERE id = #{id} AND status != 2 AND like_count > 0")
    void decrementLikeCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET comment_count = comment_count + 1 WHERE id = #{id} AND status != 2")
    void incrementCommentCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET comment_count = comment_count - #{count} WHERE id = #{id} AND status != 2 AND comment_count >= #{count}")
    void decrementCommentCount(@Param("id") Long id, @Param("count") int count);

    @Update("UPDATE blog_circle_post SET repost_count = repost_count + 1 WHERE id = #{id} AND status != 2")
    void incrementRepostCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET repost_count = repost_count - 1 WHERE id = #{id} AND status != 2 AND repost_count > 0")
    void decrementRepostCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET status = 2 WHERE id = #{id} AND status != 2")
    void markAsDeleted(@Param("id") Long id);

    @Update("UPDATE blog_topic SET post_count = post_count + 1, trending_score = trending_score + 1 WHERE id = #{topicId}")
    void incrementTopicPostCount(@Param("topicId") Long topicId);
}