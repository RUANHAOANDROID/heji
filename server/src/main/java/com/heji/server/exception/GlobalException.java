package com.heji.server.exception;

public class GlobalException extends RuntimeException {

    private Integer code = 500; //因为我需要将异常信息也返回给接口中，所以添加code区分

    public GlobalException(Integer code, String message) {
        super(message);  //把自定义的message传递个异常父类
        this.code = code;
    }

    public GlobalException(String message) {
        super(message);  //把自定义的message传递个异常父类
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}