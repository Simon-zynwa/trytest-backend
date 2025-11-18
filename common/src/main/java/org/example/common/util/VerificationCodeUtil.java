package org.example.common.util;

import java.security.SecureRandom;

/**
 * 验证码生成工具类
 */
public class VerificationCodeUtil {
    
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * 生成指定长度的数字验证码
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return code.toString();
    }
    
    /**
     * 生成6位数字验证码（默认）
     * @return 6位验证码字符串
     */
    public static String generateCode() {
        return generateCode(6);
    }
}
