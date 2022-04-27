package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.entity.Account;
import com.smrp.smartmedicinealarm.entity.AccountStatus;
import com.smrp.smartmedicinealarm.entity.Gender;
import com.smrp.smartmedicinealarm.entity.Role;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.*;
import static com.smrp.smartmedicinealarm.dto.account.NewAccountDto.Request.*;
import static org.assertj.core.api.Assertions.assertThat;
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
}