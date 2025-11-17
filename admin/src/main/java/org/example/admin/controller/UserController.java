package org.example.admin.controller;

import com.fasterxml.jackson.databind.util.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.admin.pojo.dto.UserLoginByUsernameDTO;
import org.example.admin.pojo.dto.UserMessageUpdateDTO;
import org.example.admin.pojo.dto.UserRegisterDTO;
import org.example.common.model.Response;
import org.example.common.model.Result;
import org.example.admin.pojo.entity.User;
import org.example.admin.service.UserService;
import org.example.common.annotation.ActionLog;
import org.example.common.annotation.ParameterValidation;
import org.example.common.util.AESUtil;
import org.example.common.util.HashUtil;
import org.example.common.util.RSAUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口") // Swagger 2 类注解
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AESUtil aesUtil;
    
    @Autowired
    private RSAUtil rsaUtil;

    @PostMapping("/login")
    @ApiOperation(value = "用户登陆") // Swagger 2 方法注解
    @ParameterValidation
    public Result login(@RequestBody UserLoginByUsernameDTO userLoginByUsernameDTO) {
        User loginUser = userService.SelectByUsername(userLoginByUsernameDTO.getUsername());
        if (loginUser == null) {
            return Result.fail(Response.ERROR_USER_NOT_EXIST);
        }
        
        // 方式1：解密数据库中的密码进行比对
        try {
            String decryptedPassword = AESUtil.decrypt(loginUser.getPassword(), aesUtil.getSecretKey());
            if (decryptedPassword.equals(userLoginByUsernameDTO.getPassword())) {
                log.info("用户登录成功: {}", userLoginByUsernameDTO.getUsername());
                return Result.success(Response.SUCCESS_LOGIN);
            } else {
                return Result.fail(Response.ERROR_PASSWORD);
            }
        } catch (Exception e) {
            log.error("密码解密失败: {}", e.getMessage());
            return Result.fail(Response.ERROR_PASSWORD);
        }
        
        /* 方式2：加密用户输入的密码再比对（不推荐，因为每次加密结果不同）
        try {
            String encryptedInputPassword = AESUtil.encrypt(user.getPassword(), aesUtil.getSecretKey());
            // 注意：由于每次加密时IV是随机的，所以这种方式无法直接比较
            // 必须先解密数据库密码再比较，或者使用Hash算法（如BCrypt）
        } catch (Exception e) {
            log.error("密码加密失败: {}", e.getMessage());
            return Result.fail(Response.ERROR_PASSWORD);
        }
        */
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        User registerUser = userService.SelectByUsername(userRegisterDTO.getUsername());
        if (registerUser != null) {
            return Result.fail(Response.USER_HAS_EXISTED);
        }
        
        // 💡 加密密码后再存储
        try {
            String encryptedPassword = AESUtil.encrypt(userRegisterDTO.getPassword(), aesUtil.getSecretKey());
            userRegisterDTO.setPassword(encryptedPassword);
            log.info("原始密码: {} -> 加密后: {}", "***", encryptedPassword);
        } catch (Exception e) {
            log.error("密码加密失败: {}", e.getMessage());
            return Result.fail("密码加密失败");
        }


        
        //参数校验
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
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
        //解密操作，因为是list集合，因此要遍历解密
        for (User user : list) {
            try {
                //不为null并且不为空字符串才解密
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    String decryptedEmail = AESUtil.decrypt(user.getEmail(), aesUtil.getSecretKey());
                    user.setEmail(decryptedEmail);
                }
                if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                    String decryptedPhone = AESUtil.decrypt(user.getPhone(), aesUtil.getSecretKey());
                    user.setPhone(decryptedPhone);
                }
                if (user.getIdentityCard() != null && !user.getIdentityCard().isEmpty()) {
                    String decryptedIdentityCard = AESUtil.decrypt(user.getIdentityCard(), aesUtil.getSecretKey());
                    user.setIdentityCard(decryptedIdentityCard);
                }
            } catch (Exception e) {
                log.error("用户{}信息解密失败: {}", user.getUsername(), e.getMessage());
            }
        }
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

    @PutMapping("/updateUserMessage")
    @ApiOperation(value = "更新用户信息")
    public Result updateUserMessage(@RequestBody UserMessageUpdateDTO userMessageUpdateDTO) {

        String email = userMessageUpdateDTO.getEmail();
        String phone = userMessageUpdateDTO.getPhone();
        String identityCard = userMessageUpdateDTO.getIdentityCard();

        String encryptedEmail = AESUtil.encrypt(email, aesUtil.getSecretKey());
        userMessageUpdateDTO.setEmail(encryptedEmail);
        log.info("原始邮箱: {} -> 加密后: {}", email, encryptedEmail);

        String encryptedPhone = AESUtil.encrypt(phone, aesUtil.getSecretKey());
        userMessageUpdateDTO.setPhone(encryptedPhone);
        log.info("原始手机号: {} -> 加密后: {}", phone, encryptedPhone);

        String encryptedIdentityCard = AESUtil.encrypt(identityCard, aesUtil.getSecretKey());
        userMessageUpdateDTO.setIdentityCard(encryptedIdentityCard);
        log.info("原始身份证: {} -> 加密后: {}", identityCard, encryptedIdentityCard);

        userService.updateUserMessage(userMessageUpdateDTO);
        log.info("更新用户信息成功: {}", userMessageUpdateDTO.getUsername());
        return Result.success();
    }



    



}