package com.heji.server.exception;

public class NotFoundException extends GlobalException {
    public NotFoundException(Integer code, String message) {
        super(code, message);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
