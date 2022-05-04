package com.smrp.smartmedicinealarm.error.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@Builder
@AllArgsConstructor
public class FieldErrorDto {
    private final String field;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String rejectValue;

    public static FieldErrorDto fromFieldError(FieldError fieldError){
        Object rejectedValue = fieldError.getRejectedValue();
        String value = rejectedValue != null ? rejectedValue.toString() : null;
        return FieldErrorDto.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectValue(value)
                .build();
    }
}
