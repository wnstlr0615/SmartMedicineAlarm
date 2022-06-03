package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmErrorCode implements ErrorCode{
    FOUND_MEDICINES_SIZE_IS_ZERO("알람 내에 약 목록이 비어있습니다. ", 400),
    NOF_FOUND_ALARM_ID("알람 목록을 찾을 수 없습니다.", 404),
    ACCESS_DENIED_ALARM("알람 접근 권한이 없습니다.", 403),
    ;
    private final String description;
    private final int status;
}
