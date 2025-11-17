package org.example.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 * 用于防止并发操作导致的数据不一致问题
 */
@Component
@Slf4j
public class RedisLockUtil {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 尝试获取分布式锁
     * 
     * @param lockKey 锁的key
     * @param lockValue 锁的值（唯一标识，用于释放锁时验证）
     * @param expireTime 锁的过期时间（秒）
     * @return true=获取成功，false=获取失败
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime) {
        Boolean result = stringRedisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
        
        if (Boolean.TRUE.equals(result)) {
            log.info("【Redis锁】加锁成功，key={}, value={}, 过期时间={}秒", lockKey, lockValue, expireTime);
            return true;
        } else {
            log.warn("【Redis锁】加锁失败，key={}，锁已被其他线程持有", lockKey);
            return false;
        }
    }
    
    /**
     * 释放分布式锁
     * 使用Lua脚本保证原子性：只有持有锁的线程才能释放锁
     * 
     * @param lockKey 锁的key
     * @param lockValue 锁的值（用于验证是否是当前线程持有的锁）
     */
    public void unlock(String lockKey, String lockValue) {
        // Lua脚本：判断锁是否是当前线程持有的，是则删除
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "   return redis.call('del', KEYS[1]) " +
            "else " +
            "   return 0 " +
            "end";
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = stringRedisTemplate.execute(
            redisScript,
            Collections.singletonList(lockKey),
            lockValue
        );
        
        if (result != null && result == 1) {
            log.info("【Redis锁】释放锁成功，key={}, value={}", lockKey, lockValue);
        } else {
            log.warn("【Redis锁】释放锁失败，key={}, 锁可能已过期或不属于当前线程", lockKey);
        }
    }
    
    /**
     * 生成唯一的锁值（使用UUID）
     * 
     * @return UUID字符串
     */
    public String generateLockValue() {
        return UUID.randomUUID().toString();
    }
}
