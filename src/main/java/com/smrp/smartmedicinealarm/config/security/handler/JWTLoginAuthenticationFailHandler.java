package com.smrp.smartmedicinealarm.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;
import com.smrp.smartmedicinealarm.error.code.LoginErrorCode;
import com.smrp.smartmedicinealarm.error.exception.LoginException;
import com.smrp.smartmedicinealarm.error.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTLoginAuthenticationFailHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
        if(exception instanceof  LoginException){
            LoginException loginException=(LoginException)exception;
            log.error("LoginException - requestRemoteHost : {} , errorCode : {} , errorMessage : {}", request.getRequestURI(), loginException.getErrorCode(), loginException.getMessage());
            responseError(response, loginException.getErrorCode());
        }else if(exception instanceof UsernameNotFoundException){
            log.error("UsernameNotFoundException - requestRemoteHost : {}", request.getRemoteHost());
            responseError(response, LoginErrorCode.EMAIL_OR_PASSWORD_WRONG);
        } else if(exception instanceof BadCredentialsException){
            log.error("BadCredentialsException - requestRemoteHost : {}", request.getRemoteHost());
            responseError(response, LoginErrorCode.EMAIL_OR_PASSWORD_WRONG);
        }else{
            log.error("authenticationException - requestRemoteHost : {},  requestUrl: {}, errorMessage : {}",request.getRemoteHost(), request.getRequestURI(), exception.getMessage());
            responseError(response, GlobalErrorCode.LOGIN_FAIL);
        }
    }

    private void responseError(HttpServletResponse response,  ErrorCode errorCode) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(errorCode.getStatus());
        response.getWriter().write(
                mapper.writeValueAsString(
                        ErrorResponse.CreateErrorResponse(errorCode)
                )
        );
    }
}
