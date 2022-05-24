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
public class MedicineCompany {
    @Column(length = 100)
    private Long entpSeq;   //업체일련번호
    @Column(length = 100)
    private String entpName;   //업체명

    //== 생성 메서드 ==//
    public static MedicineCompany createMedicineCompany(Long entpSeq, String entpName) {
        return MedicineCompany.builder()
                .entpSeq(entpSeq)
                .entpName(entpName)
                .build();
    }
}