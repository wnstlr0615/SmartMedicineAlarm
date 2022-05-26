package com.smrp.smartmedicinealarm.error.exception;

import com.smrp.smartmedicinealarm.error.code.MedicineErrorCode;
import lombok.Getter;

@Getter
public class MedicineException extends CustomRuntimeException{
    public MedicineException(MedicineErrorCode errorCode) {
        super(errorCode);
    }
    public MedicineException(MedicineErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
