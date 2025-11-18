package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.common.annotation.MultiLevelCache;
import org.example.common.model.Response;
import org.example.common.model.Result;
import org.example.common.util.AESUtil;
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

        //全表扫描后，再进行字段email的解密，匹配上了再解密其他的数据，然后返回
        List<User> allUsers = userMapper.selectAllUser();

        // 检查邮箱是否已注册
        User matchedUser = null;// 用于存储匹配的用户
        for (User user : allUsers) {
            // 解密数据库中的email密文
            String decryptedEmail = AESUtil.decrypt(user.getEmail(), aesUtil.getSecretKey());
            // 比对解密后的明文与用户输入的email
            if (email.equals(decryptedEmail)) {
                // 找到匹配用户，解密其他字段
                user.setEmail(decryptedEmail);
                user.setPhone(AESUtil.decrypt(user.getPhone(), aesUtil.getSecretKey()));
                user.setIdentityCard(AESUtil.decrypt(user.getIdentityCard(), aesUtil.getSecretKey()));
                matchedUser = user;
                break; // 找到后终止遍历，无需继续检查
            }
        }
        // 遍历完所有用户后再判断
        if (matchedUser == null) {
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

        //全表扫描后，再进行字段email的解密，匹配上了再解密其他的数据，然后返回
        List<User> allUsers = userMapper.selectAllUser();

        // 检查邮箱是否已注册
        User matchedUser = null;// 用于存储匹配的用户
        for (User user : allUsers) {
            // 解密数据库中的email密文
            String decryptedEmail = AESUtil.decrypt(user.getEmail(), aesUtil.getSecretKey());
            // 比对解密后的明文与用户输入的email
            if (email.equals(decryptedEmail)) {
                // 找到匹配用户，解密其他字段
                user.setEmail(decryptedEmail);
                user.setPhone(AESUtil.decrypt(user.getPhone(), aesUtil.getSecretKey()));
                user.setIdentityCard(AESUtil.decrypt(user.getIdentityCard(), aesUtil.getSecretKey()));
                matchedUser = user;
                break; // 找到后终止遍历，无需继续检查
            }
        }
        // 遍历完所有用户后再判断
        if (matchedUser == null) {
            log.warn("邮箱未注册：{}", email);
            return Result.fail(Response.ERROR_EMAIL_NOT_REGISTERED);
        }
        
        // 验证验证码
        boolean verified = emailService.verifyCode(email, code);
        if (verified) {
            log.info("用户邮箱验证码登录成功：{}", email);
            return Result.success(Response.SUCCESS_LOGIN, matchedUser);
        } else {
            log.warn("验证码错误或已过期，邮箱：{}", email);
            return Result.fail(Response.ERROR_VERIFICATION_CODE);
        }
    }
}
