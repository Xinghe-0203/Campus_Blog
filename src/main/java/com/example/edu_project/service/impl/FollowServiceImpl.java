package com.example.edu_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.edu_project.common.exception.BusinessException;
import com.example.edu_project.entity.BlogFollow;
import com.example.edu_project.entity.SysUser;
import com.example.edu_project.mapper.BlogFollowMapper;
import com.example.edu_project.mapper.SysUserMapper;
import com.example.edu_project.service.FollowService;
import com.example.edu_project.vo.FollowStatusVO;
import com.example.edu_project.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 关注服务实现类
 */
@Service
public class FollowServiceImpl extends ServiceImpl<BlogFollowMapper, BlogFollow> implements FollowService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 细粒度锁映射表：key="followerId-followingId"，value=锁对象
     */
    private static final int MAX_LOCKS_SIZE = 10000;
    private static final long LOCK_EXPIRE_MS = 300000; // 5分钟
    private final ConcurrentMap<String, LockEntry> followLocks = new ConcurrentHashMap<>();

    private static class LockEntry {
        final Object lock;
        final long createTime;

        LockEntry(Object lock) {
            this.lock = lock;
            this.createTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createTime > LOCK_EXPIRE_MS;
        }
    }

    private Object getLock(String lockKey) {
        if (followLocks.size() >= MAX_LOCKS_SIZE) {
            followLocks.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }

        LockEntry entry = followLocks.compute(lockKey, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new LockEntry(new Object());
            }
            return existing;
        });
        return entry.lock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FollowStatusVO follow(Long targetUserId, Long currentUserId) {
        // 参数校验
        if (currentUserId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (targetUserId == null) {
            throw new BusinessException(400, "目标用户ID不能为空");
        }
        // 不能关注自己
        if (targetUserId.equals(currentUserId)) {
            throw new BusinessException(400, "不能关注自己");
        }

        // 检查目标用户是否存在
        SysUser targetUser = sysUserMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        FollowStatusVO result = new FollowStatusVO();

        // 使用细粒度锁
        String lockKey = currentUserId + "-" + targetUserId;
        synchronized (getLock(lockKey)) {
            // 检查是否已关注
            LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogFollow::getFollowerId, currentUserId)
                  .eq(BlogFollow::getFollowingId, targetUserId);
            BlogFollow existingFollow = this.getOne(wrapper);

            if (existingFollow != null) {
                // 已关注
                result.setFollowing(true);
                result.setAction("already_following");
            } else {
                // 尝试关注
                BlogFollow newFollow = new BlogFollow();
                newFollow.setFollowerId(currentUserId);
                newFollow.setFollowingId(targetUserId);
                try {
                    this.save(newFollow);
                    // 原子更新计数
                    sysUserMapper.incrementFollowerCount(targetUserId);
                    sysUserMapper.incrementFollowingCount(currentUserId);
                    result.setFollowing(true);
                    result.setAction("follow");
                } catch (DuplicateKeyException e) {
                    // 并发情况下另一个请求已经插入了
                    BlogFollow concurrentFollow = this.getOne(wrapper);
                    if (concurrentFollow != null) {
                        result.setFollowing(true);
                        result.setAction("already_following");
                    } else {
                        // 极少数情况：记录刚被删了，那就当作关注成功
                        sysUserMapper.incrementFollowerCount(targetUserId);
                        sysUserMapper.incrementFollowingCount(currentUserId);
                        result.setFollowing(true);
                        result.setAction("follow");
                    }
                }
            }

            // 获取最新计数
            SysUser updatedTarget = sysUserMapper.selectById(targetUserId);
            SysUser updatedCurrent = sysUserMapper.selectById(currentUserId);
            result.setFollowerCount(updatedTarget != null ? updatedTarget.getFollowerCount() : 0);
            result.setFollowingCount(updatedCurrent != null ? updatedCurrent.getFollowingCount() : 0);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FollowStatusVO unfollow(Long targetUserId, Long currentUserId) {
        // 参数校验
        if (currentUserId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (targetUserId == null) {
            throw new BusinessException(400, "目标用户ID不能为空");
        }
        // 不能取消关注自己
        if (targetUserId.equals(currentUserId)) {
            throw new BusinessException(400, "不能取消关注自己");
        }

        // 检查目标用户是否存在
        SysUser targetUser = sysUserMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        FollowStatusVO result = new FollowStatusVO();

        // 使用细粒度锁
        String lockKey = currentUserId + "-" + targetUserId;
        synchronized (getLock(lockKey)) {
            // 检查是否已关注
            LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogFollow::getFollowerId, currentUserId)
                  .eq(BlogFollow::getFollowingId, targetUserId);
            BlogFollow existingFollow = this.getOne(wrapper);

            if (existingFollow == null) {
                // 未关注
                result.setFollowing(false);
                result.setAction("not_following");
            } else {
                // 取消关注：物理删除记录（解决软删除+唯一约束冲突）
                ((BlogFollowMapper) this.baseMapper).physicalDeleteById(existingFollow.getId());
                // 原子更新计数
                sysUserMapper.decrementFollowerCount(targetUserId);
                sysUserMapper.decrementFollowingCount(currentUserId);
                result.setFollowing(false);
                result.setAction("unfollow");
            }

            // 获取最新计数
            SysUser updatedTarget = sysUserMapper.selectById(targetUserId);
            SysUser updatedCurrent = sysUserMapper.selectById(currentUserId);
            result.setFollowerCount(updatedTarget != null ? updatedTarget.getFollowerCount() : 0);
            result.setFollowingCount(updatedCurrent != null ? updatedCurrent.getFollowingCount() : 0);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long targetUserId, Long currentUserId) {
        if (currentUserId == null || targetUserId == null) {
            return false;
        }
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, currentUserId)
              .eq(BlogFollow::getFollowingId, targetUserId);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> getFollowers(Long userId) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 查询粉丝
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowingId, userId)
              .orderByDesc(BlogFollow::getCreateTime);
        List<BlogFollow> follows = this.list(wrapper);

        if (follows.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取粉丝用户信息
        List<Long> followerIds = follows.stream()
                .map(BlogFollow::getFollowerId)
                .collect(java.util.stream.Collectors.toList());
        List<SysUser> followers = sysUserMapper.selectBatchIds(followerIds);

        return followers.stream()
                .map(this::convertToUserVO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> getFollowing(Long userId) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 查询关注
        LambdaQueryWrapper<BlogFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFollow::getFollowerId, userId)
              .orderByDesc(BlogFollow::getCreateTime);
        List<BlogFollow> follows = this.list(wrapper);

        if (follows.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取关注用户信息
        List<Long> followingIds = follows.stream()
                .map(BlogFollow::getFollowingId)
                .collect(java.util.stream.Collectors.toList());
        List<SysUser> followings = sysUserMapper.selectBatchIds(followingIds);

        return followings.stream()
                .map(this::convertToUserVO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FollowCountsVO getCounts(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        FollowCountsVO counts = new FollowCountsVO();
        counts.setUserId(userId);
        counts.setFollowerCount(user.getFollowerCount() != null ? user.getFollowerCount() : 0);
        counts.setFollowingCount(user.getFollowingCount() != null ? user.getFollowingCount() : 0);
        return counts;
    }

    /**
     * 转换 SysUser 为 UserVO
     */
    private UserVO convertToUserVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}