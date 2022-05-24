package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode{
    INTERNAL_SERVER_ERROR("서버에 문제가 발생하였습니다.", 500),
    LOGIN_FAIL("로그인에 실패하였습니다.", 400),
    UNAUTHORIZED_ERROR("접근 권한이 없습니다.", 403),
    BAD_REQUEST(" 잘못된 요청 입니다.", 404),
    UNSUPPORTED_MEDIA_TYPE("지원하지 않는 미디어 타입 입니다..", 415),
    METHOD_NOT_ALLOWED("허용 되지 않은 메소드 요청입니다.", 405)
    ;
    private final String description;
    private final int status;
}
