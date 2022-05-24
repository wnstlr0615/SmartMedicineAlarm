package com.smrp.smartmedicinealarm.entity.medicine;

import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medicine {
    @Id
    @GeneratedValue
    private Long medicineId; // PK

    @Column(length = 3000, nullable = false, unique = true)
    private Long itemSeq;   //품목 일련 번호(FK, unique)

    /** 제품명 */
    @Embedded
    private ItemName itemName;

    @Column(length = 30)
    private String etcOtcName; //전문/일반
    @Column(length = 50)
    private String formCodeName; //제형코드이름
    @Column(length = 3000)
    private String itemImage; // 큰제품 이미지
//    @Column(length = 3000)
//    private String ediCode;  //보험코드


    @Column(length = 3000)
    private String chart; // 성상

    /** 분류 */
    @Embedded
    private ClassNoAndName classNoAndName;

    /**date */
    private LocalDate itemPermitDate; //품목 허가 일지
    private LocalDate imgRegistTs;  //약학정보원 이미지 생성일
    private LocalDate changeDate; //변경일자

    /** 외형 치수 */
    @Embedded
    private LengAndThick lengAndThick;

    /** 업체 정보 */
    @Embedded
    private MedicineCompany medicineCompany;

    /** 분할 선 */
    @Embedded
    private MedicineLine medicineLine;

    /** 색상 */
    @Embedded
    private MedicineColor medicineColor;

    /** 표시 */

    @Column(length = 50)
    private String printFront;// 표시(앞)
    @Column(length = 50)
    private String printBack;  //표시(뒤)
    @Column(length = 50)
    private String drugShape;  //의약품모양

    @Embedded
    private MarkCode markCode;
}
