package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import lombok.Getter;

@Getter
public abstract class CustomRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public CustomRuntimeException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public CustomRuntimeException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage =errorCode.getDescription();
    }
}
