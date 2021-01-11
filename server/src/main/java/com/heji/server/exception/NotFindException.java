package com.heji.server.exception;

public class NotFindException extends GlobalException {
    public NotFindException(Integer code, String message) {
        super(code, message);
    }

    public NotFindException(String message) {
        super(message);
    }
}
