package com.smrp.smartmedicinealarm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult extends RepresentationModel<ResponseResult> {
    private boolean result;
    private String message;
    private Object data;

    public static ResponseResult success(String message, Object data) {
        return ResponseResult.builder()
                .result(true)
                .message(message)
                .data(data)
                .build();
    }

    public static ResponseResult success(String message) {
        return success(message, null);
    }
}
