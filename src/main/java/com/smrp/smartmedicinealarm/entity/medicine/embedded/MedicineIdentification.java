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
public class MedicineIdentification {
    @Column(length = 50)
    private String printFront;// 표시(앞)
    @Column(length = 50)
    private String printBack;  //표시(뒤)
    @Column(length = 50)
    private String drugShape;  //의약품 모양
    @Column(length = 500)
    private String chart; // 성상
    @Column(length = 50)
    private String formCodeName; //제형코드이름

    public static MedicineIdentification createMedicineIdentification(String printFront, String printBack, String drugShape, String chart, String formCodeName) {
        return MedicineIdentification.builder()
                .chart(chart)
                .drugShape(drugShape)
                .printFront(printFront)
                .printBack(printBack)
                .formCodeName(formCodeName)
                .build();
    }
}
