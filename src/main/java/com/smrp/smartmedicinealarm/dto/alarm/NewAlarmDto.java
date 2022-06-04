package com.smrp.smartmedicinealarm.dto.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.MedicineAlarm;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

import javax.validation.constraints.*;
import java.util.List;

public class NewAlarmDto {
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Request{

        @NotBlank(message = "알람 명은 필수입니다.")
        @ApiModelProperty(value = "알랑 명", example = "감기약")
        private String title;

        @NotNull(message = " 복용횟수는 필수 입니다.")
        @Max(value = 100, message = "약 복용 횟수는 최대 100번 이하입니다.")
        @Min(value = 1, message = "약 복용 횟수는 최소 1번 이상입니다.")
        @ApiModelProperty(value = "약 복용 횟수", example = "9")
        private Integer doseCount;

        @NotEmpty(message = "약 선택은 필 수 입니다.")
        @ApiModelProperty(value = "약품PK 목록", example = "1L, 2L")
        private List<Long> medicineIds;

        //== 생성 메서드 ==//

        public static Request createNewAlarmRequestDto(String title, Integer doseCount, List<Long> medicineIds){
            Assert.hasText(title, "title must not be null");
            Assert.notNull(doseCount, "doseCount must not be null");
            Assert.notEmpty(medicineIds, "medicineIds must not be null");
            return Request.builder()
                    .title(title)
                    .doseCount(doseCount)
                    .medicineIds(medicineIds)
                    .build();
        }
    }
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response extends RepresentationModel<Response> {

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

        //== 생성 메서드 ==//
        public static Response createNewAlarmResponseDto(Long alarmId, String title, Integer doseCount, String email, List<SimpleMedicineDto> medicines) {
            Assert.notNull(alarmId, "alarmId must not be null");
            Assert.hasText(title, "title must not be null");
            Assert.notNull(doseCount, "doseCount must not be null");
            Assert.hasText(email, "email must not be null");
            Assert.notEmpty(medicines, "medicines must not be null");
            return Response.builder()
                    .alarmId(alarmId)
                    .title(title)
                    .doseCount(doseCount)
                    .email(email)
                    .medicines(medicines)
                    .build();
        }

        public static Response fromEntity(Alarm alarm) {
            List<MedicineAlarm> medicineAlarms = alarm.getMedicineAlarms();
            List<SimpleMedicineDto> simpleMedicineDtos = medicineAlarms.stream().map(MedicineAlarm::getMedicine).map(SimpleMedicineDto::fromEntity).toList();
            return createNewAlarmResponseDto(alarm.getAlarmId(), alarm.getTitle(), alarm.getDoseCount(), alarm.getAccount().getEmail(), simpleMedicineDtos);
        }
    }
}
