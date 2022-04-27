package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Response;
import com.smrp.smartmedicinealarm.entity.Gender;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Request.createNewAccountDtoRequest;
import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Response.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@ActiveProfiles("test")
class AccountControllerTest extends BaseControllerTest{
    @MockBean
    AccountService accountService;

    @Test
    @DisplayName("[POST][성공] 회원가입 테스트")
    public void givenNewAccountRequest_whenAccountAdd_thenReturnNewAccountResponse() throws Exception{
        //given
        NewAccountDto.Request givenNewAccount
                = createNewAccountDtoRequest("joon@naver.com", "!abcdefgh123456", "준", Gender.MAN);

        when(accountService.addAccount(any(NewAccountDto.Request.class)))
                .thenReturn(
                        createNewAccountDtoResponse(1L, "joon@naver.com", "준", Gender.MAN)
                );
        //when //then
        mvc.perform(post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenNewAccount
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.email").value("joon@naver.com"))
                .andExpect(jsonPath("$.name").value("준"))
                .andExpect(jsonPath("$.gender").value("MAN"))
        ;
        verify(accountService).addAccount(any(NewAccountDto.Request.class));
    }


    @Test
    @DisplayName("[POST][실패] 회원가입 테스트(이메일 중복 에러) - EMAIL_IS_DUPLICATED ")
    public void givenDuplicatedAccountRequest_whenAccountAdd_thenUserException() throws Exception{
        //given
        UserErrorCode errorCode = UserErrorCode.EMAIL_IS_DUPLICATED;

        NewAccountDto.Request givenNewAccount
                = createNewAccountDtoRequest("joon@naver.com", "!abcdefgh123456", "준", Gender.MAN);

        when(accountService.addAccount(any(NewAccountDto.Request.class)))
                .thenThrow(
                        new UserException(errorCode)
                );
        //when //then
        mvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        givenNewAccount
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.name()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(accountService).addAccount(any(NewAccountDto.Request.class));
    }


}