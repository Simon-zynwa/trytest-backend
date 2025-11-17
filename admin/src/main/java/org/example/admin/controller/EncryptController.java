package org.example.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.Result;
import org.example.common.util.AESUtil;
import org.example.common.util.HashUtil;
import org.example.common.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/encrypt")
@Api(tags = "密码加密解密接口") // Swagger 2 类注解
@Slf4j
public class EncryptController {
    @Autowired
    private AESUtil aesUtil;

    @Autowired
    private RSAUtil rsaUtil;

    // ======================== AES加密示例方法 ========================

    /**
     * 🎓 示例1：加密文本
     * 演示如何使用AESUtil加密字符串
     */
    @PostMapping("/aes/encrypt")
    @ApiOperation(value = "AES加密示例")
    public Result encryptDemo(@RequestParam String text) {
        try {
            // 1️⃣ 获取密钥（从工具类中获取）
            String key = aesUtil.getSecretKey();

            // 2️⃣ 调用加密方法
            String encrypted = AESUtil.encrypt(text, key);

            // 3️⃣ 加密结果格式：IV:密文 （都是Base64编码）
            log.info("原文: {} -> 密文: {}", text, encrypted);

            return Result.success(encrypted);
        } catch (Exception e) {
            log.error("加密失败: {}", e.getMessage());
            return Result.fail("加密失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例2：解密文本
     * 演示如何使用AESUtil解密字符串
     */
    @PostMapping("/aes/decrypt")
    @ApiOperation(value = "AES解密示例")
    public Result decryptDemo(@RequestParam String cipherText) {
        try {
            // 1️⃣ 获取密钥（必须和加密时使用的密钥相同）
            String key = aesUtil.getSecretKey();

            // 2️⃣ 调用解密方法
            String decrypted = AESUtil.decrypt(cipherText, key);

            log.info("密文: {} -> 原文: {}", cipherText, decrypted);

            return Result.success(decrypted);
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage());
            return Result.fail("解密失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例3：生成新密钥
     * 演示如何生成AES密钥（仅用于学习，生产环境密钥应该固定配置）
     */
    @GetMapping("/aes/generateKey")
    @ApiOperation(value = "生成AES密钥示例")
    public Result generateKeyDemo() {
        // 生成128位密钥
        String key128 = AESUtil.generateKey(128);
        // 生成256位密钥（更安全，推荐）
        String key256 = AESUtil.generateKey(256);

        log.info("生成128位密钥: {}", key128);
        log.info("生成256位密钥: {}", key256);

        // 使用Map返回多个值
        Map<String, String> result = new HashMap<>();
        result.put("key128", key128);
        result.put("key256", key256);
        result.put("currentKey", aesUtil.getSecretKey());

        return Result.success(result);
    }

    // ======================== RSA非对称加密示例 ========================

    /**
     * 🎓 示例4：生成RSA密钥对
     */
    @GetMapping("/rsa/generateKeyPair")
    @ApiOperation(value = "生成RSA密钥对示例")
    public Result generateRSAKeyPair() {
        Map<String, String> keyPair = RSAUtil.generateKeyPair();
        log.info("生成RSA密钥对成功");
        return Result.success(keyPair);
    }

    /**
     * 🎓 示例5：RSA公钥加密
     */
    @PostMapping("/rsa/encryptByPublic")
    @ApiOperation(value = "RSA公钥加密示例")
    public Result rsaEncryptByPublic(@RequestParam String text) {
        try {
            String publicKey = rsaUtil.getPublicKey();
            String encrypted = RSAUtil.encryptByPublicKey(text, publicKey);
            log.info("RSA公钥加密成功");
            return Result.success(encrypted);
        } catch (Exception e) {
            log.error("RSA公钥加密失败: {}", e.getMessage());
            return Result.fail("加密失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例6：RSA私钥解密
     */
    @PostMapping("/rsa/decryptByPrivate")
    @ApiOperation(value = "RSA私钥解密示例")
    public Result rsaDecryptByPrivate(@RequestParam String cipherText) {
        try {
            String privateKey = rsaUtil.getPrivateKey();
            String decrypted = RSAUtil.decryptByPrivateKey(cipherText, privateKey);
            log.info("RSA私钥解密成功");
            return Result.success(decrypted);
        } catch (Exception e) {
            log.error("RSA私钥解密失败: {}", e.getMessage());
            return Result.fail("解密失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例7：RSA数字签名
     */
    @PostMapping("/rsa/sign")
    @ApiOperation(value = "RSA数字签名示例")
    public Result rsaSign(@RequestParam String data) {
        try {
            String privateKey = rsaUtil.getPrivateKey();
            String signature = RSAUtil.sign(data, privateKey);
            log.info("RSA签名成功");

            Map<String, String> result = new HashMap<>();
            result.put("data", data);
            result.put("signature", signature);
            return Result.success(result);
        } catch (Exception e) {
            log.error("RSA签名失败: {}", e.getMessage());
            return Result.fail("签名失败: " + e.getMessage());
        }
    }

    /**
     * 🎓 示例8：RSA验证签名
     */
    @PostMapping("/rsa/verify")
    @ApiOperation(value = "RSA验证签名示例")
    public Result rsaVerify(@RequestParam String data, @RequestParam String signature) {
        try {
            String publicKey = rsaUtil.getPublicKey();
            boolean valid = RSAUtil.verify(data, signature, publicKey);
            log.info("RSA签名验证: {}", valid ? "通过" : "失败");

            Map<String, Object> result = new HashMap<>();
            result.put("valid", valid);
            result.put("message", valid ? "签名验证通过" : "签名验证失败");
            return Result.success(result);
        } catch (Exception e) {
            log.error("RSA签名验证失败: {}", e.getMessage());
            return Result.fail("验证失败: " + e.getMessage());
        }
    }

    // ======================== Hash单向加密示例 ========================

    /**
     * 🎓 示例9：MD5加密
     */
    @PostMapping("/hash/md5")
    @ApiOperation(value = "MD5加密示例")
    public Result hashMd5(@RequestParam String text) {
        String hash = HashUtil.md5(text);
        log.info("MD5加密: {} -> {}", text, hash);
        return Result.success(hash);
    }

    /**
     * 🎓 示例10：SHA-256加密
     */
    @PostMapping("/hash/sha256")
    @ApiOperation(value = "SHA-256加密示例")
    public Result hashSha256(@RequestParam String text) {
        String hash = HashUtil.sha256(text);
        log.info("SHA-256加密: {} -> {}", text, hash);
        return Result.success(hash);
    }

    /**
     * 🎓 示例11：密码加密（推荐方式）
     * 使用SHA-256+盐值，返回格式：盐值$哈希值
     */
    @PostMapping("/hash/encryptPassword")
    @ApiOperation(value = "密码加密示例（推荐）")
    public Result hashEncryptPassword(@RequestParam String password) {
        String encrypted = HashUtil.encryptPassword(password);
        log.info("密码加密成功（已加盐）");

        Map<String, String> result = new HashMap<>();
        result.put("originalPassword", "***");  // 不显示原密码
        result.put("encryptedPassword", encrypted);
        result.put("format", "盐值$哈希值");
        return Result.success(result);
    }

    /**
     * 🎓 示例12：验证密码
     */
    @PostMapping("/hash/verifyPassword")
    @ApiOperation(value = "验证密码示例")
    public Result hashVerifyPassword(@RequestParam String password, @RequestParam String storedHash) {
        boolean valid = HashUtil.verifyPassword(password, storedHash);
        log.info("密码验证: {}", valid ? "通过" : "失败");

        Map<String, Object> result = new HashMap<>();
        result.put("valid", valid);
        result.put("message", valid ? "密码正确" : "密码错误");
        return Result.success(result);
    }




}
