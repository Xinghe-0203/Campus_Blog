package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 * 【说明】继承 MyBatis Plus 的 BaseMapper，自动拥有 CRUD 方法
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
