package com.smrp.smartmedicinealarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveBookmarkDto extends RepresentationModel<RemoveBookmarkDto> {
    @ApiModelProperty(value =" 약 PK 리스트", dataType = "List", example = "1L")
    @NotEmpty(message = "medicineIds는 빌 수 없습니다.")
    List<Long> medicineIds;

    //== 생성메서드 ==//
    public static RemoveBookmarkDto createRemoveBookmarkDto(List<Long> medicineIds){
        return RemoveBookmarkDto.builder()
                .medicineIds(medicineIds)
                .build();
    }
}
