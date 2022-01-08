package com.heji.server.result;

import com.google.gson.Gson;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ToString
public class Result<T>{

    /**
     * 成功
     *
     * @param msg  消息提示
     * @param data 数据
     * @return
     */
    public static String success(String msg, Object data) {
        Result result = new Result();
        result.setCode(Response.SUCCESS.code());
        result.setMsg(msg);
        result.setData(data);
        return result.toJson();
    }

    /**
     * 成功
     *
     * @param data 数据
     * @return
     */
    public static String success(Object data) {
        Result result = new Result();
        result.setCode(Response.SUCCESS.code());
        result.setMsg(Response.SUCCESS.msg());
        result.setData(data);
        return result.toJson();
    }

    /**
     * 失败
     **/
    public static String error(String msg) {
        Result result = new Result();
        result.setCode(Response.UNKNOWN_ERROR.code());
        result.setMsg(msg);
        String errorStr = result.toJson();
        return errorStr;
    }

    /**
     * 失败
     **/
    public static String error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result.toJson();
    }

    /**
     * 失败
     **/
    public static String error(Integer code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result.toJson();
    }


    /**
     * 失败
     *
     * @return
     */
    public static String error(Response response) {
        Result result = new Result();
        result.setCode(response.code());
        result.setMsg(response.msg());
        return result.toJson();
    }

    private Integer code;
    private String msg;
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public String toJson() {
//        if (data instanceof Iterable) {
//            Type usersType = new TypeToken<List<String>>() {
//            }.getType();
//            String content = new Gson().toJson(data, usersType);
//            data = (T) content;
//        }
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
