package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.common.annotation.MultiLevelCache;
import org.example.common.model.Response;
import org.example.common.model.Result;
import org.example.framework.service.EmailService;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.SendEmailCodeDTO;
import org.example.pojo.dto.UserLoginByEmailDTO;
import org.example.pojo.dto.UserMessageUpdateDTO;
import org.example.pojo.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailService emailService;

    @Override
    public User SelectByUsername(String username) {

        return userMapper.SelectByUsername(username);
    }

    @Override
    public void InsertUser(User user) {
        userMapper.InsertUser(user);
    }

    @Override
    public void redisTestAdd(User user) {
        redisTemplate.opsForValue().set(user.getUsername(), user);
    }

    @Override
    @MultiLevelCache(prefix = "", key = "allUsers", expireTime = 600)
    public List<User> selectAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    public void updateUserMessage(UserMessageUpdateDTO userMessageUpdateDTO) {
        userMapper.updateUserMessage(userMessageUpdateDTO);
    }
    
    @Override
    public User SelectByEmail(String email) {
        return userMapper.SelectByEmail(email);
    }
    
    @Override
    public Result sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO) {
        String email = sendEmailCodeDTO.getEmail();
        
        // 检查邮箱是否已注册
        User user = userMapper.SelectByEmail(email);
        if (user == null) {
            log.warn("邮箱未注册：{}", email);
            return Result.fail(Response.ERROR_EMAIL_NOT_REGISTERED);
        }
        
        // 发送验证码
        boolean success = emailService.sendVerificationCode(email);
        if (success) {
            log.info("验证码发送成功：{}", email);
            return Result.success(Response.SUCCESS_SEND_EMAIL_CODE);
        } else {
            log.error("验证码发送失败：{}", email);
            return Result.fail(Response.ERROR_SEND_EMAIL_CODE);
        }
    }
    
    @Override
    public Result loginByEmailCode(UserLoginByEmailDTO userLoginByEmailDTO) {
        String email = userLoginByEmailDTO.getEmail();
        String code = userLoginByEmailDTO.getCode();
        
        // 检查邮箱是否已注册
        User user = userMapper.SelectByEmail(email);
        if (user == null) {
            log.warn("邮箱未注册：{}", email);
            return Result.fail(Response.ERROR_EMAIL_NOT_REGISTERED);
        }
        
        // 验证验证码
        boolean verified = emailService.verifyCode(email, code);
        if (verified) {
            log.info("用户邮箱验证码登录成功：{}", email);
            return Result.success(Response.SUCCESS_LOGIN, user);
        } else {
            log.warn("验证码错误或已过期，邮箱：{}", email);
            return Result.fail(Response.ERROR_VERIFICATION_CODE);
        }
    }
}
