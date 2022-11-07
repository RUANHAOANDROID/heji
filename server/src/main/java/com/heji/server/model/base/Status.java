package com.heji.server.model.base;

public enum Status {
    //这里是可以自己定义的，方便与前端交互即可
    FAIL(1, "failed"),
    SUCCESS(0, "成功");
    private Integer code;
    private String msg;

    Status(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    Status(String msg) {
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
