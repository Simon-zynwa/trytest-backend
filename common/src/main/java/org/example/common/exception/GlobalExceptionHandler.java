package org.example.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.common.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 从异常对象中拿到错误信息
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        // 封装成我们自定义的 Result 返回
        log.error("参数校验失败: {}", message);
        return Result.fail(400, message);
    }

    //非法数据异常
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("非法参数异常: {}", ex.getMessage());
        return Result.fail(400, ex.getMessage());
    }

    /**
     * 处理AES加解密过程中抛出的自定义异常
     * 这类异常通常意味着服务器内部处理数据时出错，所以返回500状态码
     */
    @ExceptionHandler(AESException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置响应的HTTP状态码为500
    public Result handleAESException(AESException ex) {
        // 在服务器后台日志中打印详细的错误信息和堆栈跟踪
        log.error("AES处理时发生异常: {}", ex.getMessage(), ex);
        return Result.fail(500, "数据处理时发生内部错误，请联系管理员。");
    }

    //兜底异常
    /**
     * 处理所有未被其他 @ExceptionHandler 捕获的异常
     * 确保任何意外的错误都能以统一的格式返回
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleAllUncaughtException(Exception ex) {
        log.error("捕获到未处理的异常: {}", ex.getMessage(), ex);
        return Result.fail(500, "服务器发生未知错误。");
    }
}