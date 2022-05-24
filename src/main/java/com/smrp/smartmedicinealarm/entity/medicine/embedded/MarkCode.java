package com.smrp.smartmedicinealarm.entity.medicine.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Getter
@Builder
@ToString
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarkCode {
    /** 마크 코드 */
    @Column(length = 100)
    private String markCodeBackAnal; //마크내용(뒤)
    @Column(length = 100)
    private String markCodeFrontAnal; //마크내용(앞)
    @Column(length = 500)
    private String markCodeFrontImg; //마크이미지(앞)
    @Column(length = 500)
    private String markCodeBackImg;//마크이미지(뒤)
}