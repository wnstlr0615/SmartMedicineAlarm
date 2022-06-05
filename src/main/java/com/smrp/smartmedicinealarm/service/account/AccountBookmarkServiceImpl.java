package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountBookmarkServiceImpl implements AccountBookmarkService{
    private final AccountRepository accountRepository;

    @Override
    public SimpleBookmarkDto findAllBookmark(Account account) {
        // 사용자 상세 조회
        Account accountEntity = getDetailAccountById(account.getAccountId());
        // 약 목록을 dto 로 변환
        return createSimpleMedicineDto(accountEntity);
    }
    public Account getDetailAccountById(Long accountId){
        return accountRepository.findDetailByAccountId(accountId)
                .orElseThrow(
                        () -> new UserException(UserErrorCode.NOT_FOUND_USER_ID)
                );
    }
    private SimpleBookmarkDto createSimpleMedicineDto(Account account) {
        List<SimpleMedicineDto> simpleMedicineDtos = account.getBookmarks().stream()
                .map(bookmark ->
                        SimpleMedicineDto.fromEntity(bookmark.getMedicine())
                ).collect(Collectors.toList());
        return SimpleBookmarkDto.createSimpleBookmarkDto(account.getAccountId(), account.getEmail(), simpleMedicineDtos);
    }
}
