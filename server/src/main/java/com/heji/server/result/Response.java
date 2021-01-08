package com.heji.server.result;

public enum Response {
    //这里是可以自己定义的，方便与前端交互即可
    UNKNOWN_ERROR(-1, "未知错误"),
    SUCCESS(0, "成功"),

    USER_NOT_EXIST(-1, "用户不存在"),
    USER_IS_EXISTS(-1, "用户已存在"),

    DATA_IS_NULL(-1, "数据为空"),
    DATA_NOT_EXIST(-1, "数据不存在"),

    DATA_IS_EXIST(-1, "数据已存在"),
    FILE_IS_NULL(-1, "文件为空");
    private Integer code;
    private String msg;

    Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    Response(String msg) {
        this.code = -1;
        this.msg = msg;
    }

    public Integer code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
