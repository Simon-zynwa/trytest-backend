package org.example.common.model;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null; // 默认data为null
    }

    public Result() {
        this.code = 0; // 默认code为0
        this.message = "success"; // 默认message为"success"
        this.data = null; // 默认data为null
    }

    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    public static Result success() {
        return new Result(200, "success");
    }

    public static Result fail(Integer code, String message) {
        return new Result(code, message);
    }

    public static Result fail(String message) {
        return new Result(500, message);
    }

    public static Result fail() {
        return new Result(500, "failed");
    }

    /**
     * 使用 Response 枚举返回成功结果
     * @param response 状态码枚举
     * @param data 返回数据
     * @return Result
     */
    public static Result success(Response response, Object data) {
        return new Result(response.getCode(), response.getMessage(), data);
    }

    /**
     * 使用 Response 枚举返回成功结果 (无数据)
     * @param response 状态码枚举
     * @return Result
     */
    public static Result success(Response response) {
        return success(response, null);
    }

    /**
     * 使用 Response 枚举返回失败结果
     * @param response 状态码枚举
     * @return Result
     */
    public static Result fail(Response response) {
        return new Result(response.getCode(), response.getMessage());
    }


}
