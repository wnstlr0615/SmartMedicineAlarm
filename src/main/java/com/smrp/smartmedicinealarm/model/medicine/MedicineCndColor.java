package com.smrp.smartmedicinealarm.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineCndColor {
    NONE("없음")
    ,YELLOWISH_GREEN("연두")
    ,YELLOW("노랑")
    ,BROWN("갈색")
    ,PINK("분홍")
    ,WHITE("하양")
    ,GREEN("초록")
    ,PURPLE("자주")
    ,BLUE("파랑")
    ,ORANGE("주황")
    ,BLACK("검정")
    ,RED("빨강")
    ,TURQUOISE("청록")
    , GRAY("회색")
    , TRANSPARENT("투명")
    , OTHER("기타");

    private final String description;
}
