package com.heji.server.exception;

public class UserException extends GlobalException{
    public UserException(Integer code, String message) {
        super(code, message);
    }

    public UserException(String message) {
        super(message);
    }
}
