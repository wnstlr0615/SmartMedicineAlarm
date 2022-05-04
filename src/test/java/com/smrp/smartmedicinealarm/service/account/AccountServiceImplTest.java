package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.AccountModifyDto;
import com.smrp.smartmedicinealarm.dto.account.SimpleAccountDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.error.code.UserErrorCode;
import com.smrp.smartmedicinealarm.error.exception.UserException;
import com.smrp.smartmedicinealarm.repository.AccountRepository;
import com.smrp.smartmedicinealarm.utils.PasswordUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Request;
import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Request.createNewAccountDtoRequest;
import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Response;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    AccountRepository accountRepository;
    @Spy
    PasswordUtils passwordUtils = new PasswordUtils(passwordEncoder);
    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    @DisplayName("[성공] 회원가입 ")
    public void givenNewAccountDtoRequest_whenAddAccount_thenReturnNewAccountDtoResponse(){
        //given
        String email = "joon@naver.com";
        String rawPassword = "abcdefghi1234";
        String name = "joon";
        Gender gender = Gender.MAN;
        Request givenNewAccount = createNewAccountDtoRequest(email, rawPassword, name, gender);

        when(accountRepository.existsByEmail(eq(email)))
                .thenReturn(false);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(
                        createAccount(1L, email, rawPassword, name, gender)
                );
        //when
        Response response = accountService.addAccount(givenNewAccount);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("accountId", 1L)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("gender", gender)
        ;
        verify(passwordUtils).encode(anyString());
        verify(accountRepository).existsByEmail(eq(email));
        verify(accountRepository).save(any(Account.class));
    }

    private Account createAccount(long accountId, String email, String rawPassword, String name, Gender gender) {
        return Account.createAccount(accountId, email, rawPassword, name, gender, AccountStatus.USE, Role.NORMAL);
    }
    
    @Test
    @DisplayName("[실패] 회원가입(이메일 중복) - EMAIL_IS_DUPLICATED")
    public void givenDuplicatedAccountRequest_whenAddAccount_thenUserException(){
        //given
        UserErrorCode errorCode = UserErrorCode.EMAIL_IS_DUPLICATED;
        String email = "joon@naver.com";
        String rawPassword = "abcdefghi1234";
        String name = "joon";
        Gender gender = Gender.MAN;
        Request givenNewAccount = createNewAccountDtoRequest(email, rawPassword, name, gender);

        when(accountRepository.existsByEmail(eq(email)))
                .thenReturn(true);

        //when //then
        UserException exception = assertThrows(UserException.class,
                () -> accountService.addAccount(givenNewAccount));

        verify(passwordUtils).encode(anyString());
        verify(accountRepository).existsByEmail(eq(email));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("[성공] 사용자 상세 조회 ")
    public void givenAccountId_whenAccountDetails_thenReturnAccountDetailsDto(){
        //given
        long accountId = 1L;
        String email = "joon@naver.com";
        String rawPassword = "abcdefghi1234";
        String name = "joon";
        Gender gender = Gender.MAN;

        when(accountRepository.findById(anyLong()))
                .thenReturn(
                        Optional.of(
                                createAccount(accountId, email, rawPassword, name, gender)
                        )
                );

        //when
        AccountDetailsDto accountDetailsDto = accountService.findAccount(accountId);

        //then
        assertThat(accountDetailsDto)
                .hasFieldOrPropertyWithValue("accountId", accountId)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("gender", gender)
                .hasFieldOrPropertyWithValue("status", AccountStatus.USE)
                .hasFieldOrPropertyWithValue("role", Role.NORMAL)
            ;
        verify(accountRepository).findById(eq(accountId));
    }

    @Test
    @DisplayName("[실패] 사용자 상세 조회 - 잘못된 accountId ")
    public void givenNotfoundAccountId_whenAccountDetails_thenUserException_whenAccountDetails(){
        //given
        final UserErrorCode errorCode = UserErrorCode .NOT_FOUND_USER_ID;
            long notfoundAccountId = 9999L;
        //when
        final UserException exception = assertThrows(UserException.class,
            () -> accountService.findAccount(notfoundAccountId)
        )
        ;
        //then
        assertAll(
            () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
            () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(accountRepository).findById(eq(notfoundAccountId));
    }

    @Test
    @DisplayName("[성공] 사용자 전체 조회")
    public void givenPageAndSize_whenAccountList_thenReturnPagingSimpleAccounts(){
        //given
        int page = 0;
        int size = 10;
        List<Account> accountList = createAccountListIterate(10);
        when(accountRepository.findAll(any(Pageable.class)))
                .thenReturn(
                        new PageImpl<>(accountList, PageRequest.of(page, size), 20)
                );
        //when
        Page<SimpleAccountDto> simpleAccountDtos = accountService.findAllAccounts(page, size);

        //then
        assertAll(
                () -> assertThat(simpleAccountDtos.getTotalElements()).isEqualTo(20),
                () -> assertThat(simpleAccountDtos.getTotalPages()).isEqualTo(2),
                () -> assertThat(simpleAccountDtos.getContent().get(0).getAccountId()).isEqualTo(1L),
                () -> assertThat(simpleAccountDtos.getContent().get(0).getEmail()).isEqualTo("joon1@naver.com"),
                () -> assertThat(simpleAccountDtos.getContent().get(0).getName()).isEqualTo("minsu1"),
                () -> assertThat(simpleAccountDtos.getContent().get(0).getGender()).isEqualTo(Gender.MAN)
        );
        verify(accountRepository).findAll(any(Pageable.class));
    }

    private List<Account> createAccountListIterate(int endInclusive) {
        return IntStream.rangeClosed(1, endInclusive)
                .mapToObj(this::createAccount)
                .collect(toList());
    }

    private Account createAccount(long idx) {
        return createAccount(idx, AccountStatus.USE);
    }

    private Account createAccount(long idx, AccountStatus status) {
        String email = "joon" + idx + "@naver.com";
        String name = "minsu" + idx;
        return Account.createAccount(idx, email, "abecasdas", name, Gender.MAN, status, Role.NORMAL);
    }

    @Test
    @DisplayName("[성공] 사용자 계정 삭제 처리")
    public void givenDeleteId_whenRemoveAccount_thenNothing(){
        //given
        long deleteId = 1L;
        Account account = createAccount(deleteId);
        AccountStatus beforeStatus = account.getStatus();
        when(accountRepository.findById(anyLong()))
                .thenReturn(
                        Optional.of(
                                account
                        )
                );

        //when
        accountService.removeAccount(deleteId);

        //then
        assertAll(
                () -> assertThat(beforeStatus).isEqualTo(AccountStatus.USE),
                () -> assertThat(account.getStatus()).isEqualTo(AccountStatus.DELETED)
        );
        verify(accountRepository).findById(eq(deleteId));
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 처리 - 이미 삭제된 계정")
    public void givenAlreadyDeleteId_whenRemoveAccount_thenUserException(){
        //given
        UserErrorCode errorCode = UserErrorCode.ALREADY_DELETED_ACCOUNT;
        long deleteId = 1L;
        Account account = createAccount(deleteId, AccountStatus.DELETED);
        when(accountRepository.findById(anyLong()))
                .thenReturn(
                        Optional.of(
                                account
                        )
                );

        //when
        UserException userException = assertThrows(UserException.class,
                () -> accountService.removeAccount(deleteId)
        );

        //then
        assertAll(
                () -> assertThat(userException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(userException.getMessage()).isEqualTo(errorCode.getDescription())
        );

        verify(accountRepository).findById(eq(deleteId));
    }
    @Test
    @DisplayName("[성공] 사용자 계정 업데이트")
    public void givenAccountModifyDto_whenModifyAccount_thenNothing(){
        //given
        long accountId = 1L;
        AccountModifyDto accountModifyDto = AccountModifyDto.createAccountModifyDto("민정", Gender.WOMAN);
        Account account = createAccount(accountId, "민수", Gender.MAN, AccountStatus.USE);

        when(accountRepository.findById(accountId))
                .thenReturn(
                        Optional.of(
                                account
                        )
                );
        //when
        accountService.modifyAccount(accountId, accountModifyDto);

        //then
        assertAll(
                () -> assertThat(account.getName()).isEqualTo("민정"),
                () -> assertThat(account.getGender()).isEqualTo(Gender.WOMAN)
        );
        verify(accountRepository).findById(eq(accountId));
    }

    private Account createAccount(long accountId, String name, Gender gender, AccountStatus status) {
        String email = String.format("joon%d@naver.com", accountId);
        return Account.createAccount(accountId, email, "abecasdas", name, gender, status, Role.NORMAL);
    }


    @Test
    @DisplayName("[실패] 사용자 계정 업데이트 - 삭제된 계정인 경우")
    public void givenDeleteId_whenModifyAccount_thenUserException(){
        //given
        final UserErrorCode errorCode = UserErrorCode.CAN_NOT_UPDATE_ACCOUNT_INFO;

        long accountId = 1L;
        AccountModifyDto accountModifyDto = AccountModifyDto.createAccountModifyDto("민정", Gender.WOMAN);
        Account account = createAccount(accountId, "민수", Gender.MAN, AccountStatus.DELETED);

        when(accountRepository.findById(accountId))
                .thenReturn(
                        Optional.of(
                                account
                        )
                );

        //when
        final UserException  exception = assertThrows(UserException.class,
            () ->  accountService.modifyAccount(accountId, accountModifyDto)
        )
        ;
        //then
        assertAll(
            () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
            () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(accountRepository).findById(eq(accountId));

    }

    @Test
    @DisplayName("[실패] 사용자 계정 업데이트 - 휴먼 계정인 경우")
    public void givenDorantId_whenModifyAccount_thenUserException(){
        //given
        final UserErrorCode errorCode = UserErrorCode.CAN_NOT_UPDATE_ACCOUNT_INFO;

        long accountId = 1L;
        AccountModifyDto accountModifyDto = AccountModifyDto.createAccountModifyDto("민정", Gender.WOMAN);
        Account account = createAccount(accountId, "민수", Gender.MAN, AccountStatus.DORMANT);

        when(accountRepository.findById(accountId))
                .thenReturn(
                        Optional.of(
                                account
                        )
                );

        //when
        final UserException  exception = assertThrows(UserException.class,
                () ->  accountService.modifyAccount(accountId, accountModifyDto)
        )
                ;
        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(accountRepository).findById(eq(accountId));

    }
}