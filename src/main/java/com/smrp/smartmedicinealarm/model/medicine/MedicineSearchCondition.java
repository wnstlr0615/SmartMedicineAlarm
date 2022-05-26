package com.smrp.smartmedicinealarm.model.medicine;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineSearchCondition {
    @ApiModelProperty(value = "약품명", example = "쿨정", dataType = "String")
    private String itemName;

    @ApiModelProperty(value = "약 모양", example = "타원형", dataType = "List<MedicineCndShape>"
            , allowEmptyValue = true, allowableValues = "${MedicineCndShape.values()}")
    private List<MedicineCndShape> shapes;

    @ApiModelProperty(value = "약 색상", example = "연두", dataType = "List<MedicineCndColor>")
    private List<MedicineCndColor> colors;

    @ApiModelProperty(value = "약 색상", example = "연두" , dataType = "List<MedicineCndLine>")
    private List<MedicineCndLine> lines;

    public static MedicineSearchCondition createMedicineSearchCondition(String itemName,
                            List<MedicineCndShape> shapes, List<MedicineCndColor> colors, List<MedicineCndLine> lines){
        return MedicineSearchCondition.builder()
                .itemName(itemName)
                .shapes(shapes)
                .colors(colors)
                .lines(lines)
                .build();
    }
}
