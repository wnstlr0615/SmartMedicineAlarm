package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.annotation.MockNormalUser;
import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.service.bookmark.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto.Request.createNewBookmarkRequestDto;
import static com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto.Response.createNewBookmarkResponseDto;
import static com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto.createSimpleMedicineDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookmarkController.class)
@AutoConfigureMockMvc
class BookmarkControllerTest extends BaseControllerTest{
    @MockBean
    BookmarkService bookmarkService;
    @Nested
    @DisplayName("약 즐겨찾기 추가 API")
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
    private UsernamePasswordAuthenticationToken getAuthentication(Account account) {
        return new UsernamePasswordAuthenticationToken(
                account,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_NORMAL")
                )
        );
    }

    private Account createAccount() {
        return Account.createAccount(1L, "joon@naver.com", "asdasdasd", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
    }

    private List<SimpleMedicineDto> getSimpleMedicineDtos(){
        return List.of(
                createSimpleMedicineDto(1L, 200502447L, "쿨정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/1Mxwka5v0jL", "일반의약품", "알파제약(주)"),
                createSimpleMedicineDto(2L, 200401321L, "속코정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147428089523200037", "일반의약품", "일양약품(주)"),
                createSimpleMedicineDto(3L, 199300386L, "솔펜정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147427336539800072", "일반의약품", "에이프로젠제약(주)")
        );
    }


}