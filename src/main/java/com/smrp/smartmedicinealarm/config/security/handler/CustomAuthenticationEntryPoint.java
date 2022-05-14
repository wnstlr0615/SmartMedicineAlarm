package com.smrp.smartmedicinealarm.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.LoginException;
import com.smrp.smartmedicinealarm.error.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        log.error("requestRemoteHost : {},  requestUrl: {}, authenticationException - errorMessage : {}",request.getRemoteHost(), request.getRequestURI(), authException.getMessage());
        if(authException instanceof LoginException){
            responseError(response, ((LoginException) authException).getErrorCode());
        }else if(authException instanceof UsernameNotFoundException){
            responseError(response, UserErrorCode.NOT_FOUND_USER_ID);
        }else {
            responseError(response, GlobalErrorCode.UNAUTHORIZED_ERROR);
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
