package org.example.common.config;

import org.example.common.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * AES加密配置类
 * 用于管理AES加密密钥
 */
@Configuration
public class AESConfig {
    
    /**
     * AES密钥，从配置文件读取
     * 如果没有配置，会在启动时自动生成
     */
    @Value("${aes.secret.key:}")
    private String secretKey;
    
    /**
     * 初始化密钥
     * 如果配置文件中没有密钥，则自动生成一个256位的密钥
     */
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            // 生成256位密钥（推荐使用256位，更安全）
            secretKey = AESUtil.generateKey(256);
            System.out.println("=================================================");
            System.out.println("⚠️  警告：未配置AES密钥，已自动生成密钥");
            System.out.println("请将以下密钥添加到 application.yml 或 application.properties 中：");
            System.out.println("aes.secret.key: " + secretKey);
            System.out.println("=================================================");
        }
    }
    
    /**
     * 获取密钥
     * @return Base64编码的密钥
     */
    public String getSecretKey() {
        return secretKey;
    }
}
