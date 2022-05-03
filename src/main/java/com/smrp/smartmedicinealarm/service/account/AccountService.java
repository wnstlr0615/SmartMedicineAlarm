package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.dto.account.SimpleAccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    /** 회원 가입 */
    NewAccountDto.Response addAccount(NewAccountDto.Request request);

    /** 사용자 상세 조회*/
    AccountDetailsDto findAccount(Long accountId);

    Page<SimpleAccountDto> findAllAccounts(int page, int size);
}
