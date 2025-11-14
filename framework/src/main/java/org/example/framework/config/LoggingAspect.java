package org.example.framework.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 1. 执行前通知 (保留原来的)
    @Before("@annotation(org.example.common.annotation.ActionLog)")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("【执行前】通用日志切面捕获: " + joinPoint.getSignature().getName());
    }

    // 2. 【新增】执行后通知
    // returning = "result" 告诉Spring，把目标方法的返回值注入到名为 result 的参数中
    @AfterReturning(pointcut = "@annotation(org.example.common.annotation.ActionLog)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("【执行后】通用日志切面捕获: " + joinPoint.getSignature().getName());
        // 您甚至可以获取并打印方法的返回值
        System.out.println("【执行后】方法返回值: " + result);
    }
}
