package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.entity.Account;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import com.smrp.smartmedicinealarm.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordUtils passwordUtils;

    /** 계정 생성 */
    @Override
    @Transactional
    public NewAccountDto.Response addAccount(NewAccountDto.Request request) {
        //계정 생성
        Account account = createAccount(request);

        //AccountEntity 저장
        Account accountEntity = saveAccountEntity(account);

        //Dto 로 변환
        return NewAccountDto.Response.fromEntity(accountEntity);
    }

    private Account createAccount(NewAccountDto.Request request) {
        Account account = request.toEntity();

        //패스워드 암호화
        String bcryptPassword = passwordUtils.encode(account.getPassword());
        account.setBcryptPassword(bcryptPassword);

        return account;
    }

    private Account saveAccountEntity(Account account) {
        validAccountDuplicated(account);
        return accountRepository.save(account);
    }

    private void validAccountDuplicated(Account account) {
        if(isExists(account)){
            throw new UserException(UserErrorCode.EMAIL_IS_DUPLICATED);
        }
    }

    private boolean isExists(Account account) {
        return accountRepository.existsByEmail(account.getEmail());
    }
}
