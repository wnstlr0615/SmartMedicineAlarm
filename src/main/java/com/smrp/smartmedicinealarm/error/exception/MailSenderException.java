package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.MailErrorCode;

public class MailSenderException extends CustomRuntimeException{


    public MailSenderException(MailErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public MailSenderException(MailErrorCode errorCode) {
        super(errorCode);
    }
}
