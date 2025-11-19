package org.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Response {

    // --- 通用成功状态码 ---
    SUCCESS(200, "操作成功"),

    // --- 通用错误状态码 ---
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),

    // --- 用户模块 1000-1999 ---
    SUCCESS_LOGIN(1001, "登录成功"),
    ERROR_USER_NOT_EXIST(1002, "用户不存在"),
    ERROR_PASSWORD(1003, "密码错误"),
    SUCCESS_REGISTER(1004, "注册成功"),
    USER_HAS_EXISTED(1005, "用户已存在"),
    SUCCESS_SEND_EMAIL_CODE(1006, "验证码已发送到您的邮箱"),
    ERROR_SEND_EMAIL_CODE(1007, "验证码发送失败，请稍后重试"),
    ERROR_VERIFICATION_CODE(1008, "验证码错误或已过期"),
    ERROR_EMAIL_NOT_REGISTERED(1009, "该邮箱未注册"),
    ERROR_PHONE_NOT_REGISTERED(1010,"该手机号未注册");


    private final int code;
    private final String message;
}
