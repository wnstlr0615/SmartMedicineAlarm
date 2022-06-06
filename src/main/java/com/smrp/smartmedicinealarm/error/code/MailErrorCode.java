package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailErrorCode implements ErrorCode{
    WRONG_MAIL_TEXT("메일 텍스트 정보가 올바르지 않습니다.", 500),
    WRONG_MAIL_ATTRIBUTE("메일 속성 설정이 잘못되었습니다", 500),
    WRONG_MAIL_AUTHENTICATION("메일 인증 정보가 알맞지 않습니다.", 500),
    WRONG_INPUT_MAIL("올바르지 않은 이메일 입니다.", 400),

    NOT_FOUND_MAIL_TEMPLATE("해당 메일 템플릿이 없습니다.", 500)
    ;
    private final String description;
    private final int status;
}
