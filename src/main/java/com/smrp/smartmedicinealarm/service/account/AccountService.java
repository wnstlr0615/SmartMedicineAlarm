package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;

public interface AccountService {
    NewAccountDto.Response addAccount(NewAccountDto.Request request);
}
