package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.annotation.MockNormalUser;
import com.smrp.smartmedicinealarm.dto.RemoveBookmarkDto;
import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.service.bookmark.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto.Request.createNewBookmarkRequestDto;
import static com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto.Response.createNewBookmarkResponseDto;
import static com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto.createSimpleMedicineDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookmarkController.class)
class BookmarkControllerTest extends BaseControllerTest{
    @MockBean
    BookmarkService bookmarkService;
    @Nested
    @DisplayName("[POST]약 즐겨찾기 추가 API")
    class WhenAddBookmark {
        @Test
        @MockNormalUser
        @DisplayName("[성공][POST]  약 즐겨찾기 추가 API")
        public void givenItemMedicineIds_whenBookmarkAdd_thenBookmarkDto() throws Exception {
            //given
            Account account = createAccount();
            List<Long> medicineIds = List.of(1L, 2L, 3L);
            List<SimpleMedicineDto> simpleMedicineDtos = getSimpleMedicineDtos();
            when(bookmarkService.addBookmark(any(Account.class), createNewBookmarkRequestDto(any())
            ))
                    .thenReturn(
                            createNewBookmarkResponseDto(account.getAccountId(), account.getEmail(), simpleMedicineDtos)
                    );
            //when //then
            mvc.perform(post("/api/v1/accounts/me/bookmarks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createNewBookmarkRequestDto(
                                                    medicineIds
                                            )
                                    )
                            ).with(authentication(
                                    getAuthentication(account))
                            )
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().methodName("bookmarkAdd"))
                    .andExpect(handler().handlerType(BookmarkController.class))
                    .andExpect(jsonPath("$.accountId").value(account.getAccountId()))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.medicines[0].medicineId").exists())
                    .andExpect(jsonPath("$.medicines[0].itemSeq").exists())
                    .andExpect(jsonPath("$.medicines[0].itemName").exists())
                    .andExpect(jsonPath("$.medicines[0].itemImage").exists())
                    .andExpect(jsonPath("$.medicines[0].etcOtcName").exists())
                    .andExpect(jsonPath("$.medicines[0].entpName").exists())
                    .andExpect(jsonPath("$.medicines[0]._links.self.href").exists())
                    .andExpect(jsonPath("$._links.self.href").exists())
                    .andExpect(jsonPath("$._links.profile.href").exists())
            ;
            verify(bookmarkService).addBookmark(any(Account.class), any(NewBookmarkDto.Request.class));
        }
        @Test
        @MockNormalUser
        @DisplayName("[실패][POST] 사용자 계정이 삭제 유저인 경우 - ALREADY_DELETED_ACCOUNT")
        public void givenDeletedAccount_whenAddBookmark_thenUserException() throws Exception{
            //given
            UserErrorCode errorCode = UserErrorCode.ALREADY_DELETED_ACCOUNT;
            Account account = createAccount();
            List<Long> medicineIds = List.of(1L, 2L, 3L);
            when(bookmarkService.addBookmark(any(Account.class), any(NewBookmarkDto.Request.class)))
                    .thenThrow(new UserException(errorCode));
            //when //then
            mvc.perform(post("/api/v1/accounts/me/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createNewBookmarkRequestDto(
                                                    medicineIds
                                            )
                                    )
                            )
                            .with(authentication(getAuthentication(account)))
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
            verify(bookmarkService).addBookmark(any(Account.class), any(NewBookmarkDto.Request.class));
        }

        @Test
        @MockNormalUser
        @DisplayName("[실패][POST] 사용자 계정이 휴먼 유저 인 경우 - ACCOUNT_STATUS_IS_DORMANT")
        public void givenDorantAccount_whenAddBookmark_thenUserException() throws Exception{
            //given
            UserErrorCode errorCode = UserErrorCode.ACCOUNT_STATUS_IS_DORMANT;
            Account account = createAccount();
            List<Long> medicineIds = List.of(1L, 2L, 3L);
            when(bookmarkService.addBookmark(any(Account.class), any(NewBookmarkDto.Request.class)))
                    .thenThrow(new UserException(errorCode));
            //when //then
            mvc.perform(post("/api/v1/accounts/me/bookmarks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createNewBookmarkRequestDto(
                                                    medicineIds
                                            )
                                    )
                            )
                            .with(authentication(getAuthentication(account)))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                    .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
            verify(bookmarkService).addBookmark(any(Account.class), any(NewBookmarkDto.Request.class));
        }

    }


    @Nested
    @DisplayName("[DELETE] 즐겨찾기 목록에서 제거 API")
    class WhenMedicineRemove{
        @Test
        @DisplayName("[성공][DELETE] 즐겨찾기 목록에서 약 제거하기")
        public void givenRemoveMedicineIds_whenBookmarkRemove_thenSuccess() throws Exception{
            //given
            List<Long> removeBookmarkList = List.of(2L, 3L);
            Account account = createAccount();
            Medicine medicine = getMedicine();
            List<SimpleMedicineDto> medicines = List.of(
                    SimpleMedicineDto.fromEntity(medicine)
            );
            when(bookmarkService.removeBookmark(any(Account.class), any(RemoveBookmarkDto.class)))
                    .thenReturn(
                            SimpleBookmarkDto.createSimpleBookmarkDto(account.getAccountId(), account.getEmail(), medicines)
                    );

            //when //then
            mvc.perform(delete("/api/v1/accounts/me/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                            .with(authentication(
                                            getAuthentication(account)
                                )
                            )
                .content(
                        mapper.writeValueAsString(
                                RemoveBookmarkDto.createRemoveBookmarkDto(removeBookmarkList)
                        )
                )
            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName("bookmarkRemove"))
                .andExpect(handler().handlerType(BookmarkController.class))
                .andExpect(jsonPath("$.accountId").value(account.getAccountId()))
                .andExpect(jsonPath("$.email").value(account.getEmail()))
                .andExpect(jsonPath("$.medicines[0].medicineId").exists())
                .andExpect(jsonPath("$.medicines[0].itemSeq").exists())
                .andExpect(jsonPath("$.medicines[0].itemName").exists())
                .andExpect(jsonPath("$.medicines[0].itemImage").exists())
                .andExpect(jsonPath("$.medicines[0].etcOtcName").exists())
                .andExpect(jsonPath("$.medicines[0].entpName").exists())
                .andExpect(jsonPath("$.medicines[0]._links.self.href").exists())
            ;
            verify(bookmarkService).removeBookmark(any(Account.class), any(RemoveBookmarkDto.class));
        }
    }

    private Medicine getMedicine() {
        Long itemSeq = 200611524L;
        String  itemName =  "마도파정";
        String itemImage =  "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149";
        String etcOtcName = "전문의약품";
        ClassNoAndName classNoAndName = createClassNoAndName("01190", "기타의 중추신경용약");
        LengAndThick lengAndThick = createLengAndThick("13.0", "13.0", "3.5");
        MedicineCompany medicineCompany = createMedicineCompany(20161439L, "(주)한국로슈");
        MedicineIdentification medicineIdentification
                = createMedicineIdentification("RO분할선C분할선HE분할선마크분할선", "십자분할선", "원형", "십자눈금이 새겨져 있는 분홍색의 원형정제이다", "나정");
        MedicineLine medicineLine = crateMedicineLine("+", "+");
        MedicineColor medicineColor = createMedicineColor("분홍", "");
        MarkCode markCode = createMarkCode("육각형", "", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147938640332900169", "");
        MedicineDate medicineDate = createMedicineDate(LocalDate.parse("2006-11-27"), LocalDate.parse("2004-12-22"), LocalDate.parse("2020-02-27"));
        return Medicine.createMedicine(1L, itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                        medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
    }

    private List<SimpleMedicineDto> getSimpleMedicineDtos(){
        return List.of(
                createSimpleMedicineDto(1L, 200502447L, "쿨정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/1Mxwka5v0jL", "일반의약품", "알파제약(주)"),
                createSimpleMedicineDto(2L, 200401321L, "속코정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147428089523200037", "일반의약품", "일양약품(주)"),
                createSimpleMedicineDto(3L, 199300386L, "솔펜정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147427336539800072", "일반의약품", "에이프로젠제약(주)")
        );
    }


}