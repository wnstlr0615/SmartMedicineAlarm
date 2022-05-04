package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
