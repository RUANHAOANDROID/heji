package com.heji.server.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求日志拦截器
 */
@Slf4j
public class LogInInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       log.info("request :{}",request.getAuthType());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        log.info("postHandle-request-{}",request.toString());
        log.info("postHandle-response-{}",response.toString());
        log.info("postHandle-header-{}",handler.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("preHandle()\n request: {}\n response :{} \n handler:{}\n Exception:{}", request, response, handler,ex);
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle()\n request: {}\n response :{} \n handler:{}\n Exception:{}", request, response, handler);
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
