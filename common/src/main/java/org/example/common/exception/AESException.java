package org.example.common.exception;

/**
 * 自定义运行时异常，封装 AES 加解密过程中的各种异常
 */
public class AESException extends RuntimeException {
    public AESException(String message, Throwable cause) {
        super(message, cause);
    }
}