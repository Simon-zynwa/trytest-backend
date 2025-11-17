package org.example.framework.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class ValidationAspect {

    // 注入 JSR-303 的 Validator
    @Autowired
    private Validator validator;

    @Around("@annotation(org.example.common.annotation.ParameterValidation)")
    public Object doValidation(ProceedingJoinPoint pjp) throws Throwable {
        // 1. 获取方法的所有参数
        Object[] args = pjp.getArgs();

        for (Object arg : args) {
            if (arg != null) {
                // 2. 对每个参数进行校验
                Set<ConstraintViolation<Object>> violations = validator.validate(arg);

                // 3. 如果有校验失败的信息
                if (!violations.isEmpty()) {
                    String errorMessage = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("; "));
                    
                    // 4. 抛出异常，可以自定义一个业务异常，或者直接抛出通用异常
                    // 这里就不再执行目标方法了
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

        // 5. 如果所有参数都校验通过，则执行目标方法
        return pjp.proceed();
    }
}