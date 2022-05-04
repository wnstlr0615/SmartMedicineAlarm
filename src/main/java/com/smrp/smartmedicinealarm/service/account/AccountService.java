package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.AccountModifyDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.dto.account.SimpleAccountDto;
import org.springframework.data.domain.Page;

public interface AccountService {
    /** 회원 가입 */
    NewAccountDto.Response addAccount(NewAccountDto.Request request);

    /** 사용자 상세 조회*/
    AccountDetailsDto findAccount(Long accountId);

    /** 사용자 전체 조회*/
    Page<SimpleAccountDto> findAllAccounts(int page, int size);

    /** 사용자 삭제 */
    void removeAccount(Long deletedId);

    /** 사용자 정보 업데이트*/
    void modifyAccount(Long accountId, AccountModifyDto accountModifyDto);
}
