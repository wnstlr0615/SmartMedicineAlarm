package com.smrp.smartmedicinealarm.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineCndLine {
    NONE("μμ"), MINUS("-"), PLUS("+"), OTHER("κΈ°ν");

    private final String description;
}