package org.example.framework.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.util.VerificationCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 邮件服务类
 * 负责发送验证码邮件
 */
@Service
@Slf4j
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    /**
     * 验证码在Redis中的key前缀
     */
    private static final String EMAIL_CODE_PREFIX = "email:code:";
    
    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String toEmail) {
        try {
            // 生成6位验证码
            String code = VerificationCodeUtil.generateCode();
            
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("【登录验证码】");
            message.setText(String.format(
                "您的登录验证码是：%s\n\n验证码有效期为%d分钟，请勿泄露给他人。\n\n如非本人操作，请忽略此邮件。",
                code, CODE_EXPIRE_MINUTES
            ));
            
            // 发送邮件
            mailSender.send(message);
            
            // 将验证码存储到Redis，设置过期时间
            String key = EMAIL_CODE_PREFIX + toEmail;
            redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            log.info("验证码邮件发送成功，收件人：{}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("验证码邮件发送失败，收件人：{}，错误信息：{}", toEmail, e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证邮箱验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            log.warn("验证码不存在或已过期，邮箱：{}", email);
            return false;
        }
        
        if (storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            log.info("验证码验证成功，邮箱：{}", email);
            return true;
        } else {
            log.warn("验证码错误，邮箱：{}", email);
            return false;
        }
    }
}
