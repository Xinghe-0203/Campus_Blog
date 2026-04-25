package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.BlogReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 举报 Mapper 接口
 */
@Mapper
public interface BlogReportMapper extends BaseMapper<BlogReport> {
}