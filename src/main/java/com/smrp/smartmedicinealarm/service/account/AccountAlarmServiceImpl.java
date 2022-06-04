package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.dto.alarm.SimpleAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.repository.alarm.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountAlarmServiceImpl implements AccountAlarmService{
    private final AlarmRepository alarmRepository;

    @Override
    public List<SimpleAlarmDto> findAllAlarm(Account account) {
        // 삭제되지 않은 알람 조회
        List<Alarm> alarms = getAlarms(account);

        // DTO 로 변환
        return getSimpleAlarmDtos(alarms);
    }

    private List<SimpleAlarmDto> getSimpleAlarmDtos(List<Alarm> alarms) {
        return alarms.stream().map(SimpleAlarmDto::fromEntity).toList();
    }

    private List<Alarm> getAlarms(Account account) {
        return alarmRepository.findByAccountAndDeletedFalse(account);
    }
}
