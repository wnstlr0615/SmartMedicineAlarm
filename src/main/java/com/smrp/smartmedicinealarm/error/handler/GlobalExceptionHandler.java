package com.smrp.smartmedicinealarm.error.handler;

import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;
import com.smrp.smartmedicinealarm.error.exception.CustomRuntimeException;
import com.smrp.smartmedicinealarm.error.exception.MedicineException;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.error.response.ErrorResponse;
import com.smrp.smartmedicinealarm.error.response.FieldErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UserException.class,
            MedicineException.class
    })
    private ResponseEntity<?> customRuntimeExceptionHandler(CustomRuntimeException e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request){
        log.debug("MethodArgumentNotValidException", e);
        return createErrorResponse(GlobalErrorCode.BAD_REQUEST, e.getAllErrors());
    }
    @ExceptionHandler({
            IllegalStateException.class,
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class
    })
    private ResponseEntity<?> BadRequestExceptionHandler(Exception e, HttpServletRequest request) {
        printLog(e, request);
        return createErrorResponse(GlobalErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> httpMediaTypeNotSupportedExceptionHandler(Exception e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedExceptionHandler(Exception e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(GlobalErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> exceptionHandler(Exception e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
    private ResponseEntity<?> createErrorResponse(ErrorCode errorCode, List<ObjectError> allErrors) {
        List<FieldErrorDto> fieldErrorDtos = getFieldErrorDtos(allErrors);
        return ResponseEntity.status(errorCode.getStatus())
                .body(
                        ErrorResponse.CreateErrorResponse(errorCode, fieldErrorDtos)
                );
    }

    private List<FieldErrorDto> getFieldErrorDtos(List<ObjectError> allErrors) {
        return allErrors.stream().filter(objectError -> objectError instanceof FieldError)
                .map(objectError -> (FieldError) objectError).map(FieldErrorDto::fromFieldError).collect(Collectors.toList());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(CustomRuntimeException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(
                ErrorResponse.CreateErrorResponse(e.getErrorCode())
        );
    }
    private ResponseEntity<?> createErrorResponse(ErrorCode errorCode) {
        return createErrorResponse(errorCode, errorCode.getDescription());
    }
    private ResponseEntity<?> createErrorResponse(ErrorCode errorCode, String errorMessage) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(
                        ErrorResponse.CreateErrorResponse(errorCode, errorMessage)
                );
    }


    private void printLog(Exception e, HttpServletRequest request) {
        String uuid = (String) request.getAttribute("UUID");
        log.error("[{}] requestURL : {}",uuid, request.getRequestURL());
        log.error("[{}] errorMessage : {}",uuid, e.getMessage());
        log.error("",e);
    }
}
