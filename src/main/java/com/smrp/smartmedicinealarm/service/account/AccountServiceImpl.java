package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.AccountModifyDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.dto.account.SimpleAccountDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import com.smrp.smartmedicinealarm.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smrp.smartmedicinealarm.error.code.UserErrorCode.ALREADY_DELETED_ACCOUNT;
import static com.smrp.smartmedicinealarm.error.code.UserErrorCode.CAN_NOT_UPDATE_ACCOUNT_INFO;


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

    /** 계정 상세 조회*/
    @Override
    public AccountDetailsDto findAccount(Long accountId) {
        // 계정 조회
        Account findAccount = getAccountById(accountId);

        // DTO로 변환
        return AccountDetailsDto.fromEntity(findAccount);

    }

    @Override
    public Page<SimpleAccountDto> findAllAccounts(int page, int size) {
        return accountRepository.findAll(
                PageRequest.of(page, size)
                ).map(SimpleAccountDto::fromEntity);
    }

    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(
                        () -> new UserException(UserErrorCode.NOT_FOUND_USER_ID)
                );
    }
    /** 계정 삭제 처리*/
    @Override
    @Transactional
    public void removeAccount(Long deletedId) {
        //계정 조회
        Account account = getAccountById(deletedId);

        //삭제 처리
        deleteAccount(account);
    }

    private void deleteAccount(Account account) {
        if(account.getStatus().equals(AccountStatus.DELETED)){
            throw new UserException(ALREADY_DELETED_ACCOUNT);
        }
        account.remove();
    }

    /** 계정 업데이트*/
    @Override
    @Transactional
    public void modifyAccount(Long accountId, AccountModifyDto accountModifyDto) {
        //사용자 조회
        Account account = getAccountById(accountId);

        //계정 업데이트
        updateAccountInfo(account, accountModifyDto);
    }

    private void updateAccountInfo(Account account, AccountModifyDto accountModifyDto) {
        //업데이트 가능한  상테 검증
        verifyAccountStatusUpdatable(account);

        account.updateInfo(accountModifyDto.getName(), accountModifyDto.getGender());
    }

    private void verifyAccountStatusUpdatable(Account account) {
        if(account.getStatus().equals(AccountStatus.DELETED)
                || account.getStatus().equals(AccountStatus.DORMANT)){
            throw new UserException(CAN_NOT_UPDATE_ACCOUNT_INFO);
        }
    }
}
