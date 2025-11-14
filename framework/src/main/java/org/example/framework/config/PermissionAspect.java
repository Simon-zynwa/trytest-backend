package org.example.framework.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.common.annotation.ActionLog;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect { // 为了职责单一，新建一个切面类

    // 环绕通知，拦截 ActionLog 注解
    @Around("@annotation(org.example.common.annotation.ActionLog)")
    public Object checkPermission(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("【环绕通知】进入权限检查...");

        // 1. 获取注解中的权限标识
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        ActionLog actionLog = method.getAnnotation(ActionLog.class);
        String requiredPermission = actionLog.permission();

        // 2. 【执行前】进行权限校验逻辑
        // 在真实项目中，这里会获取当前登录用户，并检查其权限列表
        boolean hasPermission = checkCurrentUserPermission(requiredPermission); // 这是一个模拟方法

        if (!hasPermission) {
            System.err.println("【环绕通知】权限不足，已拦截方法: " + pjp.getSignature().getName());
            // 如果权限不足，就不再执行目标方法，直接返回一个错误结果
            // return Result.fail("权限不足"); // 假设您有 Result.fail 方法
            throw new RuntimeException("权限不足，操作被禁止！");
        }

        System.out.println("【环绕通知】权限校验通过！");

        // 3. 【核心】手动调用目标方法
        // 如果不调用 pjp.proceed()，原始的 selectAllUser() 方法将永远不会被执行
        Object result = pjp.proceed();

        // 4. 【执行后】可以对结果进行处理或记录
        System.out.println("【环绕通知】目标方法已执行完毕。");

        return result;
    }

    // 模拟一个权限检查方法
    private boolean checkCurrentUserPermission(String permission) {
        if (permission == null || permission.isEmpty()) {
            return true; // 如果注解没定义权限，直接放行
        }
        // 模拟：只有 "user:list" 权限才放行
        return "user:list".equals(permission);
    }
}