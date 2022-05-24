package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode{
    INTERNAL_SERVER_ERROR("서버에 문제가 발생하였습니다.", 500),
    LOGIN_FAIL("로그인에 실패하였습니다.", 400),
    UNAUTHORIZED_ERROR("접근 권한이 없습니다.", 403)
    ;
    private final String description;
    private final int status;
}
