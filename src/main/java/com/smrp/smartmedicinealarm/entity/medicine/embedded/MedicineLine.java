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
public class MedicineLine {
    @Column(length = 3000)
    private String lineFront;  //분할선(앞)
    @Column(length = 3000)
    private String lineBack;  //분할선(뒤)

    //== 생성 메서드 ==//
    public static MedicineLine crateMedicineLine(String lineFront, String lineBack){
        return MedicineLine.builder()
                .lineFront(lineFront)
                .lineBack(lineBack)
                .build();
    }

    //== 비즈니스 메서드 ==//
    public void update(String lineFront, String lineBack) {
        this.lineFront = lineFront != null ? lineFront : this.lineFront;
        this.lineBack = lineBack != null ? lineBack : this.lineBack;
    }
}