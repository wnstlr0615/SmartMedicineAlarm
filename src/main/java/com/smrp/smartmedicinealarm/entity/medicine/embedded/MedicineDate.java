package com.smrp.smartmedicinealarm.entity.medicine.embedded;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineDate {
    private LocalDate itemPermitDate; //품목 허가 일지
    private LocalDate imgRegistTs;  //약학정보원 이미지 생성일
    private LocalDate changeDate; //변경일자
    //== 생성 메서드 ==//
    public static MedicineDate createMedicineDate(LocalDate itemPermitDate, LocalDate imgRegistTs, LocalDate changeDate){
        return MedicineDate.builder()
                .itemPermitDate(itemPermitDate)
                .imgRegistTs(imgRegistTs)
                .changeDate(changeDate)
                .build();
    }
}
