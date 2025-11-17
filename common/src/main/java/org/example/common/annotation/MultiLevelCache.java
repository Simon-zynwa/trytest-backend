package org.example.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多级缓存注解
 * 用于标记需要使用三重缓存查询的方法（内存 -> Redis -> MySQL）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLevelCache {
    
    /**
     * 缓存key的前缀
     */
    String prefix() default "";
    
    /**
     * 缓存key，支持SpEL表达式
     * 例如：#username 表示取第一个参数username的值
     */
    String key();
    
    /**
     * 缓存过期时间（秒），默认30分钟
     */
    long expireTime() default 1800;
    
    /**
     * 是否使用内存缓存，默认true
     */
    boolean useMemory() default true;
    
    /**
     * 是否使用Redis缓存，默认true
     */
    boolean useRedis() default true;
}
