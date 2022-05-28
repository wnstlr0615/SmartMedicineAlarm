package com.smrp.smartmedicinealarm.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineErrorCode implements ErrorCode{
    NOT_FOUND_MEDICINE("해당 id에 약품이 존재하지 않습니다.", 404),
    ALREADY_REGISTER_MEDICINE("해당 약품은 이미 등록되어 있습니다.", 400);
    private final String description;
    private final int status;
}
