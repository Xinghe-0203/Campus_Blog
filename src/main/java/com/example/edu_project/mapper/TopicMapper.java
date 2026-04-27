package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 话题表 Mapper 接口
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 原子性增加动态数
     * @param topicId 话题ID
     * @return 影响的行数
     */
    @Update("UPDATE blog_topic SET post_count = post_count + 1, trending_score = trending_score + 1 WHERE id = #{topicId}")
    int incrementPostCount(@Param("topicId") Long topicId);

    /**
     * 原子性减少动态数
     * @param topicId 话题ID
     * @return 影响的行数
     */
    @Update("UPDATE blog_topic SET post_count = GREATEST(post_count - 1, 0), trending_score = GREATEST(trending_score - 1, 0) WHERE id = #{topicId}")
    int decrementPostCount(@Param("topicId") Long topicId);

    /**
     * 增加热度分数
     * @param topicId 话题ID
     * @param score 增加的分数
     * @return 影响的行数
     */
    @Update("UPDATE blog_topic SET trending_score = trending_score + #{score} WHERE id = #{topicId}")
    int incrementTrendingScore(@Param("topicId") Long topicId, @Param("score") int score);
}