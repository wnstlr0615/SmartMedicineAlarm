package com.smrp.smartmedicinealarm.service.notification;

import com.smrp.smartmedicinealarm.entity.account.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile({"dev", "local"})
@Primary
@Service
public class LogSendService implements SendService{
    @Override
    public void send(Account account, String title, String message) {
        log.info("{}", title);
        log.info("{} 사용자에게 {} 를 전송 하였습니다.", account.getEmail(), message);
    }
}
