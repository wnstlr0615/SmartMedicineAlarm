package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode{
    EMAIL_IS_DUPLICATED("해당 이메일은 이미 등록되어 있습니다.", 400)
    ;
    private final String description;
    private final int status;

}
