package com.smrp.smartmedicinealarm.dto.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Relation(collectionRelation = "items")
public class SimpleAlarmDto extends RepresentationModel<SimpleAlarmDto> {
    @ApiModelProperty(value = "알랑 PK", example = "1L")
    private Long alarmId;

    @ApiModelProperty(value = "알랑 명", example = "감기약")
    private String title;

    @ApiModelProperty(value = "알람 사용 상태", example = "true")
    private Boolean used;

    @ApiModelProperty(value = "생성 일자", example = "2020-06-03")
    private LocalDate createdAt;

    public static SimpleAlarmDto createAlarmDetailDto(Long alarmId, String title, Boolean used,  LocalDate createAt) {
        Assert.notNull(alarmId, "alarmId must not be null");
        Assert.hasText(title, "title must not be null");
        Assert.notNull(used, "medicines must not be null");
        Assert.notNull(createAt, "createAt must not be null");

        return SimpleAlarmDto.builder()
                .alarmId(alarmId)
                .title(title)
                .used(used)
                .createdAt(createAt)
                .build();
    }

    public static SimpleAlarmDto fromEntity(Alarm alarm){
        boolean used = alarm.getLeftOverDoseCount() > 0;
        return createAlarmDetailDto(alarm.getAlarmId(), alarm.getTitle(), used, alarm.getCreateDate().toLocalDate());
    }
}
