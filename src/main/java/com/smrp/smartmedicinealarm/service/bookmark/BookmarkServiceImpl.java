package com.smrp.smartmedicinealarm.service.bookmark;

import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import com.smrp.smartmedicinealarm.repository.medicine.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.smrp.smartmedicinealarm.entity.bookmark.Bookmark.createBookmark;
import static com.smrp.smartmedicinealarm.error.code.UserErrorCode.ACCOUNT_STATUS_IS_DORMANT;
import static com.smrp.smartmedicinealarm.error.code.UserErrorCode.ALREADY_DELETED_ACCOUNT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService{
    private final MedicineRepository medicineRepository;
    private final AccountRepository accountRepository;
    @Override
    @Transactional
    public NewBookmarkDto.Response addBookmark(Account account, NewBookmarkDto.Request bookmarkDto) {
        // 사용자 조회
        Account accountEntity = getAccountById(account.getAccountId());

        //사용자 상태 검증
        verifyAccountStatusAvailable(accountEntity);

        // 약품 목록 조회
        List<Medicine> medicines = getMedicineListByMedicineIdIn(bookmarkDto);

        //북마크 추가
        accountAddBookmarks(medicines, accountEntity);

        //SimpleDto 로 변환
        return createNewBookmarkResponseDot(account, medicines);
    }

    private NewBookmarkDto.Response createNewBookmarkResponseDot(Account account, List<Medicine> medicines) {
        List<SimpleMedicineDto> simpleMedicineDtos = medicines.stream().map(SimpleMedicineDto::fromEntity).collect(Collectors.toList());
        return NewBookmarkDto.Response.createNewBookmarkResponseDto(account.getAccountId(), account.getEmail(), simpleMedicineDtos);
    }



    private List<Medicine> getMedicineListByMedicineIdIn(NewBookmarkDto.Request bookmarkDto) {
        return medicineRepository.findAllByMedicineIdIn(bookmarkDto.getMedicineIds());
    }

    private void accountAddBookmarks(List<Medicine> medicines, Account accountEntity) {
        medicines.stream()
                .map(medicine -> createBookmark(accountEntity, medicine))
                .forEach(bookmark -> bookmark.setAccount(accountEntity));
    }

    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(
                        () -> new UserException(UserErrorCode.NOT_FOUND_USER_ID)
                );
    }
    private void verifyAccountStatusAvailable(Account account) {
        if(account.getStatus().equals(AccountStatus.DELETED)){
            throw new UserException(ALREADY_DELETED_ACCOUNT);
        }
        if(account.getStatus().equals(AccountStatus.DORMANT)){
            throw new UserException(ACCOUNT_STATUS_IS_DORMANT);
        }
    }
}
