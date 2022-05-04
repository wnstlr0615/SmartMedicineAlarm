package com.smrp.smartmedicinealarm.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute("UUID", uuid);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
