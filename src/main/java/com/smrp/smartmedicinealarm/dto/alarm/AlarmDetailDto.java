package com.smrp.smartmedicinealarm.dto.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.MedicineAlarm;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmDetailDto extends RepresentationModel<AlarmDetailDto> {
    @ApiModelProperty(value = "알랑 PK", example = "1L")
    private Long alarmId;

    @ApiModelProperty(value = "알랑 명", example = "감기약")
    private String title;

    @ApiModelProperty(value = "약 복용 횟수", example = "9")
    private Integer doseCount;

    @ApiModelProperty(value = "사용자 이메일 정보", example = "joon@naver.com")
    private String email;

    @ApiModelProperty(value = "등록된 약 정보들", dataType = "List")
    private List<SimpleMedicineDto> medicines;

    @ApiModelProperty(value = "생성 일자", example = "2020-06-03")
    private LocalDate createdAt;



    public static AlarmDetailDto createAlarmDetailDto(Long alarmId, String title, Integer doseCount, String email, List<SimpleMedicineDto> medicines, LocalDate createAt) {
        Assert.notNull(alarmId, "alarmId must not be null");
        Assert.hasText(title, "title must not be null");
        Assert.notNull(doseCount, "doseCount must not be null");
        Assert.hasText(email, "email must not be null");
        Assert.notEmpty(medicines, "medicines must not be null");
        Assert.notNull(createAt, "createAt must not be null");

        return AlarmDetailDto.builder()
                .alarmId(alarmId)
                .title(title)
                .doseCount(doseCount)
                .email(email)
                .medicines(medicines)
                .createdAt(createAt)
                .build();
    }


    public static AlarmDetailDto fromEntity(Alarm alarm) {
        List<SimpleMedicineDto> simpleMedicineDtos = alarm.getMedicineAlarms().stream().map(MedicineAlarm::getMedicine).map(SimpleMedicineDto::fromEntity).toList();
        Long alarmId = alarm.getAlarmId();
        String title = alarm.getTitle();
        Integer doseCount = alarm.getDoseCount();
        String email = alarm.getAccount().getEmail();
        LocalDate createAt = alarm.getCreateDate().toLocalDate();
        return createAlarmDetailDto(alarmId, title, doseCount, email, simpleMedicineDtos, createAt);
    }
}
