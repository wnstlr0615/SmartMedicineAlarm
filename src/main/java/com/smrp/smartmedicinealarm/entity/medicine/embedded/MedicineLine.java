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
    private String lineBack;  //분할선(뒤)
    @Column(length = 3000)
    private String lineFront;  //분할선(앞)
}