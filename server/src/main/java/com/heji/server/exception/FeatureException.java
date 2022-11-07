package com.heji.server.exception;

public class FeatureException extends GlobalException {

    public FeatureException(Integer code, String message) {
        super(code, message);
    }

    public FeatureException(String message) {
        super(message);
    }

}
