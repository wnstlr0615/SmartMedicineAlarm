package com.smrp.smartmedicinealarm.entity.medicine;

import com.smrp.smartmedicinealarm.entity.BaseTimeEntity;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medicine extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicineId; // PK

    @Column(length = 100, nullable = false)
    private Long itemSeq;   //품목 일련 번호(FK, unique)

    @Column(length = 200, nullable = false)
    private String itemName; //품목명

    @Column(length = 500)
    private String itemImage; // 큰제품 이미지

    @Column(length = 100)
    private String etcOtcName; //전문/일반

    /* 분류 */
    @Embedded
    private ClassNoAndName classNoAndName;

    /* 외형 치수 */
    @Embedded
    private LengAndThick lengAndThick;

    /* 업체 정보 */
    @Embedded
    private MedicineCompany medicineCompany;

    //약품 식별
    @Embedded
    private MedicineIdentification medicineIdentification;

    /* 분할 선 */
    @Embedded
    private MedicineLine medicineLine;

    /* 색상 */
    @Embedded
    private MedicineColor medicineColor;
    /* 마크 */
    @Embedded
    private MarkCode markCode;

    @Embedded
    private MedicineDate medicineDate;

    //== 생성 메서드 ==//
    public static Medicine createMedicine(Long medicineId, Long itemSeq, String  itemName, String itemImage, String etcOtcName,
                    ClassNoAndName classNoAndName, LengAndThick lengAndThick, MedicineCompany medicineCompany,
                    MedicineIdentification medicineIdentification, MedicineLine medicineLine, MedicineColor medicineColor,
                    MarkCode markCode, MedicineDate medicineDate) {
        return Medicine.builder()
                    .medicineId(medicineId)
                    .itemSeq(itemSeq)
                    .etcOtcName(etcOtcName)
                    .itemImage(itemImage)
                    .itemName(itemName)
                    .classNoAndName(classNoAndName)
                    .lengAndThick(lengAndThick)
                    .medicineCompany(medicineCompany)
                    .medicineIdentification(medicineIdentification)
                    .medicineLine(medicineLine)
                    .medicineColor(medicineColor)
                    .markCode(markCode)
                    .medicineDate(medicineDate)
                  . build();
    }
    public static Medicine createMedicine(Long itemSeq, String  itemName, String itemImage, String etcOtcName,
                                          ClassNoAndName classNoAndName, LengAndThick lengAndThick, MedicineCompany medicineCompany,
                                          MedicineIdentification medicineIdentification, MedicineLine medicineLine, MedicineColor medicineColor,
                                          MarkCode markCode, MedicineDate medicineDate
    ) {
        return Medicine.builder()
                .itemSeq(itemSeq)
                .etcOtcName(etcOtcName)
                .itemImage(itemImage)
                .itemName(itemName)
                .classNoAndName(classNoAndName)
                .lengAndThick(lengAndThick)
                .medicineCompany(medicineCompany)
                .medicineIdentification(medicineIdentification)
                .medicineLine(medicineLine)
                .medicineColor(medicineColor)
                .markCode(markCode)
                .medicineDate(medicineDate)
                . build();
    }

}
