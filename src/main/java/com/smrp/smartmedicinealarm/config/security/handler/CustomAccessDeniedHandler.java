package com.smrp.smartmedicinealarm.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;
import com.smrp.smartmedicinealarm.error.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("accessDeniedException - requestURL :{}, authentication : {}", request.getRequestURL(), authentication.getName());
        log.info("{}",accessDeniedException);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(
                mapper.writeValueAsString(
                        ErrorResponse.CreateErrorResponse(GlobalErrorCode.ACCESS_DENIED)
                )
        );
    }
}
