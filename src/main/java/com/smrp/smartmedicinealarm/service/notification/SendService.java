package com.smrp.smartmedicinealarm.service.notification;

import com.smrp.smartmedicinealarm.entity.account.Account;

public interface SendService {
     void send(Account account, String title, String message);
}
