package com.heji.server.exception;

import com.mongodb.client.internal.OperationExecutor;

public class OperationException extends  GlobalException{
    public OperationException(Integer code, String message) {
        super(code, message);
    }

    public OperationException(String message) {
        super(message);
    }
}
