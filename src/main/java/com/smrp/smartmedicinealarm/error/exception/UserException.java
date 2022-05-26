package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends CustomRuntimeException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
    public UserException(UserErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
