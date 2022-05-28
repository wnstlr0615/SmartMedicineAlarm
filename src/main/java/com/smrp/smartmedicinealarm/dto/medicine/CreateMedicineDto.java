package com.smrp.smartmedicinealarm.dto.medicine;

import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static com.smrp.smartmedicinealarm.entity.medicine.embedded.ClassNoAndName.createClassNoAndName;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.LengAndThick.createLengAndThick;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MarkCode.createMarkCode;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineColor.createMedicineColor;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineCompany.createMedicineCompany;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineDate.createMedicineDate;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineIdentification.createMedicineIdentification;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineLine.crateMedicineLine;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateMedicineDto {
    @NotNull(message = "약품 일련번호는 필수입니다.")
    @ApiModelProperty(value = "품목 일련 번호", example = "200611524")
    private Long itemSeq;

    @NotBlank(message = "약품명은 필수 입니다.")
    @ApiModelProperty(value = "약품명", example = "마도파정")
    private String itemName;

    @NotBlank(message = "약품 이미지는 필수입니다.")
    @ApiModelProperty(value = "큰제품 이미지 URL", example = "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149")
    private String itemImage;

    @NotBlank(message = "의약품 구분은 필 수 입니다.")
    @ApiModelProperty(value = "전문일반구분", example = "전문의약품")
    private String etcOtcName;

    /* 분류 */
    @NotBlank(message = "약품 분류 번호는 필수 입니다.")
    @ApiModelProperty(value = "약품 분류 번호", example = "01190")
    private String classNo;

    @NotBlank(message = "약품 분류 명은 필수 입니다..")
    @ApiModelProperty(value = "약품 분류명", example = "기타의 중추신경용약")
    private String className;

    /* 외형 치수 */
    @NotBlank(message = "크기(장축)는 필수 입니다.")
    @ApiModelProperty(value = "크기(장축)", example = "13.0")
    private String lengLong;

    @NotBlank(message = "크기(단축)은 필수 입니다.")
    @ApiModelProperty(value = "크기(단축)", example = "13.0")
    private String lengShort;

    @NotBlank(message = "크기(두께)는 필수입니다.")
    @ApiModelProperty(value = "크기(두께)", example = "3.5")
    private String thick;

    /* 업체 정보 */
    @NotNull(message = "업체 일련번호는 필수입니다.")
    @ApiModelProperty(value = "업체일련번호", example = "20161439")
    private Long entpSeq;

    @NotBlank(message = "업체명은 필수입니다.")
    @ApiModelProperty(value = "업체명", example = "(주)한국로슈")
    private String entpName;

    @ApiModelProperty(value = "표시(앞)", example = "RO분할선C분할선HE분할선마크분할선")
    private String printFront;// 표시(앞)

    @ApiModelProperty(value = "표시(뒤)", example = "십자분할선")
    private String printBack;

    @NotBlank(message = "의약품 모양은 필수 입니다.")
    @ApiModelProperty(value = "의약품 모양", example = "원형")
    private String drugShape;

    @NotBlank(message = "약품성상은 필수입니다.")
    @ApiModelProperty(value = "약품 성상", example = "십자눈금이 새겨져 있는 분홍색의 원형정제이다")
    private String chart;

    @NotBlank(message = "제형코드이름은 필수입니다.")
    @ApiModelProperty(value = "제형코드이름", example = "나정")
    private String formCodeName;

    /* 분할 선 */
    @ApiModelProperty(value = "분할선(앞)", example = "+")
    private String lineFront;

    @ApiModelProperty(value = "분할선(뒤)", example = "+")
    private String lineBack;

    /* 색상 */
    @ApiModelProperty(value = "색깔(앞면)", example = "분홍")
    private String colorFront;

    @ApiModelProperty(value = "색깔(뒷면)")
    private String colorBack;

    /* 마크 */
    @ApiModelProperty(value = "마크내용(앞)", example = "육각형")
    private String markCodeFrontAnal;

    @ApiModelProperty(value = "마크내용(뒤)")
    private String markCodeBackAnal;

    @ApiModelProperty(value = "마크이미지(앞)", example = "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147938640332900169")
    private String markCodeFrontImg;

    @ApiModelProperty(value = "마크이미지(뒤)")
    private String markCodeBackImg;

    @ApiModelProperty(value = "품목 허가 일지", example = "20061127")
    private LocalDate itemPermitDate;

    @NotNull
    @ApiModelProperty(value = "약학정보원 이미지 생성일", example = "20041222")
    private LocalDate imgRegistTs;

    @ApiModelProperty(value = "변경일자", example = "20200227")
    private LocalDate changeDate;


    //== 생성 메서드 ==//
    public static CreateMedicineDto createMedicineDto(Long itemSeq, String  itemName, String itemImage, String etcOtcName,
                                          ClassNoAndName classNoAndName, LengAndThick lengAndThick, MedicineCompany medicineCompany,
                                          MedicineIdentification medicineIdentification, MedicineLine medicineLine, MedicineColor medicineColor,
                                          MarkCode markCode, MedicineDate medicineDate
    ){
        return CreateMedicineDto.builder()
                .itemSeq(itemSeq)
                .itemName(itemName)
                .itemImage(itemImage)
                .etcOtcName(etcOtcName)
                .classNo(classNoAndName.getClassNo())
                .className(classNoAndName.getClassName())
                .lengLong(lengAndThick.getLengLong())
                .lengShort(lengAndThick.getLengShort())
                .thick(lengAndThick.getThick())
                .entpSeq(medicineCompany.getEntpSeq())
                .entpName(medicineCompany.getEntpName())
                .printFront(medicineIdentification.getPrintFront())
                .printBack(medicineIdentification.getPrintBack())
                .drugShape(medicineIdentification.getDrugShape())
                .chart(medicineIdentification.getChart())
                .formCodeName(medicineIdentification.getFormCodeName())
                .lineFront(medicineLine.getLineFront())
                .lineBack(medicineLine.getLineBack())
                .colorFront(medicineColor.getColorFront())
                .colorBack(medicineColor.getColorBack())
                .markCodeFrontAnal(markCode.getMarkCodeFrontAnal())
                .markCodeBackAnal(markCode.getMarkCodeBackAnal())
                .markCodeFrontImg(markCode.getMarkCodeFrontImg())
                .markCodeBackImg(markCode.getMarkCodeBackImg())
                .itemPermitDate(medicineDate.getItemPermitDate())
                .imgRegistTs(medicineDate.getImgRegistTs())
                .changeDate(medicineDate.getChangeDate())
                .build();
    }

    public Medicine toEntity() {
        ClassNoAndName classNoAndName = createClassNoAndName(classNo, className);
        LengAndThick lengAndThick = createLengAndThick(lengShort, lengLong, thick);
        MedicineCompany medicineCompany = createMedicineCompany(20161439L, entpName);
        MedicineIdentification medicineIdentification
                = createMedicineIdentification(printFront, printBack, drugShape, chart, formCodeName);
        MedicineLine medicineLine = crateMedicineLine(lineFront, lineBack);
        MedicineColor medicineColor = createMedicineColor(colorFront, colorBack);
        MarkCode markCode = createMarkCode(markCodeFrontAnal, markCodeBackAnal, markCodeFrontImg, markCodeBackImg);
        MedicineDate medicineDate = createMedicineDate(itemPermitDate, imgRegistTs, changeDate);
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
