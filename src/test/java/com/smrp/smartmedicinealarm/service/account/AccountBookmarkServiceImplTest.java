package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.entity.bookmark.Bookmark;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import com.smrp.smartmedicinealarm.service.bookmark.BookmarkServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.smrp.smartmedicinealarm.entity.medicine.Medicine.createMedicine;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.ClassNoAndName.createClassNoAndName;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.LengAndThick.createLengAndThick;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MarkCode.createMarkCode;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineColor.createMedicineColor;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineCompany.createMedicineCompany;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineDate.createMedicineDate;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineIdentification.createMedicineIdentification;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineLine.crateMedicineLine;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountBookmarkServiceImplTest {
    @Mock
    AccountRepository accountRepository;
    @InjectMocks
    BookmarkServiceImpl bookmarkService;

    @Nested
    @DisplayName("???????????? ?????? ??????")
    class WhenFindAllBookmark{
        @Test
        @DisplayName("[??????] ???????????? ?????? ??????")
        public void givenAccount_whenFindAllBookmark_thenReturnBookmarkList(){
            //given
            Account account = createAccount(AccountStatus.USE);
            Medicine medicine = getMedicine();
            account.addBookmark(Bookmark.createBookmark(account, medicine));
            when(accountRepository.findDetailByAccountId(anyLong()))
                    .thenReturn(
                            Optional.of(
                                    account
                            )
                    );

            //when
            SimpleBookmarkDto simpleBookmarkDto = bookmarkService.findAllBookmark(account);
            //then
            assertAll(
                    () -> assertThat(simpleBookmarkDto)
                            .hasFieldOrPropertyWithValue("accountId", account.getAccountId())
                            .hasFieldOrPropertyWithValue("email", account.getEmail())
                            .hasFieldOrProperty("medicines"),
                    () -> assertThat(simpleBookmarkDto.getMedicines()).allSatisfy(simpleMedicineDto -> {
                                assertThat(simpleMedicineDto.getMedicineId()).isEqualTo(1L);
                                assertThat(simpleMedicineDto.getItemSeq()).isEqualTo(200611524L);
                                assertThat(simpleMedicineDto.getItemName()).isEqualTo("????????????");
                                assertThat(simpleMedicineDto.getItemImage()).isEqualTo("https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149");
                                assertThat(simpleMedicineDto.getEtcOtcName()).isEqualTo("???????????????");
                                assertThat(simpleMedicineDto.getEntpName()).isEqualTo("(???)????????????");
                            }
                    )

            );
            verify(accountRepository).findDetailByAccountId(eq(account.getAccountId()));
        }
    }
    private Medicine getMedicine() {
        Long itemSeq = 200611524L;
        String  itemName =  "????????????";
        String itemImage =  "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149";
        String etcOtcName = "???????????????";
        ClassNoAndName classNoAndName = createClassNoAndName("01190", "????????? ??????????????????");
        LengAndThick lengAndThick = createLengAndThick("13.0", "13.0", "3.5");
        MedicineCompany medicineCompany = createMedicineCompany(20161439L, "(???)????????????");
        MedicineIdentification medicineIdentification
                = createMedicineIdentification("RO?????????C?????????HE????????????????????????", "???????????????", "??????", "??????????????? ????????? ?????? ???????????? ??????????????????", "??????");
        MedicineLine medicineLine = crateMedicineLine("+", "+");
        MedicineColor medicineColor = createMedicineColor("??????", "");
        MarkCode markCode = createMarkCode("?????????", "", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147938640332900169", "");
        MedicineDate medicineDate = createMedicineDate(LocalDate.parse("2006-11-27"), LocalDate.parse("2004-12-22"), LocalDate.parse("2020-02-27"));
        return createMedicine(1L, itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
    }
    private Account createAccount(AccountStatus accountStatus) {
        return Account.createAccount(1L, "joon@naver.com", "asdasdasd", "joon", Gender.MAN, accountStatus, Role.NORMAL);
    }
}