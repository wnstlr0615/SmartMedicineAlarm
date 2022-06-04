package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.alarm.SimpleAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;

import java.util.List;

public interface AccountAlarmService {
    /** 사용자 알람 조회 */
    List<SimpleAlarmDto> findAllAlarm(Account account);
}
