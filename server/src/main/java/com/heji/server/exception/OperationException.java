package com.heji.server.exception;

/**
 * 操作错误
 */
public class OperationException extends  GlobalException{
    public OperationException(Integer code, String message) {
        super(code, message);
    }

    public OperationException(String message) {
        super(message);
    }
}
