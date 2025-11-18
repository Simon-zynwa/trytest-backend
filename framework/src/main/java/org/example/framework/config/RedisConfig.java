package org.example.framework.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    /**
     * RedisTemplate 配置
     * <p>
     * 该方法配置了 RedisTemplate 的序列化方式，使用 String 序列化器处理 key，
     * 使用 JSON 序列化器处理 value，以便于存储和读取对象。
     *
     * @param factory RedisConnectionFactory
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置连接工厂
        template.setConnectionFactory(factory);

        // 创建 String 序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 创建 JSON 序列化器
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // ---- 设置序列化规则 ----

        // 1. 设置 key 的序列化方式为 String
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // 2. 设置 value 的序列化方式为 JSON
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        // 初始化 RedisTemplate
        template.afterPropertiesSet();

        return template;
    }

}
