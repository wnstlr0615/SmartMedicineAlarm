package com.smrp.smartmedicinealarm.service.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.error.code.AlarmErrorCode;
import com.smrp.smartmedicinealarm.error.exception.AlarmException;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import com.smrp.smartmedicinealarm.repository.alarm.AlarmRepository;
import com.smrp.smartmedicinealarm.repository.medicine.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmServiceImpl implements AlarmService{
    private final AlarmRepository alarmRepository;
    private final AccountRepository accountRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public NewAlarmDto.Response addAlarm(Account account, NewAlarmDto.Request request) {
        List<Medicine> medicines = getMedicinesByIdIn(request);

        Alarm alarm = createAlarm(account, request, medicines);
        Alarm saveAlarm = alarmRepository.save(alarm);
        return NewAlarmDto.Response.fromEntity(saveAlarm);
    }

    private List<Medicine> getMedicinesByIdIn(NewAlarmDto.Request request) {
        List<Medicine> medicines = medicineRepository.findAllByMedicineIdIn(request.getMedicineIds());
        if(medicines.size() <= 0){
            log.info("must not have medicines size zero");
            throw new AlarmException(AlarmErrorCode.FOUND_MEDICINES_SIZE_IS_ZERO);
        }
        return medicines;
    }

    private Alarm createAlarm(Account account, NewAlarmDto.Request request, List<Medicine> medicines) {

        return Alarm.createAlarm(request.getTitle(), request.getDoseCount(), account, medicines);
    }
}
