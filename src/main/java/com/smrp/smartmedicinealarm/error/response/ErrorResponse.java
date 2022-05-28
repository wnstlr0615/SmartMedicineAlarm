package com.smrp.smartmedicinealarm.error.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final ErrorCode errorCode;
    private final String errorMessage;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldErrorDto> errors;

    public static ErrorResponse CreateErrorResponse(ErrorCode errorCode){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getDescription())
                .build();
    }
    public static ErrorResponse CreateErrorResponse(ErrorCode errorCode, String errorMessage){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public static ErrorResponse CreateErrorResponse(ErrorCode errorCode, List<FieldErrorDto> errors){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getDescription())
                .errors(errors)
                .build();
    }


}
