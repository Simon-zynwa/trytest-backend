//package org.example.common.util;
//
//
//import org.example.common.exception.AESException;
//import org.springframework.context.annotation.Configuration;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.GCMParameterSpec;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardOpenOption;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//@Configuration
//public class AESUtil {
//    private static final String DEFAULT_ALGO = "AES/CBC/PKCS5Padding";
//    private static final int GCM_TAG_LENGTH = 128; // GCM 模式下，认证标签长度（bit）
//    private static final int IV_LENGTH = 16;       // IV 长度（字节）
//    private static final SecureRandom RANDOM = new SecureRandom();
//
//
//    private AESUtil() {}
//    public static String generateKey(int keySize) {
//        try {
//            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(keySize, RANDOM);
//            SecretKey key = kg.generateKey();
//            return Base64.getEncoder().encodeToString(key.getEncoded());
//        } catch (Exception e) {
//            throw new AESException("AES 密钥生成失败", e);
//        }
//    }
//
//    /** 生成随机 IV，返回 Base64 编码 */
//    public static String generateIV() {
//        byte[] iv = new byte[IV_LENGTH];
//        RANDOM.nextBytes(iv);
//        return Base64.getEncoder().encodeToString(iv);
//    }
//
//    /** 默认模式加密（AES/CBC/PKCS5Padding），输出格式 IV:CipherText（Base64） */
//    public static String encrypt(String plaintext, String base64Key) {
//        String ivB64 = generateIV();
//        return encrypt(plaintext, base64Key, DEFAULT_ALGO, ivB64);
//    }
//
//    /** 全参数模式加密 */
//    public static String encrypt(String plaintext, String base64Key,
//                                 String algorithm, String base64IV) {
//        try {
//            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
//            byte[] ivBytes  = Base64.getDecoder().decode(base64IV);
//            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//            Cipher cipher = Cipher.getInstance(algorithm);
//            if (algorithm.contains("/GCM/")) {
//                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);
//                cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
//            } else {
//                IvParameterSpec spec = new IvParameterSpec(ivBytes);
//                cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
//            }
//
//            byte[] ct = cipher.doFinal(plaintext.getBytes("UTF-8"));
//            String ctB64 = Base64.getEncoder().encodeToString(ct);
//            return base64IV + ":" + ctB64;
//        } catch (Exception e) {
//            throw new AESException("AES 加密失败", e);
//        }
//    }
//
//    /** 默认模式解密，输入格式 IV:CipherText（Base64） */
//    public static String decrypt(String cipherText, String base64Key) {
//        return decrypt(cipherText, base64Key, DEFAULT_ALGO, null);
//    }
//
//    /** 全参数模式解密 */
//    public static String decrypt(String cipherText, String base64Key,
//                                 String algorithm, String overrideIV) {
//        try {
//            String[] parts = cipherText.split(":", 2);
//            String ivB64 = overrideIV != null ? overrideIV : parts[0];
//            String ctB64 = overrideIV != null ? parts[0] : parts[1];
//
//            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
//            byte[] ivBytes  = Base64.getDecoder().decode(ivB64);
//            byte[] ctBytes  = Base64.getDecoder().decode(ctB64);
//            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//            Cipher cipher = Cipher.getInstance(algorithm);
//            if (algorithm.contains("/GCM/")) {
//                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);
//                cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
//            } else {
//                IvParameterSpec spec = new IvParameterSpec(ivBytes);
//                cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
//            }
//
//            byte[] pt = cipher.doFinal(ctBytes);
//            return new String(pt, "UTF-8");
//        } catch (Exception e) {
//            throw new AESException("AES 解密失败", e);
//        }
//    }
//
//    /** 文件加密：读取 inPath，按默认模式加密后写入 outPath，内容为 IV:CipherText（Base64） */
//    public static void encrypt(Path inPath, Path outPath, String base64Key) {
//        try {
//            byte[] data = Files.readAllBytes(inPath);
//            String ct = encrypt(new String(data, "UTF-8"), base64Key);
//            Files.write(outPath, ct.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//        } catch (Exception e) {
//            throw new AESException("文件加密失败", e);
//        }
//    }
//
//    /** 文件解密：读取 inPath，按默认模式解密后写入 outPath */
//    public static void decrypt(Path inPath, Path outPath, String base64Key) {
//        try {
//            String ct = new String(Files.readAllBytes(inPath), "UTF-8");
//            String pt = decrypt(ct, base64Key);
//            Files.write(outPath, pt.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//        } catch (Exception e) {
//            throw new AESException("文件解密失败", e);
//        }
//    }
//
//
//
//
//
//}
