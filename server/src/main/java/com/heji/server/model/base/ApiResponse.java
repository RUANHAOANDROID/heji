package com.heji.server.model.base;

import com.google.gson.Gson;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ToString
public class ApiResponse<T>{

    /**
     * 成功
     *
     * @param msg  消息提示
     * @param data 数据
     * @return
     */
    public static String success(String msg, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(Status.SUCCESS.code());
        apiResponse.setMsg(msg);
        apiResponse.setData(data);
        return apiResponse.toJson();
    }

    /**
     * 成功
     *
     * @param data 数据
     * @return
     */
    public static String success(Object data) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(Status.SUCCESS.code());
        apiResponse.setMsg(Status.SUCCESS.msg());
        apiResponse.setData(data);
        return apiResponse.toJson();
    }

    /**
     * 失败
     **/
    public static String error(String msg) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(Status.FAIL.code());
        apiResponse.setMsg(msg);
        String errorStr = apiResponse.toJson();
        return errorStr;
    }

    /**
     * 失败
     **/
    public static String error(Integer code, String msg, Object data) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(code);
        apiResponse.setMsg(msg);
        apiResponse.setData(data);
        return apiResponse.toJson();
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
