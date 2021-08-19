package com.heji.server.exception;

public class OperationsException extends  GlobalException{
    public OperationsException(Integer code, String message) {
        super(code, message);
    }

    public OperationsException(String message) {
        super(message);
    }
}
