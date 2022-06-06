package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;

public class GlobalException extends CustomRuntimeException{
    public GlobalException(GlobalErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public GlobalException(GlobalErrorCode errorCode) {
        super(errorCode);
    }

    public GlobalException(GlobalErrorCode errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }
}
