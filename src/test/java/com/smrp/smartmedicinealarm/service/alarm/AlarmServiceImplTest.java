package com.smrp.smartmedicinealarm.service.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.MedicineAlarm;
import com.smrp.smartmedicinealarm.dto.alarm.AlarmDetailDto;
import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.dto.alarm.UpdateAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.error.code.AlarmErrorCode;
import com.smrp.smartmedicinealarm.error.exception.AlarmException;
import com.smrp.smartmedicinealarm.repository.alarm.AlarmRepository;
import com.smrp.smartmedicinealarm.repository.medicine.MedicineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto.Request.createNewAlarmRequestDto;
import static com.smrp.smartmedicinealarm.dto.alarm.UpdateAlarmDto.createUpdateAlarmDto;
import static com.smrp.smartmedicinealarm.entity.medicine.Medicine.createMedicine;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.ClassNoAndName.createClassNoAndName;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.LengAndThick.createLengAndThick;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MarkCode.createMarkCode;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineColor.createMedicineColor;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineCompany.createMedicineCompany;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineDate.createMedicineDate;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineIdentification.createMedicineIdentification;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineLine.crateMedicineLine;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AlarmServiceImplTest {
    @Mock
    AlarmRepository alarmRepository;
    @Mock
    MedicineRepository medicineRepository;
    @InjectMocks
    AlarmServiceImpl alarmService;

    @Nested
    @DisplayName("?????? ?????? ??????")
    class WhenAddAlarm{
        @Test
        @DisplayName("?????? ?????? ??????")
        public void givenNewAlarmDtoRequest_whenAddAlarm_thenReturnNewAlarmDtoResponse (){
            //given
            String title = "?????????";
            int doseCount = 6;
            Account account = createAccount(1L, "joon@naver.com");
            NewAlarmDto.Request requestDto = createNewAlarmRequestDto(title, doseCount, List.of(1L));
            List<Medicine> medicines = List.of(getMedicine());
            Alarm alarm = createAlarm("?????????", 6, account, medicines, false, null);

            when(medicineRepository.findAllByMedicineIdIn(anyList()))
                    .thenReturn(
                            medicines
                    );
            when(alarmRepository.save(any(Alarm.class)))
                    .thenReturn(
                            alarm
                    )
            ;

            //when
            NewAlarmDto.Response response = alarmService.addAlarm(account, requestDto);
            //then
            assertAll(
                    () ->  assertThat(response)
                            .hasFieldOrPropertyWithValue("alarmId",alarm.getAlarmId())
                            .hasFieldOrPropertyWithValue("title", title)
                            .hasFieldOrPropertyWithValue("doseCount", doseCount)
                            .hasFieldOrPropertyWithValue("email", account.getEmail())
                            .hasFieldOrProperty("medicines"),
                    () -> assertThat(response.getMedicines()).allSatisfy(simpleMedicineDto -> {
                                assertThat(simpleMedicineDto.getMedicineId()).isEqualTo(1L);
                                assertThat(simpleMedicineDto.getItemSeq()).isEqualTo(200611524L);
                                assertThat(simpleMedicineDto.getItemName()).isEqualTo("????????????");
                                assertThat(simpleMedicineDto.getItemImage()).isEqualTo("https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149");
                                assertThat(simpleMedicineDto.getEtcOtcName()).isEqualTo("???????????????");
                                assertThat(simpleMedicineDto.getEntpName()).isEqualTo("(???)????????????");
                        }
                    )
                );
            verify(medicineRepository).findAllByMedicineIdIn(anyList());
            verify(alarmRepository).save(any(Alarm.class));
        }


        @Test
        @DisplayName("?????? ??? ????????? ?????? ?????? ?????? - FOUND_MEDICINES_SIZE_IS_ZERO")
        public void givenWrongMedicineIds_whenAddAlarm_themAlarmException(){
            //given
            final AlarmErrorCode errorCode = AlarmErrorCode.FOUND_MEDICINES_SIZE_IS_ZERO;
            String title = "?????????";
            int doseCount = 6;
            Account account = createAccount(1L, "joon@naver.com");
            List<Long> wrongMedicineIds = List.of(999999L);
            NewAlarmDto.Request requestDto = createNewAlarmRequestDto(title, doseCount, wrongMedicineIds);

            when(medicineRepository.findAllByMedicineIdIn(wrongMedicineIds))
                    .thenThrow(new AlarmException(errorCode));
            //when
            final AlarmException exception = assertThrows(AlarmException.class,
                () -> alarmService.addAlarm(account, requestDto)
            )
            ;
            //then
            assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );
            verify(medicineRepository).findAllByMedicineIdIn(anyList());
            verify(alarmRepository, never()).save(any(Alarm.class));
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ??????")
    class WhenFindAlarmDetails{
        @Test
        @DisplayName("[??????] ?????? ?????? ?????? ??????")
        public void givenAlarmId_whenFindAlarmDetails_thenReturnAlarmDetailDto(){
            //given
            long alarmId = 1L;
            Account account = createAccount(1L, "joon@naver.com");
            List<Medicine> medicines = List.of(getMedicine());
            Alarm alarm = createAlarm("?????????", 6, account, medicines, false, null);

            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(
                            Optional.of(
                                    alarm
                            )
                    );
            //when
            AlarmDetailDto alarmDetails = alarmService.findAlarmDetails(account, alarmId);
            assertAll(
                    () ->  assertThat(alarmDetails)
                            .hasFieldOrPropertyWithValue("alarmId",alarm.getAlarmId())
                            .hasFieldOrPropertyWithValue("title", alarm.getTitle())
                            .hasFieldOrPropertyWithValue("doseCount", alarm.getDoseCount())
                            .hasFieldOrPropertyWithValue("leftOverDoseCount", alarm.getLeftOverDoseCount())
                            .hasFieldOrPropertyWithValue("email", alarm.getAccount().getEmail())
                            .hasFieldOrProperty("medicines")
                            .hasFieldOrProperty("createdAt"),
                    () -> assertThat(alarmDetails.getMedicines()).allSatisfy(simpleMedicineDto -> {
                                assertThat(simpleMedicineDto.getMedicineId()).isEqualTo(1L);
                                assertThat(simpleMedicineDto.getItemSeq()).isEqualTo(200611524L);
                                assertThat(simpleMedicineDto.getItemName()).isEqualTo("????????????");
                                assertThat(simpleMedicineDto.getItemImage()).isEqualTo("https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149");
                                assertThat(simpleMedicineDto.getEtcOtcName()).isEqualTo("???????????????");
                                assertThat(simpleMedicineDto.getEntpName()).isEqualTo("(???)????????????");
                            }
                    )
            );
            //then
            verify(alarmRepository).findDetailsByAlarmId(anyLong());
        }
        @Test
        @DisplayName("[??????] ?????? ?????? ?????? ?????? id ?????? - NOF_FOUND_ALARM_ID")
        public void givenWrongAlarmId_whenFindAlarmDetails_thenAlarmException(){
            //given
            Account account = createAccount(1L, "joon@naver.com");
            long wrongAlarmId = 999999999L;
            final AlarmErrorCode errorCode = AlarmErrorCode.NOF_FOUND_ALARM_ID;

            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(
                            Optional.empty()
                    );
            //when
            final AlarmException exception = assertThrows(AlarmException.class,
                () ->     alarmService.findAlarmDetails(account, wrongAlarmId)
            );
            //then
            assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );
            verify(alarmRepository).findDetailsByAlarmId(anyLong());
        }

        @Test
        @DisplayName("[??????] ?????? ????????? ?????? id ?????? - ACCESS_DENIED_ALARM")
        public void giveOtherUserAlarmId_whenFindAlarmDetails_thenAlarmException(){
            //given
            final AlarmErrorCode errorCode = AlarmErrorCode.ACCESS_DENIED_ALARM;
            List<Medicine> medicines = List.of(getMedicine());
            Account account = createAccount(1L, "joon1@naver.com");
            Account otherAccount = createAccount(2L, "joon2@naver.com");
            long alarmId = 1L;

            Alarm alarm = createAlarm("?????????", 6, otherAccount, medicines, false, null);
            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(
                            Optional.of(
                                    alarm
                            )
                    );
            //when
            final AlarmException exception = assertThrows(AlarmException.class,
                    () ->     alarmService.findAlarmDetails(account, alarmId)
            );
            //then
            assertAll(
                    () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                    () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );
            verify(alarmRepository).findDetailsByAlarmId(anyLong());
        }
    }

    @Nested
    @DisplayName("?????? ????????????")
    class WhenRemoveAlarm{
        @Test
        @DisplayName("[??????] ?????? ????????????")
        public void givenAlarmId_whenRemoveAlarm_thenNothing(){
            //given
            List<Medicine> medicines = List.of(getMedicine());
            Account account = createAccount(1L, "joon1@naver.com");
            long alarmId = 1L;
            Alarm alarm = createAlarm("?????????", 6, account, medicines, false, null);
            boolean beforeDeleted = alarm.isDeleted();

            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(
                            Optional.of(
                                    alarm
                            )
                    );
            //when
            alarmService.removeAlarm(account, alarmId);

            //then
            assertAll(
                    () ->assertThat(beforeDeleted).isFalse(),
                    () ->assertThat(alarm.isDeleted()).isTrue(),
                    () ->assertThat(alarm.getDeletedAt()).isNotNull()
            );
            verify(alarmRepository).findDetailsByAlarmId(anyLong());

        }

        @Test
        @DisplayName("[??????] ?????? ????????? ?????? ?????? ?????? - NOF_FOUND_ALARM_ID")
        public void givenDeletedAlarmId_whenRemoveAlarm_thenAlarmException(){
            //given
            final AlarmErrorCode errorCode = AlarmErrorCode.NOF_FOUND_ALARM_ID;
            Account account = createAccount(1L, "joon1@naver.com");
            long alarmId = 1L;
            List<Medicine> medicines = List.of(getMedicine());
            LocalDateTime deletedAt = LocalDateTime.of(2022,6,1,0,0);
            Alarm alarm = createAlarm("?????????", 6, account, medicines, true, deletedAt);

            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(
                            Optional.of(
                                    alarm
                            )
                    );
            //when
            final AlarmException exception = assertThrows(AlarmException.class,
                () -> alarmService.removeAlarm(account, alarmId)
            );
            //then
            assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );
            verify(alarmRepository).findDetailsByAlarmId(anyLong());

        }
    }
    @Nested
    @DisplayName("?????? ????????????")
    class WhenModifyAlarm{
        @Test
        @DisplayName("[??????] ?????? ????????????")
        public void givenAlarmIdAndUpdateAlarmDto_whenModifyAlarm_thenReturnAlarmDetail(){
            String updateTitle = "????????? ?????????";
            int updateDoseCount = 10;
            Account account = createAccount(1L, "joon@naver.com");
            UpdateAlarmDto updateAlarmDto = createUpdateAlarmDto(updateTitle, updateDoseCount, List.of(1L));

            List<Medicine> medicines = List.of(getMedicine());
            Alarm alarm = createAlarm("?????????", 6, account, medicines, false, null);

            when(medicineRepository.findAllByMedicineIdIn(anyList()))
                    .thenReturn(medicines);
            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(Optional.of(alarm));

            //when

            AlarmDetailDto alarmDetailDto = alarmService.modifyAlarm(1L, account, updateAlarmDto);
            //then
            assertAll(
                    () ->  assertThat(alarmDetailDto)
                            .hasFieldOrPropertyWithValue("alarmId",account.getAccountId())
                            .hasFieldOrPropertyWithValue("title", updateTitle)
                            .hasFieldOrPropertyWithValue("doseCount", updateDoseCount)
                            .hasFieldOrPropertyWithValue("email", account.getEmail())
                            .hasFieldOrProperty("medicines"),
                    () -> assertThat(alarmDetailDto.getMedicines()).allSatisfy(simpleMedicineDto -> {
                                assertThat(simpleMedicineDto.getMedicineId()).isEqualTo(1L);
                                assertThat(simpleMedicineDto.getItemSeq()).isEqualTo(200611524L);
                                assertThat(simpleMedicineDto.getItemName()).isEqualTo("????????????");
                                assertThat(simpleMedicineDto.getItemImage()).isEqualTo("https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149");
                                assertThat(simpleMedicineDto.getEtcOtcName()).isEqualTo("???????????????");
                                assertThat(simpleMedicineDto.getEntpName()).isEqualTo("(???)????????????");
                            }
                    )
            );
            verify(medicineRepository).findAllByMedicineIdIn(anyList());
            verify(alarmRepository).findDetailsByAlarmId(anyLong());

        }
        @Test
        @DisplayName("[??????] ????????? ?????? ?????? ?????? ??????")
        public void givenWrongMedicineIds_whenModifyAlarm_themAlarmException(){
            //given
            final AlarmErrorCode errorCode = AlarmErrorCode.FOUND_MEDICINES_SIZE_IS_ZERO;
            String updateTitle = "????????? ?????????";
            int updateDoseCount = 10;
            Account account = createAccount(1L, "joon@naver.com");
            UpdateAlarmDto updateAlarmDto = createUpdateAlarmDto(updateTitle, updateDoseCount, List.of(1L));

            List<Medicine> medicines = List.of(getMedicine());
            Alarm alarm = createAlarm("?????????", 6, account, medicines, false, null);

            when(medicineRepository.findAllByMedicineIdIn(anyList()))
                    .thenReturn(List.of());
            when(alarmRepository.findDetailsByAlarmId(anyLong()))
                    .thenReturn(Optional.of(alarm));
            //when
            final AlarmException exception = assertThrows(AlarmException.class,
                () -> alarmService.modifyAlarm(1L, account, updateAlarmDto)
            );
            //then
            assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );
            verify(medicineRepository).findAllByMedicineIdIn(anyList());
            verify(alarmRepository).findDetailsByAlarmId(anyLong());
        }
    }
    private Alarm createAlarm(String title, int doseCount, Account account, List<Medicine> medicines, boolean deleted, LocalDateTime deletedAt) {
        Alarm alarm = Alarm.createAlarm(1L, title, doseCount, account, null, deleted, deletedAt);
        List<MedicineAlarm> medicineAlarms = medicines.stream().map(medicine -> MedicineAlarm.createMedicineAlarm(alarm, medicine)).toList();
        alarm.setCreateDate(LocalDateTime.of(2022 , 6, 1, 0,0));
        for (MedicineAlarm medicineAlarm : medicineAlarms) {
            alarm.addMedicineAlarm(medicineAlarm);
        }
        return alarm;
    }

    protected Account createAccount(long accountId, String email) {
        return Account.createAccount(accountId, email, "asdasdasd", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
    }
    private Medicine getMedicine() {
        Long itemSeq = 200611524L;
        String  itemName =  "????????????";
        String itemImage =  "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149";
        String etcOtcName = "???????????????";
        ClassNoAndName classNoAndName = createClassNoAndName("01190", "????????? ??????????????????");
        LengAndThick lengAndThick = createLengAndThick("13.0", "13.0", "3.5");
        MedicineCompany medicineCompany = createMedicineCompany(20161439L, "(???)????????????");
        MedicineIdentification medicineIdentification
                = createMedicineIdentification("RO?????????C?????????HE????????????????????????", "???????????????", "??????", "??????????????? ????????? ?????? ???????????? ??????????????????", "??????");
        MedicineLine medicineLine = crateMedicineLine("+", "+");
        MedicineColor medicineColor = createMedicineColor("??????", "");
        MarkCode markCode = createMarkCode("?????????", "", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147938640332900169", "");
        MedicineDate medicineDate = createMedicineDate(LocalDate.parse("2006-11-27"), LocalDate.parse("2004-12-22"), LocalDate.parse("2020-02-27"));
        return createMedicine(1L, itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
    }
}