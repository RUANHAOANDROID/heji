package com.rh.heji.network;

/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * #
 */
public class BaseResponse<T> {


    /**
     * code : 0
     * msg : 成功
     * data : 7
     */

    public int code;
    public String msg;
    public T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getDate() {
        return data;
    }

    public void setDate(T date) {
        this.data = date;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", date=" + data +
                '}';
    }
}
