package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmErrorCode implements ErrorCode{
    FOUND_MEDICINES_SIZE_IS_ZERO("", 400),
    ;
    private final String description;
    private final int status;
}
