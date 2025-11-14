package org.example.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.Response;
import org.example.common.model.Result;
import org.example.admin.pojo.entity.User;
import org.example.admin.service.UserService;
import org.example.common.annotation.ActionLog;
import org.example.common.annotation.ParameterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口") // Swagger 2 类注解
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登陆") // Swagger 2 方法注解
    @ParameterValidation
    public Result login(@RequestBody User user) {
        User loginUser = userService.SelectByUsername(user.getUsername());
        if (loginUser == null) {
            return Result.fail(Response.ERROR_USER_NOT_EXIST);
        }
        return loginUser.getPassword().equals(user.getPassword()) ? Result.success(Response.SUCCESS_LOGIN) : Result.fail(Response.ERROR_PASSWORD);
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result register(@Validated @RequestBody User user) {
        User registerUser = userService.SelectByUsername(user.getUsername());
        if (registerUser != null) {
            return Result.fail(Response.USER_HAS_EXISTED);
        }
        //参数校验
        userService.InsertUser(user);
        // 注册成功后返回成功响应
        log.info("用户注册成功: {}", user.getUsername());
        return Result.success();
    }


    @GetMapping("/selectAllUser")
    @ApiOperation(value="查询所有用户")
    @ActionLog(value = "查询用户", permission = "user:list") // 假设需要 user:list 权限
    public Result selectAllUser() {
        List<User> list =userService.selectAllUser();
        log.info("查询所有用户成功");
        return Result.success(list);
    }

    @PostMapping("/redisTestAdd")
    @ApiOperation(value = "Redis测试添加")
    public Result redisTestAdd(@RequestBody User user) {
        userService.redisTestAdd(user);
        log.info("Redis测试添加成功: {}", user.getUsername());
        return Result.success();
    }



}