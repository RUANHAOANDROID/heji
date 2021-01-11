package com.heji.server.exception;

public class DeleteException extends GlobalException {
    public DeleteException(Integer code, String message) {
        super(code, message);
    }

    public DeleteException(String message) {
        super(message);
    }
}
