package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.bookmark.Bookmark;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.service.account.AccountBookmarkServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.smrp.smartmedicinealarm.entity.medicine.embedded.ClassNoAndName.createClassNoAndName;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.LengAndThick.createLengAndThick;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MarkCode.createMarkCode;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineColor.createMedicineColor;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineCompany.createMedicineCompany;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineDate.createMedicineDate;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineIdentification.createMedicineIdentification;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineLine.crateMedicineLine;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = AccountBookmarkController.class)
class AccountBookmarkControllerTest extends BaseControllerTest{
    @MockBean
    AccountBookmarkServiceImpl accountBookmarkService;

    @Nested
    @DisplayName("[GET]???????????? ?????? ??????")
    class WhenFindAllBookmark{
        @Test
        @DisplayName("[??????][GET] ???????????? ?????? ??????")
        public void givenAccount_whenFindAllBookmark_thenReturnBookmarkList() throws Exception{
            //given
            Account account = createAccount();
            Medicine medicine = getMedicine();
            List<SimpleMedicineDto> medicines = List.of(
                    SimpleMedicineDto.fromEntity(medicine)
            );
            account.addBookmark(Bookmark.createBookmark(account, medicine));
            when(accountBookmarkService.findAllBookmark(any(Account.class)))
                    .thenReturn(
                            SimpleBookmarkDto.createSimpleBookmarkDto(account.getAccountId(), account.getEmail(), medicines)
                    );
            //when //then
            mvc.perform(get("/api/v1/accounts/me/bookmarks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(
                                    authentication(
                                            getAuthentication(
                                                    account
                                            )
                                    )
                            )
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().methodName("findAllBookmark"))
                    .andExpect(handler().handlerType(AccountBookmarkController.class))
                    .andExpect(jsonPath("$.accountId").value(account.getAccountId()))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.medicines[0].medicineId").exists())
                    .andExpect(jsonPath("$.medicines[0].itemSeq").exists())
                    .andExpect(jsonPath("$.medicines[0].itemName").exists())
                    .andExpect(jsonPath("$.medicines[0].itemImage").exists())
                    .andExpect(jsonPath("$.medicines[0].etcOtcName").exists())
                    .andExpect(jsonPath("$.medicines[0].entpName").exists())
                    .andExpect(jsonPath("$.medicines[0]._links.self.href").exists())
                    .andExpect(jsonPath("$._links.self").isNotEmpty())
                    .andExpect(jsonPath("$._links.profile").isNotEmpty())
            ;
            verify(accountBookmarkService).findAllBookmark(any(Account.class));
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
        return Medicine.createMedicine(1L, itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
    }
}