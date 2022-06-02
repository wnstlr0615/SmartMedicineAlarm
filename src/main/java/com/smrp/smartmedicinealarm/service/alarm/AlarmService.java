package com.smrp.smartmedicinealarm.service.alarm;

import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;

public interface AlarmService {
    /** 알람 생성 */
    NewAlarmDto.Response addAlarm(Account account, NewAlarmDto.Request request);
}
