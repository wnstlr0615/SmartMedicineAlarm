package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;

public interface AccountService {
    /** 회원 가입 */
    NewAccountDto.Response addAccount(NewAccountDto.Request request);

    /** 사용자 상세 조회*/
    AccountDetailsDto findAccount(Long accountId);
}
