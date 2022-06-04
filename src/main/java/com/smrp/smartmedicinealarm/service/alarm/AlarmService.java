package com.smrp.smartmedicinealarm.service.alarm;

import com.smrp.smartmedicinealarm.dto.alarm.AlarmDetailDto;
import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.dto.alarm.UpdateAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;

public interface AlarmService {
    /** 알람 생성 */
    NewAlarmDto.Response addAlarm(Account account, NewAlarmDto.Request request);

    /** 알람 상세 조회*/
    AlarmDetailDto findAlarmDetails(Account account, Long alarmId);

    /** 알람 제거*/
    void removeAlarm(Account account, Long alarmId);

    /** 알람 수정하기 */
    AlarmDetailDto modifyAlarm(Long alarmId, Account account, UpdateAlarmDto updateAlarmDto);
}
