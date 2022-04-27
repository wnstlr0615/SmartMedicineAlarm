package com.smrp.smartmedicinealarm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountStatus {
    USE("사용 중인 계정"),
    DELETED("삭제된 계정"),
    DORMANT("휴먼 계정")
    ;
    private final String description;
}
