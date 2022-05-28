package com.smrp.smartmedicinealarm.entity.medicine.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Builder
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MedicineColor {
    @Column(length = 100)
    private String colorFront;  //색깔(앞) colorClass1
    @Column(length = 100)
    private String colorBack;  //색깔(뒤) colorClass2

    //== 생성 메서드 ==//
    public static MedicineColor createMedicineColor(String colorFront, String colorBack){
        return MedicineColor.builder()
                .colorFront(colorFront)
                .colorBack(colorBack)
                .build();
    }

    //== 비즈니스 메서드 ==//
    public void update(String colorFront, String colorBack) {
        this.colorFront = colorFront != null ? colorFront : this.colorFront;
        this.colorBack = colorBack != null ? colorBack : this.colorBack;
    }
}
