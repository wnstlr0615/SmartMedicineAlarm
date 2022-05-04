package com.smrp.smartmedicinealarm.entity.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MAN("남자"), WOMAN("여자")
    ;
    private final String description;
}
