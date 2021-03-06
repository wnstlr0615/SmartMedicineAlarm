package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.MedicineAlarm;
import com.smrp.smartmedicinealarm.dto.alarm.SimpleAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.repository.alarm.AlarmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountAlarmServiceImplTest {
    @Mock
    AlarmRepository alarmRepository;
    @InjectMocks
    AccountAlarmServiceImpl alarmService;

    @Test
    @DisplayName("[??????] ????????? ???????????? ??????")
    public void givenNothing_whenFindAllAlarms_thenSimpleAlarmDtos (){
        //given
        Account account = createAccount();
        when(alarmRepository.findByAccountAndDeletedFalse(any(Account.class)))
                .thenReturn(
                        List.of(
                                createAlarm(1L, "?????????1", account),
                                createAlarm(2L, "?????????2", account)
                        )
                );
        //when
        List<SimpleAlarmDto> simpleAlarmDtos = alarmService.findAllAlarm(account);

        //then
        assertAll(
            () -> assertThat(simpleAlarmDtos.get(0))
                    .hasFieldOrPropertyWithValue("alarmId", 1L)
                    .hasFieldOrPropertyWithValue("title", "?????????1")
                    .hasFieldOrPropertyWithValue("used", true)
                    .hasFieldOrProperty("createdAt")
        );
        verify(alarmRepository).findByAccountAndDeletedFalse(any(Account.class));
    }
    protected Account createAccount() {
        return Account.createAccount(1L, "joon@naver.com", "asdasdasd", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
    }
    private Alarm createAlarm(Long alarmId, String title, Account account) {
        Alarm alarm = Alarm.createAlarm(alarmId, title, 6, account, null, false, null);
        List<Medicine> medicines = List.of(
                getMedicine(),
                getMedicine()
        );

        List<MedicineAlarm> medicineAlarms = medicines.stream().map(medicine -> MedicineAlarm.createMedicineAlarm(alarm, medicine)).toList();
        alarm.setCreateDate(LocalDateTime.of(2022 , 6, 1, 0,0));
        for (MedicineAlarm medicineAlarm : medicineAlarms) {
            alarm.addMedicineAlarm(medicineAlarm);
        }
        return alarm;
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