package com.smrp.smartmedicinealarm.dto.medicine;

import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class MedicineDetailsDto extends RepresentationModel<MedicineDetailsDto> {
    @ApiModelProperty(value = "약품 고유번호 PK", example = "1L")
    private Long medicineId;

    @ApiModelProperty(value = "품목 일련 번호", example = "200611524")
    private Long itemSeq;

    @ApiModelProperty(value = "약품명", example = "마도파정")
    private String itemName;

    @ApiModelProperty(value = "큰제품 이미지 URL", example = "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149")
    private String itemImage;

    @ApiModelProperty(value = "전문일반구분", example = "전문의약품")
    private String etcOtcName;

    @ApiModelProperty(value = "약품 분류명", example = "기타의 중추신경용약")
    private String className;

    @ApiModelProperty(value = "크기(장축)", example = "13.0")
    private String lengLong;

    @ApiModelProperty(value = "크기(단축)", example = "13.0")
    private String lengShort;

    @ApiModelProperty(value = "크기(두께)", example = "3.5")
    private String thick;

    @ApiModelProperty(value = "업체명", example = "(주)한국로슈")
    private String entpName;

    @ApiModelProperty(value = "표시(앞)", example = "RO분할선C분할선HE분할선마크분할선")
    private String printFront;

    @ApiModelProperty(value = "표시(뒤)", example = "십자분할선")
    private String printBack;

    @ApiModelProperty(value = "의약품 모양", example = "원형")
    private String drugShape;

    @ApiModelProperty(value = "약품 성상", example = "십자눈금이 새겨져 있는 분홍색의 원형정제이다")
    private String chart;

    @ApiModelProperty(value = "제형코드이름", example = "나정")
    private String formCodeName;

    @ApiModelProperty(value = "분할선(앞)", example = "+")
    private String lineFront;

    @ApiModelProperty(value = "분할선(뒤)", example = "+")
    private String lineBack;

    @ApiModelProperty(value = "색깔(앞면)", example = "분홍")
    private String colorFront;

    @ApiModelProperty(value = "색깔(뒷면)")
    private String colorBack;

    public MedicineDetailsDto(Medicine medicine) {
        copyProperties(medicine, this);
        copyProperties(medicine.getMedicineColor(), this);
        copyProperties(medicine.getMedicineLine(), this);
        copyProperties(medicine.getMedicineIdentification(), this);
        copyProperties(medicine.getClassNoAndName(), this);
        copyProperties(medicine.getEtcOtcName(), this);
        copyProperties(medicine.getLengAndThick(), this);
        copyProperties(medicine.getMarkCode(), this);
        copyProperties(medicine.getMedicineCompany(), this);
        copyProperties(medicine.getMedicineDate(), this);
    }

    public static MedicineDetailsDto fromEntity(@NotNull Medicine medicine){
        Assert.notNull(medicine, "medicine must not null");
        return new MedicineDetailsDto(medicine);
    }
}
