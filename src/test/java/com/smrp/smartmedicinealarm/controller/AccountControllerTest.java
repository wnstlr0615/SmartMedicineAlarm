package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.annotation.MockNormalUser;
import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.AccountModifyDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.dto.account.SimpleAccountDto;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.error.code.ErrorCode;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.IntStream;

import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Request.createNewAccountDtoRequest;
import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Response.createNewAccountDtoResponse;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


    @Test
    @MockNormalUser
    @DisplayName("[GET][성공] 사용자 상세 조회")
    public void givenAccountId_whenAccountDetails_thenReturnAccountDetailsDto() throws Exception {
        //given
        long accountId = 1L;
        String email = "joon@naver.com";
        String name = "최모씨";
        when(accountService.findAccount(anyLong()))
                .thenReturn(
                        createAccountDetailsDto(accountId, email, name)
                );

        //when //then
        mvc.perform(get("/api/v1/accounts/{accountId}", accountId)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(accountId))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.gender").value(Gender.MAN.name()))
            .andExpect(jsonPath("$.status").value(AccountStatus.USE.name()))
            .andExpect(jsonPath("$.role").value(Role.NORMAL.name()))
        ;
        verify(accountService).findAccount(anyLong());
    }

    private AccountDetailsDto createAccountDetailsDto(long accountId, String email, String name) {
        return AccountDetailsDto.createAccountDetailsDto(accountId, email, name, Gender.MAN, AccountStatus.USE, Role.NORMAL);
    }

    @Test
    @MockNormalUser
    @DisplayName("[GET][실패] 사용자 상세 조회 - 등록되지 않는 accountId로 조회")
    public void givenNotFoundAccountId_whenAccountDetails_thenUserException() throws Exception {
        //given
        long accountId = 1L;
        String email = "joon@naver.com";
        String name = "최모씨";
        when(accountService.findAccount(anyLong()))
                .thenThrow(
                        new UserException(UserErrorCode.NOT_FOUND_USER_ID)
                );

        //when //then
        mvc.perform(get("/api/v1/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())

        ;
        verify(accountService).findAccount(anyLong());
    }

    @Test
    @MockNormalUser
    @DisplayName("[성공] 사용자 전체 조회")
    public void givenPageAndSize_whenAccountList_thenReturnPagingSimpleAccounts() throws Exception{
        //given
        String page = "0";
        String size = "10";
        List<SimpleAccountDto> content = createSimpleAccountListIterate(10);
        when(accountService.findAllAccounts(anyInt(), anyInt()))
                .thenReturn(
                        new PageImpl<>(
                                content
                                , PageRequest.of(0, 10)
                                , 20)
                );
        //when //then

        mvc.perform(get("/api/v1/accounts")
            .queryParam("page", page)
            .queryParam("size", size)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.items[0].accountId").value(1L))
                .andExpect(jsonPath("$._embedded.items[0].email").value("joon1@naver.com"))
                .andExpect(jsonPath("$._embedded.items[0].name").value("minsu1"))
                .andExpect(jsonPath("$._embedded.items[0].gender").value(Gender.MAN.name()))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(20))
                .andExpect(jsonPath("$.page.totalPages").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
        ;
        verify(accountService).findAllAccounts(anyInt(), anyInt());
    }

    private List<SimpleAccountDto> createSimpleAccountListIterate(int endInclusive) {
        return IntStream.rangeClosed(1, endInclusive)
                        .mapToObj(this::createSimpleAccountDto)
                        .collect(toList());
    }

    private SimpleAccountDto createSimpleAccountDto(int i) {
        Long idx = (long) i;
        String email = "joon" + idx + "@naver.com";
        String name = "minsu" + idx;
        return SimpleAccountDto.createSimpleAccountDto(idx, email, name, Gender.MAN);
    }

    @Test
    @MockNormalUser
    @DisplayName("[DELETE][성공] 사용자 계정 삭제 처리")
    public void givenDeleteId_whenAccountRemove_thenStatusIsOk() throws Exception{
        //given
        long deleteId = 1L;

        doNothing().when(accountService)
                        .removeAccount(anyLong());
        //when //then
        mvc.perform(delete("/api/v1/accounts/{deleteId}", deleteId)
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;
        verify(accountService).removeAccount(eq(deleteId));
    }

    @Test
    @MockNormalUser
    @DisplayName("[DELETE][실패] 사용자 계정 삭제 처리 - 이미 제거한 경우")
    public void givenAlreadyDeleteId_whenAccountRemove_thenUserException() throws Exception{
        //given
        long alreadyDeleteId = 1L;

        UserErrorCode errorCode = UserErrorCode.ALREADY_DELETED_ACCOUNT;
        doThrow(new UserException(errorCode)).when(accountService)
                .removeAccount(anyLong());
        //when //then
        mvc.perform(delete("/api/v1/accounts/{deleteId}", alreadyDeleteId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.name()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(accountService).removeAccount(eq(alreadyDeleteId));
    }

    @Test
    @MockNormalUser
    @DisplayName("[성공][PUT] 사용자 계정 업데이트")
    public void givenAccountModifyDto_whenAccountModify_thenStatusIsOk() throws Exception{
        //given
        long accountId = 1L;
        doNothing().when(accountService)
                        .modifyAccount(anyLong(), any(AccountModifyDto.class));
        //when //then
        mvc.perform(put("/api/v1/accounts/{accountId}", accountId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            AccountModifyDto.createAccountModifyDto("민정", Gender.WOMAN)
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;
        verify(accountService).modifyAccount(eq(accountId), any(AccountModifyDto.class));
    }

    @Test
    @MockNormalUser
    @DisplayName("[실패][PUT] 사용자 계정 업데이트 - 삭제된 계정이거나 휴먼 계정인 경우")
    public void givenDeletedAccountIdAndAccountModifyDto_whenAccountModify_thenUserException() throws Exception{
        //given
        long accountId = 1L;
        ErrorCode errorCode = UserErrorCode.CAN_NOT_UPDATE_ACCOUNT_INFO;
        doThrow(new UserException(UserErrorCode.CAN_NOT_UPDATE_ACCOUNT_INFO))
                .when(accountService)
                .modifyAccount(anyLong(), any(AccountModifyDto.class));

        //when //then
        mvc.perform(put("/api/v1/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                mapper.writeValueAsString(
                                        AccountModifyDto.createAccountModifyDto("민정", Gender.WOMAN)
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(accountService).modifyAccount(eq(accountId), any(AccountModifyDto.class));
    }



}
