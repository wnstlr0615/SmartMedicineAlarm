package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import com.smrp.smartmedicinealarm.error.code.LoginErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class LoginException extends AuthenticationException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public LoginException(LoginErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
