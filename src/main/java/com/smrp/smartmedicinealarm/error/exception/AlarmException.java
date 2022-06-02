package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.AlarmErrorCode;

public class AlarmException extends CustomRuntimeException {
    public AlarmException(AlarmErrorCode errorCode) {
        super(errorCode);
    }
    public AlarmException(AlarmErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
