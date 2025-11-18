package org.example.framework.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.common.annotation.MultiLevelCache;
import org.example.framework.cache.MemoryCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存切面
 * 实现三重查询逻辑：内存缓存 -> Redis缓存 -> MySQL数据库
 */
@Aspect
@Component
@Slf4j
public class MultiLevelCacheAspect {
    
    @Autowired
    private MemoryCacheManager memoryCacheManager;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private final ExpressionParser parser = new SpelExpressionParser();
    
    // 用于深拷贝的ObjectMapper（启用默认类型信息，保留泛型）
    private final ObjectMapper objectMapper = new ObjectMapper()
        .activateDefaultTyping(
            new ObjectMapper().getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );
    
    /**
     * 深拷贝对象，防止缓存污染
     * 使用JSON序列化/反序列化实现深拷贝
     * 通过enableDefaultTyping保留类型信息，避免泛型丢失
     */
    private Object deepCopy(Object source) {
        if (source == null) {
            return null;
        }
        try {
            // 序列化为JSON字符串（包含类型信息），再反序列化为新对象
            String json = objectMapper.writeValueAsString(source);
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.warn("【深拷贝】深拷贝失败，返回原对象: {}", e.getMessage());
            return source;  // 如果深拷贝失败，返回原对象
        }
    }

    // 1. 执行前通知
    @Before("@annotation(org.example.common.annotation.MultiLevelCache)")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("【执行前】通用日志切面捕获: " + joinPoint.getSignature().getName());
        //打印入参
        System.out.println("【执行前】入参: " + Arrays.toString(joinPoint.getArgs()));

    }

    // 2. 执行后通知
    // returning = "result" 告诉Spring，把目标方法的返回值注入到名为 result 的参数中
    @AfterReturning(pointcut = "@annotation(org.example.common.annotation.MultiLevelCache)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("【执行后】通用日志切面捕获: " + joinPoint.getSignature().getName());
        // 您甚至可以获取并打印方法的返回值
        System.out.println("【执行后】出参: " + result);
    }
    
    /**
     * 环绕通知，拦截@MultiLevelCache注解
     */
    @Around("@annotation(org.example.common.annotation.MultiLevelCache)")
    public Object cacheAround(ProceedingJoinPoint pjp) throws Throwable {
        // 获取方法签名和注解
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        MultiLevelCache cacheAnnotation = method.getAnnotation(MultiLevelCache.class);
        
        // 生成缓存key
        String cacheKey = generateCacheKey(cacheAnnotation, pjp);
        long expireTime = cacheAnnotation.expireTime();
        
        log.info("【三重缓存查询】开始查询，key={}", cacheKey);
        
        // ==================== 第一层：内存缓存 ====================
        if (cacheAnnotation.useMemory()) {
            Object memoryData = memoryCacheManager.get(cacheKey);
            if (memoryData != null) {
                log.info("【三重缓存查询】✅ 内存缓存命中，key={}", cacheKey);
                // 返回深拷贝对象，防止调用方修改缓存中的数据
                return deepCopy(memoryData);
            }
            log.info("【三重缓存查询】❌ 内存缓存未命中，key={}", cacheKey);
        }
        
        // ==================== 第二层：Redis缓存 ====================
        if (cacheAnnotation.useRedis()) {
            Object redisData = redisTemplate.opsForValue().get(cacheKey);
            if (redisData != null) {
                log.info("【三重缓存查询】✅ Redis缓存命中，key={}", cacheKey);
                
                // 回写到内存缓存
                if (cacheAnnotation.useMemory()) {
                    memoryCacheManager.put(cacheKey, redisData, expireTime);
                    log.info("【三重缓存查询】📝 数据回写到内存缓存");
                }
                
                // 返回深拷贝对象，防止调用方修改缓存中的数据
                return deepCopy(redisData);
            }
            log.info("【三重缓存查询】❌ Redis缓存未命中，key={}", cacheKey);
        }
        
        // ==================== 第三层：MySQL数据库 ====================
        log.info("【三重缓存查询】🔍 查询MySQL数据库...");
        Object dbData = pjp.proceed();
        
        if (dbData == null) {
            log.warn("【三重缓存查询】⚠️ MySQL查询结果为null，key={}", cacheKey);
            return null;
        }
        
        log.info("【三重缓存查询】✅ MySQL查询成功，key={}", cacheKey);
        
        // 数据回写到Redis和内存缓存
        if (cacheAnnotation.useRedis()) {
            redisTemplate.opsForValue().set(cacheKey, dbData, expireTime, TimeUnit.SECONDS);
            log.info("【三重缓存查询】📝 数据回写到Redis缓存，过期时间={}秒", expireTime);
        }
        
        if (cacheAnnotation.useMemory()) {
            memoryCacheManager.put(cacheKey, dbData, expireTime);
            log.info("【三重缓存查询】📝 数据回写到内存缓存，过期时间={}秒", expireTime);
        }
        
        return dbData;
    }
    
    /**
     * 生成缓存key
     * 支持SpEL表达式，例如：#username
     */
    private String generateCacheKey(MultiLevelCache cacheAnnotation, ProceedingJoinPoint pjp) {
        String prefix = cacheAnnotation.prefix();
        String keyExpression = cacheAnnotation.key();
        
        // 如果key不包含SpEL表达式，直接返回
        if (!keyExpression.contains("#")) {
            return prefix.isEmpty() ? keyExpression : prefix + ":" + keyExpression;
        }
        
        // 解析SpEL表达式
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        
        // 创建SpEL上下文
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        
        // 解析表达式
        String key = parser.parseExpression(keyExpression).getValue(context, String.class);
        
        return prefix.isEmpty() ? key : prefix + ":" + key;
    }
}
