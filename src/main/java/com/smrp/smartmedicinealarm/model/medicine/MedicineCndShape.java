package com.smrp.smartmedicinealarm.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineCndShape {
    NONE("없음")
    , CIRCLE("원형")
    , LONG_RECTANGLE("장방형")
    , OVAL("타원형")
    , RECTANGLE("사각형")
    , OCTAGON("팔각형")
    , PENTAGON("오각형")
    , TRIANGLE("삼각형")
    , RHOMBUS("마름모형")
    , HEXAGON("육각형")
    , SEMICIRCULAR("반원형")
    , OTHER("기타")
    ;

    private final String description;

}