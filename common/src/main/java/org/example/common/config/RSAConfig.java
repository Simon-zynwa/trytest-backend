package org.example.common.config;

import org.example.common.util.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * RSA加密配置类
 * 用于管理RSA公钥和私钥
 */
@Configuration
public class RSAConfig {
    
    /**
     * RSA公钥，从配置文件读取
     */
    @Value("${rsa.public.key:}")
    private String publicKey;
    
    /**
     * RSA私钥，从配置文件读取
     */
    @Value("${rsa.private.key:}")
    private String privateKey;
    
    /**
     * 初始化密钥对
     * 如果配置文件中没有密钥，则自动生成
     */
    @PostConstruct
    public void init() {
        if ((publicKey == null || publicKey.isEmpty()) || 
            (privateKey == null || privateKey.isEmpty())) {
            // 生成RSA密钥对
            Map<String, String> keyPair = RSAUtil.generateKeyPair();
            publicKey = keyPair.get("publicKey");
            privateKey = keyPair.get("privateKey");
            
            System.out.println("=================================================");
            System.out.println("⚠️  警告：未配置RSA密钥对，已自动生成");
            System.out.println("请将以下密钥添加到 application.yml 中：");
            System.out.println("\nrsa:");
            System.out.println("  public:");
            System.out.println("    key: " + publicKey);
            System.out.println("  private:");
            System.out.println("    key: " + privateKey);
            System.out.println("=================================================");
        }
    }
    
    /**
     * 获取公钥
     */
    public String getPublicKey() {
        return publicKey;
    }
    
    /**
     * 获取私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }
}
