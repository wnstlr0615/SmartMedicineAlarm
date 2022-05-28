package com.smrp.smartmedicinealarm.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineCndLine {
    NONE("없음"), MINUS("-"), PLUS("+"), OTHER("기타");

    private final String description;
}