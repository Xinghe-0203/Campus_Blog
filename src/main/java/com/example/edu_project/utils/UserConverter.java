package com.example.edu_project.utils;

import com.example.edu_project.entity.SysUser;
import com.example.edu_project.vo.UserVO;
import org.springframework.beans.BeanUtils;

public class UserConverter {

    public static UserVO toUserVO(SysUser user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // Don't expose sensitive fields
        vo.setEmail(null);
        vo.setRole(null);
        return vo;
    }

    public static UserVO toUserVOWithEmail(SysUser user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // Only include role if admin
        if (!"admin".equals(user.getRole())) {
            vo.setRole(null);
        }
        return vo;
    }
}