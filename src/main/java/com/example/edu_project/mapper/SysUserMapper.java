package com.example.edu_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.edu_project.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户表 Mapper 接口
 * 【说明】继承 MyBatis Plus 的 BaseMapper，自动拥有 CRUD 方法
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 原子性增加登录失败计数并检查是否需要锁定
     * @param userId 用户ID
     * @param maxFailCount 最大失败次数
     * @param lockMinutes 锁定分钟数
     * @return 影响的行数
     */
    @Update("UPDATE sys_user SET login_fail_count = login_fail_count + 1, " +
            "lock_until = CASE WHEN login_fail_count + 1 >= #{maxFailCount} " +
            "THEN DATE_ADD(NOW(), INTERVAL #{lockMinutes} MINUTE) ELSE lock_until END " +
            "WHERE id = #{userId} AND (lock_until IS NULL OR lock_until <= NOW())")
    int incrementLoginFailCount(@Param("userId") Long userId, @Param("maxFailCount") int maxFailCount, @Param("lockMinutes") int lockMinutes);
}
