package com.heji.server.exception;

/**
 * 认证错误
 */
public class AuthException extends GlobalException {
    public AuthException(Integer code, String message) {
        super(code, message);
    }

    public AuthException(String message) {
        super(message);
    }
}
