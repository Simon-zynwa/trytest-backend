package org.example.framework.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 内存缓存管理器
 * 使用ConcurrentHashMap实现线程安全的本地缓存
 */
@Component
@Slf4j
public class MemoryCacheManager {
    
    /**
     * 缓存数据存储
     */
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    /**
     * 定时清理过期缓存的线程池
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * 缓存条目，包含数据和过期时间
     */
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;
        
        public CacheEntry(Object value, long ttl) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + ttl * 1000;
        }
        
        public Object getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
    
    public MemoryCacheManager() {
        // 每分钟清理一次过期缓存
        scheduler.scheduleAtFixedRate(this::cleanExpiredCache, 1, 1, TimeUnit.MINUTES);
        log.info("内存缓存管理器初始化完成");
    }
    
    /**
     * 存储缓存
     * @param key 缓存key
     * @param value 缓存值
     * @param ttl 过期时间（秒）
     */
    public void put(String key, Object value, long ttl) {
        cache.put(key, new CacheEntry(value, ttl));
        log.debug("【内存缓存】存入数据，key={}, ttl={}秒", key, ttl);
    }
    
    /**
     * 获取缓存
     * @param key 缓存key
     * @return 缓存值，如果不存在或已过期返回null
     */
    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            log.debug("【内存缓存】未命中，key={}", key);
            return null;
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            log.debug("【内存缓存】已过期，key={}", key);
            return null;
        }
        
        log.debug("【内存缓存】命中，key={}", key);
        return entry.getValue();
    }
    
    /**
     * 删除缓存
     * @param key 缓存key
     */
    public void remove(String key) {
        cache.remove(key);
        log.debug("【内存缓存】删除，key={}", key);
    }
    
    /**
     * 清空所有缓存
     */
    public void clear() {
        cache.clear();
        log.info("【内存缓存】已清空所有缓存");
    }
    
    /**
     * 获取缓存数量
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * 清理过期的缓存
     */
    private void cleanExpiredCache() {
        int count = 0;
        for (String key : cache.keySet()) {
            CacheEntry entry = cache.get(key);
            if (entry != null && entry.isExpired()) {
                cache.remove(key);
                count++;
            }
        }
        if (count > 0) {
            log.info("【内存缓存】定时清理，移除{}个过期缓存，剩余{}个", count, cache.size());
        }
    }
}
