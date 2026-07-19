package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}