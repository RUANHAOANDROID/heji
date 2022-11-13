package com.heji.server.exception;

import com.heji.server.model.base.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * 该拦截器会优先于 MyErrorAttributes
 * throw Exception -> ExceptionHandler -> ErrorAttributes
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 自定义的全局错误
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//Http状态码设置为500
    @ResponseBody  //异常信息返回给接口
    @ExceptionHandler(value = GlobalException.class)// 仅仅拦截value对应的异常
    public String handle(GlobalException e, HttpServletRequest request) {
        if (e instanceof GlobalException) {
            GlobalException ge = (GlobalException) e;
            return ApiResponse.error(ge.getCode(), ge.getMessage(), "error");
        }
        return ApiResponse.error(-1, e.getMessage(), "error");
    }

    /**
     * 业务上功能性错误
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(FeatureException.class)
    @ResponseBody
    public String exceptionHandler(HttpServletRequest request, FeatureException e) {
        String failed = ApiResponse.error(e.getMessage());
        return failed;
    }

    /**
     * 其他全局性错误
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exceptionHandler(HttpServletRequest request, Exception e) {
        String failed = ApiResponse.error(e.getMessage());
        return failed;
    }
}
