package com.smrp.smartmedicinealarm.error.handler;

import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.error.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userExceptionHandler(UserException e, HttpServletRequest request){
        printLog(e, request);
        return ResponseEntity.status(
                e.getErrorCode().getStatus()
        ).body(
                ErrorResponse.CreateErrorResponse(e.getErrorCode())
        );
    }

    private void printLog(UserException e, HttpServletRequest request) {
        String uuid = (String) request.getAttribute("UUID");
        log.error("[{}] requestURL : {}",uuid, request.getRequestURL());
        log.error("[{}] errorCode : {}, errorMessage : {}",uuid, e.getErrorCode(), e.getErrorMessage());
    }
}
