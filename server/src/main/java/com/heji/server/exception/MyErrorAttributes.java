package com.heji.server.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 错误属性处理
 * 重写 DefaultErrorAttributes 把错误统一转换成自己的格式
 * 业务上的操作错误由GlobalExceptionHandler 来处理并返回
 * error response {code msg data}
 */
@Component
@Slf4j
public class MyErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        //全部信息
       ErrorAttributeOptions myOptions = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.BINDING_ERRORS,
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.MESSAGE);
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, myOptions);
        //自己更改后的信息
        Map<String, Object> myErrorAttributes = new LinkedHashMap<>();
        if (errorAttributes.containsKey("status")) {
            myErrorAttributes.put("code", errorAttributes.get("status"));
        }
        if (errorAttributes.containsKey("error")) {
            myErrorAttributes.put("msg", errorAttributes.get("error"));
            //log.error("error:{}",errorAttributes.get("error"));
        }
        if (errorAttributes.containsKey("message")) {
            myErrorAttributes.put("data", errorAttributes.get("message"));
            //log.error("message:{}",errorAttributes.get("message"));
        }
//        if (errorAttributes.containsKey("path")){
//            myErrorAttributes.put("status",errorAttributes.get("path"));
//        }
//        if (errorAttributes.containsKey("timestamp")){
//            myErrorAttributes.put("timestamp",errorAttributes.get("timestamp"));
//        }


        return myErrorAttributes;
    }
}
