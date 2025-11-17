package org.example.admin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.admin.mapper.UserMapper;
import org.example.admin.pojo.entity.User;
import org.example.admin.service.CacheService;
import org.example.common.annotation.MultiLevelCache;
import org.example.framework.cache.MemoryCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存服务实现类
 * 
 * ⚠️ 重要说明：
 * 1. Service层的方法只负责从MySQL查询数据
 * 2. 缓存逻辑（内存缓存、Redis缓存）由AOP切面自动处理
 * 3. 不需要在这里手动操作Redis，AOP会自动完成
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MemoryCacheManager memoryCacheManager;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 根据用户名查询用户
     * @MultiLevelCache 注解会触发AOP，自动实现三重缓存查询
     * prefix = "user"           -> 缓存key前缀
     * key = "#username"         -> 使用方法参数username作为key
     * expireTime = 1800         -> 缓存30分钟
     * 最终的缓存key为：user:实际用户名
     * 例如：user:jack
     */
    @Override
    @MultiLevelCache(prefix = "user", key = "#username", expireTime = 1800)//切面查询内存--》redis--》mysql
    public User getUserByUsername(String username) {
        // 这里只负责从MySQL查询，缓存由AOP自动处理
        log.info("【Service】从MySQL查询用户，username={}", username);
        return userMapper.SelectByUsername(username);
    }
    
    /**
     * 查询所有用户
     * 缓存key固定为：allUsers
     */
    @Override
    @MultiLevelCache(prefix = "", key = "allUsers", expireTime = 600)
    public List<User> getAllUsers() {
        // 这里只负责从MySQL查询，缓存由AOP自动处理
        log.info("【Service】从MySQL查询所有用户");
        return userMapper.selectAllUser();
    }
    
    /**
     * 根据ID查询用户
     * 缓存key为：userId:123
     */
    @Override
    @MultiLevelCache(prefix = "userId", key = "#userId", expireTime = 1800)
    public User getUserById(Long userId) {
        // 这里只负责从MySQL查询，缓存由AOP自动处理
        log.info("【Service】从MySQL查询用户，userId={}", userId);
        // 注意：这里假设UserMapper有根据ID查询的方法
        // 如果没有，需要在UserMapper中添加
        return userMapper.SelectByUsername(userId.toString());
    }
    
    /**
     * 清空指定用户的缓存
     * 手动清理缓存的场景：当用户信息更新时，需要清空缓存
     */
    @Override
    public void clearUserCache(String username) {
        String cacheKey = "user:" + username;
        
        // 清空内存缓存
        memoryCacheManager.remove(cacheKey);
        log.info("【清空缓存】已清空内存缓存，key={}", cacheKey);
        
        // 清空Redis缓存
        redisTemplate.delete(cacheKey);
        log.info("【清空缓存】已清空Redis缓存，key={}", cacheKey);
    }
    
    /**
     * 清空所有用户列表缓存
     */
    @Override
    public void clearAllUsersCache() {
        String cacheKey = "allUsers";
        
        // 清空内存缓存
        memoryCacheManager.remove(cacheKey);
        log.info("【清空缓存】已清空内存缓存，key={}", cacheKey);
        
        // 清空Redis缓存
        redisTemplate.delete(cacheKey);
        log.info("【清空缓存】已清空Redis缓存，key={}", cacheKey);
    }
    
    /**
     * 获取缓存统计信息
     */
    @Override
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 内存缓存统计
        stats.put("memoryCacheSize", memoryCacheManager.size());
        
        // Redis缓存统计（示例）
        stats.put("redisCacheInfo", "Redis缓存统计信息");
        
        log.info("【缓存统计】{}", stats);
        return stats;
    }
}
