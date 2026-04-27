package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 私信 Mapper 接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}