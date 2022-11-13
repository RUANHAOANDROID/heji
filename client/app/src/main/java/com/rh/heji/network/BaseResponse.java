package com.rh.heji.network;

/**
 * @date: 2020/9/23
 *
 * @author: 锅得铁
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", date=" + data +
                '}';
    }

    public boolean success() {
        return code == 0;
    }
}
