package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginErrorCode implements ErrorCode{
    WRONG_LOGIN_FORM("로그인 요청이 올바르지 않습니다.", 400),
    EMAIL_OR_PASSWORD_WRONG("이메일 또는 패스워드가 잘못 입력되었습니다.", 400),

    //토큰 관련 에러
    TOKEN_IS_EXPIRED_OR_WRONG("토큰이 만료되었거나 잘못된 토큰을 입력하였습니다.", 400)
    ;
    private final String description;
    private final int status;
}
