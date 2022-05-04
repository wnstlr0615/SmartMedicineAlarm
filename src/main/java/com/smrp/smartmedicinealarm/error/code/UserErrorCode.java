package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode{
    EMAIL_IS_DUPLICATED("해당 이메일은 이미 등록되어 있습니다.", 400),
    NOT_FOUND_USER_ID("해당 ID로 된 회원을 찾지 못했습니다.", 404),
    ALREADY_DELETED_ACCOUNT("이미 삭제된 계정 입니다.", 400)
    ;
    private final String description;
    private final int status;

}
