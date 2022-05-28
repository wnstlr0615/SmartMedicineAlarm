package com.smrp.smartmedicinealarm.entity.medicine.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassNoAndName {
    @Column(length = 10)
    private String classNo; //분류번호
    @Column(length = 50)
    private String className;  //분류명

    //== 생성 메서드 ==//
    public static ClassNoAndName createClassNoAndName(String classNo, String className) {
        return ClassNoAndName.builder()
                .classNo(classNo)
                .className(className)
                .build();
    }
    //== 비즈니스 메서드 ==//
    public void update(String classNo, String className){
        this.classNo = classNo != null ? classNo : this.classNo;
        this.className = className != null ? className : this.className;
    }
}