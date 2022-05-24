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
public class ItemName {
    @Column(length = 200, nullable = false)
    private String itemName; //품목명
    @Column(length = 200, nullable = false)
    private String itemEngName;  //제품 영문명

}
