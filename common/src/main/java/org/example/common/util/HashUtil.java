package org.example.common.util;

import org.example.common.exception.AESException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hash加密工具类（单向加密，不可逆）
 * 
 * 📚 核心概念：
 * - Hash是单向加密，无法解密，只能通过相同输入验证
 * - 适用场景：密码存储、数据完整性校验、数字指纹
 * 
 * 🔐 常见算法：
 * - MD5：已不安全，仅用于非安全场景（如文件校验）
 * - SHA-256：安全，通用哈希算法
 * - SHA-512：更安全，哈希值更长
 * - BCrypt：专门用于密码哈希（推荐用于密码存储）
 * 
 * ⚠️ 密码存储建议：使用BCrypt或Argon2，而不是简单的MD5/SHA
 */
public class HashUtil {
    
    private HashUtil() {}
    
    // ==================== MD5 ====================
    
    /**
     * MD5加密（不推荐用于密码，仅用于文件校验等）
     * 
     * @param input 输入字符串
     * @return 32位小写MD5值
     */
    public static String md5(String input) {
        return hash(input, "MD5");
    }
    
    /**
     * MD5加密（带盐值，增加安全性）
     * 
     * @param input 输入字符串
     * @param salt 盐值
     * @return 32位小写MD5值
     */
    public static String md5WithSalt(String input, String salt) {
        return md5(input + salt);
    }
    
    // ==================== SHA-256 ====================
    
    /**
     * SHA-256加密
     * 
     * @param input 输入字符串
     * @return 64位小写SHA-256值
     */
    public static String sha256(String input) {
        return hash(input, "SHA-256");
    }
    
    /**
     * SHA-256加密（带盐值）
     * 
     * @param input 输入字符串
     * @param salt 盐值
     * @return 64位小写SHA-256值
     */
    public static String sha256WithSalt(String input, String salt) {
        return sha256(input + salt);
    }
    
    // ==================== SHA-512 ====================
    
    /**
     * SHA-512加密
     * 
     * @param input 输入字符串
     * @return 128位小写SHA-512值
     */
    public static String sha512(String input) {
        return hash(input, "SHA-512");
    }
    
    /**
     * SHA-512加密（带盐值）
     * 
     * @param input 输入字符串
     * @param salt 盐值
     * @return 128位小写SHA-512值
     */
    public static String sha512WithSalt(String input, String salt) {
        return sha512(input + salt);
    }
    
    // ==================== 通用Hash方法 ====================
    
    /**
     * 通用Hash加密方法
     * 
     * @param input 输入字符串
     * @param algorithm 算法名称（MD5、SHA-256、SHA-512等）
     * @return 小写十六进制字符串
     */
    private static String hash(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new AESException(algorithm + " 加密失败", e);
        }
    }
    
    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // ==================== BCrypt风格加密（推荐用于密码） ====================
    
    /**
     * 生成随机盐值（用于密码加密）
     * 
     * @return Base64编码的盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 密码加密（SHA-256 + 盐值，推荐用于密码存储）
     * 返回格式：盐值$哈希值
     * 
     * @param password 原始密码
     * @return 盐值$哈希值
     */
    public static String encryptPassword(String password) {
        String salt = generateSalt();
        String hash = sha256WithSalt(password, salt);
        return salt + "$" + hash;
    }
    
    /**
     * 验证密码
     * 
     * @param password 用户输入的密码
     * @param storedHash 数据库存储的哈希值（格式：盐值$哈希值）
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$", 2);
            if (parts.length != 2) {
                return false;
            }
            String salt = parts[0];
            String expectedHash = parts[1];
            String actualHash = sha256WithSalt(password, salt);
            return expectedHash.equals(actualHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    // ==================== 文件校验相关 ====================
    
    /**
     * 验证输入是否匹配某个哈希值（用于数据完整性校验）
     * 
     * @param input 输入字符串
     * @param expectedHash 期望的哈希值
     * @param algorithm 算法（MD5、SHA-256、SHA-512）
     * @return 是否匹配
     */
    public static boolean verify(String input, String expectedHash, String algorithm) {
        String actualHash = hash(input, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash);
    }
    
    /**
     * 快速MD5校验
     */
    public static boolean verifyMd5(String input, String expectedMd5) {
        return verify(input, expectedMd5, "MD5");
    }
    
    /**
     * 快速SHA-256校验
     */
    public static boolean verifySha256(String input, String expectedSha256) {
        return verify(input, expectedSha256, "SHA-256");
    }
}
