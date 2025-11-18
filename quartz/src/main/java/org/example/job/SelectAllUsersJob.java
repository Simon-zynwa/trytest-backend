package org.example.job;

import lombok.extern.slf4j.Slf4j;
import org.example.common.util.AESUtil;
import org.example.framework.util.RedisLockUtil;
import org.example.pojo.entity.User;
import org.example.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时查询所有用户并更新Redis缓存
 * 使用分布式锁保证数据一致性
 */
@Slf4j
@Component
public class SelectAllUsersJob implements Job {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AESUtil aesUtil;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("⏰ [SelectAllUsersJob] 定时任务开始执行，当前时间: {}", currentTime);

        // 定义锁的key和value
        String lockKey = "lock:refreshAllUsers";
        String lockValue = redisLockUtil.generateLockValue();

        // 尝试获取分布式锁，超时时间30秒
        boolean locked = redisLockUtil.tryLock(lockKey, lockValue, 30);

        if (!locked) {
            log.warn("⚠️ [SelectAllUsersJob] 获取锁失败，可能有其他任务正在执行");
            return;
        }

        try {
            log.info("🔒 [SelectAllUsersJob] 获取锁成功，开始刷新用户缓存");

            // 1. 从数据库查询所有用户（加密数据）
            List<User> users = userService.selectAllUser();
            log.info("📊 [SelectAllUsersJob] 从数据库查询到 {} 个用户", users.size());

            // 2. 更新Redis缓存（存储加密数据）
            String cacheKey = "allUsers";
            redisTemplate.opsForValue().set(cacheKey, users, 600, TimeUnit.SECONDS);
            log.info("✅ [SelectAllUsersJob] Redis缓存已更新: key={}, 过期时间=600秒", cacheKey);

        } catch (Exception e) {
            log.error("❌ [SelectAllUsersJob] 定时任务执行失败", e);
            throw new JobExecutionException("定时任务执行失败: " + e.getMessage(), e);
        } finally {
            // 释放锁
            redisLockUtil.unlock(lockKey, lockValue);
            log.info("🔓 [SelectAllUsersJob] 锁已释放");
        }
    }
}
