package org.example.service.impl;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.example.common.annotation.MultiLevelCache;
import org.example.common.model.Response;
import org.example.common.model.Result;
import org.example.common.util.AESUtil;
import org.example.framework.service.EmailService;
import org.example.framework.service.SmsService;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.*;
import org.example.pojo.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AESUtil aesUtil;

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

        //检测邮箱有无注册
        User user = userMapper.SelectByEmail(email);
        if (user == null){
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

        //检测邮箱有无注册
        User user = userMapper.SelectByEmail(email);
        if (user == null){
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

    @Override
    public User SelectByPhone(String phone) {
        return userMapper.SelectByPhone(phone);
    }

    @Override
    public Result importUserByExcel(MultipartFile file) {
        try {
            // 1. 解析Excel为用户列表
            List<User> userList = EasyExcel.read(file.getInputStream())
                    .head(User.class)
                    .sheet()
                    .doReadSync();

            // 2. 数据校验（示例：检查用户名是否重复）
            List<String> existUsernames = userMapper.selectUsernames(userList.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList()));
            if (!existUsernames.isEmpty()) {
                return Result.fail("以下用户名已存在：" + String.join(",", existUsernames));
            }

            // 3. 密码加密（与注册逻辑一致）
            userList.forEach(user -> {
                try {
                    user.setPassword(AESUtil.encrypt(user.getPassword(), aesUtil.getSecretKey()));
                } catch (Exception e) {
                    throw new RuntimeException("密码加密失败：" + user.getUsername());
                }
            });

            // 4. 批量入库
            int successCount = userMapper.batchInsert(userList);
            log.info("Excel导入完成，总条数：{}，成功入库：{}", userList.size(), successCount);
            return Result.success("导入成功，共" + successCount + "条数据");

        } catch (IOException e) {
            log.error("Excel文件解析失败", e);
            return Result.fail("文件解析失败，请检查文件格式");
        } catch (RuntimeException e) {
            log.error("导入处理失败", e);
            return Result.fail(e.getMessage());
        }
    }

    // 实现发送短信验证码方法
    @Override
    public Result sendSmsCode(SendSmsCodeDTO sendSmsCodeDTO) {
        String phone = sendSmsCodeDTO.getPhone();

        // 检测手机号有无注册
        User user = userMapper.SelectByPhone(phone);
        if (user == null) {
            log.warn("手机号未注册：{}", phone);
            return Result.fail(Response.ERROR_PHONE_NOT_REGISTERED);
        }

        // 发送验证码（模拟）
        boolean success = smsService.sendVerificationCode(phone);
        if (success) {
            log.info("短信验证码发送成功：{}", phone);
            return Result.success("验证码已发送到您的手机");
        } else {
            log.error("短信验证码发送失败：{}", phone);
            return Result.fail("验证码发送失败，请稍后重试");
        }
    }

    // 实现手机验证码登录方法
    @Override
    public Result loginByPhoneCode(UserLoginByPhoneCodeDTO userLoginByPhoneCodeDTO) {
        String phone = userLoginByPhoneCodeDTO.getPhone();
        String code = userLoginByPhoneCodeDTO.getCode();

        // 检测手机号有无注册
        User user = userMapper.SelectByPhone(phone);
        if (user == null) {
            log.warn("手机号未注册：{}", phone);
            return Result.fail(Response.ERROR_PHONE_NOT_REGISTERED);
        }

        // 验证验证码
        boolean verified = smsService.verifyCode(phone, code);
        if (verified) {
            log.info("用户手机验证码登录成功：{}", phone);
            return Result.success(Response.SUCCESS_LOGIN, user);
        } else {
            log.warn("验证码错误或已过期，手机号：{}", phone);
            return Result.fail(Response.ERROR_VERIFICATION_CODE);
        }
    }
}
