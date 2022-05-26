package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineErrorCode implements ErrorCode{
    NOT_FOUND_MEDICINE("해당 id에 약품이 존재하지 않습니다.", 404);
    private final String description;
    private final int status;
}
