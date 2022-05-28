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

    //== 생성 메서드 ==//
    public static MarkCode createMarkCode(String markCodeFrontAnal, String markCodeBackAnal, String markCodeFrontImg  ,String markCodeBackImg){
        return MarkCode.builder()
                .markCodeFrontAnal(markCodeFrontAnal)
                .markCodeBackAnal(markCodeBackAnal)
                .markCodeFrontImg(markCodeFrontImg)
                .markCodeBackImg(markCodeBackImg)
                .build();
    }

    //== 비즈니스 메서드 ==//
    public void update(String markCodeBackAnal, String markCodeFrontAnal, String markCodeFrontImg, String markCodeBackImg) {
        this.markCodeBackAnal = markCodeBackAnal != null ? markCodeBackAnal : this.markCodeBackAnal;
        this.markCodeFrontAnal = markCodeFrontAnal != null ? markCodeFrontAnal : this.markCodeFrontAnal;
        this.markCodeFrontImg = markCodeFrontImg != null ? markCodeFrontImg : this.markCodeFrontImg;
        this.markCodeBackImg = markCodeBackImg != null ? markCodeBackImg : this.markCodeBackImg;
    }
}