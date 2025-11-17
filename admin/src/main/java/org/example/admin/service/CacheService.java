package org.example.admin.service;

import org.example.admin.pojo.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 缓存服务接口
 */
public interface CacheService {
    
    /**
     * 根据用户名查询用户（使用三重缓存）
     */
    User getUserByUsername(String username);
    
    /**
     * 查询所有用户（使用三重缓存）
     */
    List<User> getAllUsers();
    
    /**
     * 根据ID查询用户（使用三重缓存）
     */
    User getUserById(Long userId);
    
    /**
     * 清空指定用户的缓存
     */
    void clearUserCache(String username);
    
    /**
     * 清空所有用户列表缓存
     */
    void clearAllUsersCache();
    
    /**
     * 获取缓存统计信息
     */
    Map<String, Object> getCacheStats();
}
