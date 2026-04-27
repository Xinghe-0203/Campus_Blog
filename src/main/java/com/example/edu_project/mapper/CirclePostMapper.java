package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.CirclePost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CirclePostMapper extends BaseMapper<CirclePost> {

    @Select("SELECT COUNT(DISTINCT user_id) FROM blog_circle_post WHERE create_time >= #{since} AND is_deleted = 0")
    Long countDistinctAuthorsSince(@Param("since") LocalDateTime since);

    @Update("UPDATE blog_circle_post SET view_count = view_count + 1 WHERE id = #{id} AND is_deleted = 0")
    void incrementViewCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET like_count = like_count + 1 WHERE id = #{id} AND is_deleted = 0")
    void incrementLikeCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET like_count = like_count - 1 WHERE id = #{id} AND is_deleted = 0 AND like_count > 0")
    void decrementLikeCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET comment_count = comment_count + 1 WHERE id = #{id} AND is_deleted = 0")
    void incrementCommentCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET comment_count = comment_count - #{count} WHERE id = #{id} AND is_deleted = 0 AND comment_count >= #{count}")
    void decrementCommentCount(@Param("id") Long id, @Param("count") int count);

    @Update("UPDATE blog_circle_post SET repost_count = repost_count + 1 WHERE id = #{id} AND is_deleted = 0")
    void incrementRepostCount(@Param("id") Long id);

    @Update("UPDATE blog_circle_post SET repost_count = repost_count - 1 WHERE id = #{id} AND is_deleted = 0 AND repost_count > 0")
    void decrementRepostCount(@Param("id") Long id);

    @Update("UPDATE blog_topic SET post_count = post_count + 1, trending_score = trending_score + 1 WHERE id = #{topicId}")
    void incrementTopicPostCount(@Param("topicId") Long topicId);

    @Update("<script>" +
            "UPDATE blog_topic SET post_count = post_count + 1, trending_score = trending_score + 1 " +
            "WHERE id IN <foreach collection='topicIds' item='topicId' open='(' separator=',' close=')'>" +
            "#{topicId}</foreach>" +
            "</script>")
    void batchIncrementTopicPostCount(@Param("topicIds") List<Long> topicIds);
}