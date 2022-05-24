package com.smrp.smartmedicinealarm.entity.medicine.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClassNoAndName {
    @Column(length = 10)
    private String classNo; //분류번호
    @Column(length = 50)
    private String className;  //분류명
}