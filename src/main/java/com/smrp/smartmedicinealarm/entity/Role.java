package com.smrp.smartmedicinealarm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    NORMAL("일반 계정"), ADMIN("관리자 계정")
    ;
    private final String description;
}
