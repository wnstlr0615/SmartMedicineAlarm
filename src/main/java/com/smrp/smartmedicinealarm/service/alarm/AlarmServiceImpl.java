package com.smrp.smartmedicinealarm.service.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.alarm.AlarmDetailDto;
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
        // 약 목록 조회
        List<Medicine> medicines = getMedicinesByIdIn(request);

        //알람 생성
        Alarm alarm = createAlarm(account, request, medicines);

        // 알람 저장
        Alarm saveAlarm = alarmRepository.save(alarm);

        //DTO로 변환
        return NewAlarmDto.Response.fromEntity(saveAlarm);
    }

    @Override
    public AlarmDetailDto findAlarmDetails(Account account, Long alarmId) {
        // 알람 조회
        Alarm alarm = getAlarmById(alarmId);

        // 알람 사용자 접근 검증
        validAlarmAccessAble(alarm, account);

        // DTO로 변환
        return AlarmDetailDto.fromEntity(alarm);
    }

    @Override
    @Transactional
    public void removeAlarm(Account account, Long alarmId) {
        //알람 조회
        Alarm alarm = getAlarmById(alarmId);

        // 알람 사용자 접근 검증
        validAlarmAccessAble(alarm, account);

        // 알람 제거
        alarmRemove(alarm);
    }

    private void alarmRemove(Alarm alarm) {
        if(alarm.isDeleted()){
            throw new AlarmException(AlarmErrorCode.ALREADY_DELETED_ALARM);
        }
        alarm.remove();
    }

    private void validAlarmAccessAble(Alarm alarm, Account account) {
        if(!alarm.getAccount().getAccountId().equals(account.getAccountId())){
            throw new AlarmException(AlarmErrorCode.ACCESS_DENIED_ALARM);
        }
    }

    private Alarm getAlarmById(Long alarmId) {
        return alarmRepository.findDetailsByAlarmId(alarmId)
                .orElseThrow(() -> new AlarmException(AlarmErrorCode.NOF_FOUND_ALARM_ID));
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
