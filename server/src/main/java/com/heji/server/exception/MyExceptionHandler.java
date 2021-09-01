package com.heji.server.exception;

import com.heji.server.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * 该拦截器会优先于 MyErrorAttributes
 * throw Exception -> ExceptionHandler -> ErrorAttributes
 *
 */
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    //处理自定义的业务异常
    @ResponseBody  //异常信息返回给接口
    @ExceptionHandler(value = GlobalException.class)// 仅仅拦截value对应的异常
    public String handle(Exception e,HttpServletRequest request) {
        if (e instanceof GlobalException) {
            GlobalException ge = (GlobalException) e;
            return Result.error(ge.getCode(), ge.getMessage(),"error");
        }
        return Result.error(-1, e.getMessage(),"error");
    }


    @ResponseBody
    @ExceptionHandler
    public String testError(ArithmeticException e, HttpServletRequest request) {
        log.error("出现了ArithmeticException异常", e);
        request.setAttribute("javax.servlet.error.status_code", 500);
        request.setAttribute("code", 66);
        request.setAttribute("message", "出现了ArithmeticException异常");
        return "forward:/error";
    }

    private String generateErrorInfo(int code, String message) {
        return generateErrorInfo(code, message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * 生成错误信息, 放到 request 域中.
     *
     * @param code       错误码
     * @param message    错误信息
     * @param httpStatus HTTP 状态码
     * @return SpringBoot 默认提供的 /error Controller 处理器
     */
    private String generateErrorInfo(int code, String message, int httpStatus) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("code", code);
        request.setAttribute("message", message);
        request.setAttribute("javax.servlet.error.status_code", httpStatus);
        return "forward:/error";
    }

}
