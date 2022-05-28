package com.smrp.smartmedicinealarm.dto.medicine;

import com.querydsl.core.annotations.QueryProjection;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Relation(collectionRelation = "items")
public class SimpleMedicineDto extends RepresentationModel<SimpleMedicineDto> {

    @ApiModelProperty(value = "medicine_id", example = "1L")
    private Long medicineId;

    @ApiModelProperty(value = "품목 일련번호", example = "200502447")
    private Long itemSeq;

    @ApiModelProperty(value = "약품명", example = "쿨정")
    private String itemName;

    @ApiModelProperty(value = "이미지 URL", example = "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/1Mxwka5v0jL")
    private String itemImage;

    @ApiModelProperty(value = "전문/일반", example = "일반의약품")
    private String etcOtcName;

    @ApiModelProperty(value = "업체명", example = "알파제약(주)")
    private String entpName;



    //== 생성 메서드 ==//

    public SimpleMedicineDto(Medicine medicine){
        BeanUtils.copyProperties(medicine, this);
    }
    @QueryProjection
    public SimpleMedicineDto(Long medicineId, Long itemSeq, String itemName, String itemImage, String etcOtcName, String entpName) {
        this.medicineId = medicineId;
        this.itemSeq = itemSeq;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.etcOtcName = etcOtcName;
        this.entpName = entpName;
    }

    public static  SimpleMedicineDto createSimpleMedicineDto(Long medicineId, Long itemSeq, String itemName, String itemImage, String etcOtcName, String entpName) {
        return SimpleMedicineDto.builder()
                .medicineId (medicineId)
                .itemSeq (itemSeq)
                .itemName (itemName)
                .itemImage (itemImage)
                .etcOtcName (etcOtcName)
                .entpName (entpName)
                .build();
    }
}
