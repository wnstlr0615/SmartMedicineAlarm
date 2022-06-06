package com.smrp.smartmedicinealarm.service.notification;

import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.utils.MailComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSendService implements SendService{
    private final MailComponent mailComponent;
    @Override
    public void send(Account account, String title, String message) {
        mailComponent.sendMail(account.getEmail(), account.getName() , title, message);
    }
}
