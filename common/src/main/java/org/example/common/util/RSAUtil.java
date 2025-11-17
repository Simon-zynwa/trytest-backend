package org.example.common.util;

import org.example.common.exception.AESException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA非对称加密工具类
 * 支持配置文件注入密钥对，也支持静态方法调用
 * 
 * 📚 核心概念：
 * - 公钥(PublicKey)：用于加密，可以公开
 * - 私钥(PrivateKey)：用于解密，必须保密
 * - 使用场景：数字签名、密钥交换、小数据加密
 * 
 * ⚠️ 注意：RSA加密速度慢，一般只用于加密小数据（如密钥、签名）
 */
@Configuration
public class RSAUtil {
    
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048; // 密钥长度，推荐2048或4096
    
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
            Map<String, String> keyPair = generateKeyPair();
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
    
    /**
     * 生成RSA密钥对（公钥+私钥）
     * 
     * @return Map包含publicKey和privateKey（Base64编码）
     */
    public static Map<String, String> generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();
            
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            
            Map<String, String> keys = new HashMap<>();
            keys.put("publicKey", publicKey);
            keys.put("privateKey", privateKey);
            return keys;
        } catch (Exception e) {
            throw new AESException("RSA密钥对生成失败", e);
        }
    }
    
    /**
     * 使用公钥加密
     * 
     * @param plaintext 明文
     * @param base64PublicKey Base64编码的公钥
     * @return Base64编码的密文
     */
    public static String encryptByPublicKey(String plaintext, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
            
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new AESException("RSA公钥加密失败", e);
        }
    }
    
    /**
     * 使用私钥解密
     * 
     * @param cipherText Base64编码的密文
     * @param base64PrivateKey Base64编码的私钥
     * @return 明文
     */
    public static String decryptByPrivateKey(String cipherText, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new AESException("RSA私钥解密失败", e);
        }
    }
    
    /**
     * 使用私钥加密（用于数字签名场景）
     * 
     * @param plaintext 明文
     * @param base64PrivateKey Base64编码的私钥
     * @return Base64编码的密文
     */
    public static String encryptByPrivateKey(String plaintext, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
            
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new AESException("RSA私钥加密失败", e);
        }
    }
    
    /**
     * 使用公钥解密（用于验证数字签名场景）
     * 
     * @param cipherText Base64编码的密文
     * @param base64PublicKey Base64编码的公钥
     * @return 明文
     */
    public static String decryptByPublicKey(String cipherText, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new AESException("RSA公钥解密失败", e);
        }
    }
    
    /**
     * 数字签名（使用私钥签名）
     * 
     * @param data 原始数据
     * @param base64PrivateKey Base64编码的私钥
     * @return Base64编码的签名
     */
    public static String sign(String data, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes("UTF-8"));
            
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new AESException("RSA签名失败", e);
        }
    }
    
    /**
     * 验证签名（使用公钥验证）
     * 
     * @param data 原始数据
     * @param base64Signature Base64编码的签名
     * @param base64PublicKey Base64编码的公钥
     * @return 验证是否通过
     */
    public static boolean verify(String data, String base64Signature, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes("UTF-8"));
            
            return signature.verify(Base64.getDecoder().decode(base64Signature));
        } catch (Exception e) {
            throw new AESException("RSA签名验证失败", e);
        }
    }
}
