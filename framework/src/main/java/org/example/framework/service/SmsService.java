package org.example.framework.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.util.VerificationCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 短信服务类（模拟）
 * 负责发送验证码短信
 */
@Service
@Slf4j
public class SmsService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 验证码在Redis中的key前缀
     */
    private static final String SMS_CODE_PREFIX = "sms:code:";
    
    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 发送短信验证码（模拟）
     * @param phone 手机号
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String phone) {
        try {
            // 生成6位验证码
            String code = VerificationCodeUtil.generateCode();
            
            // 【模拟】打印验证码到控制台（实际应调用第三方短信平台API）
            log.info("========================================");
            log.info("【模拟短信】发送给手机号：{}", phone);
            log.info("【模拟短信】验证码内容：您的登录验证码是：{}", code);
            log.info("【模拟短信】验证码有效期：{}分钟", CODE_EXPIRE_MINUTES);
            log.info("========================================");
            
            // 将验证码存储到Redis，设置过期时间
            String key = SMS_CODE_PREFIX + phone;
            redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            log.info("短信验证码已生成并存储到Redis，手机号：{}", phone);
            return true;
        } catch (Exception e) {
            log.error("短信验证码发送失败，手机号：{}，错误信息：{}", phone, e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证手机验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        String key = SMS_CODE_PREFIX + phone;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            log.warn("验证码不存在或已过期，手机号：{}", phone);
            return false;
        }
        
        if (storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            log.info("验证码验证成功，手机号：{}", phone);
            return true;
        } else {
            log.warn("验证码错误，手机号：{}", phone);
            return false;
        }
    }
}