package com.smrp.smartmedicinealarm.dto.alarm;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAlarmDto extends RepresentationModel<AlarmDetailDto> {
    @ApiModelProperty(value = "알랑 명", example = "감기약")
    @NotBlank(message = "알람 명은 필수입니다.")
    private String title;

    @ApiModelProperty(value = "약 복용 횟수", example = "9")
    @Max(value = 100, message = "약 복용 횟수는 최대 100번 이하입니다.")
    @Min(value = 1, message = "약 복용 횟수는 최소 1번 이상입니다.")
    private Integer doseCount;

    @NotEmpty(message = "약 선택은 필 수 입니다.")@ApiModelProperty(value = "약 PK 리스트", dataType = "List")
    private List<Long> medicineIds;

    //== 생성 메서드 ==//
    public static UpdateAlarmDto createUpdateAlarmDto(String title, Integer doseCount, List<Long> medicineIds){
        Assert.hasText(title, "title must not be null");
        Assert.notNull(doseCount, "doseCount must not be null");
        Assert.notEmpty(medicineIds, "medicineIds must not be null");
        return UpdateAlarmDto.builder()
                .title(title)
                .doseCount(doseCount)
                .medicineIds(medicineIds)
                .build();
    }
}
