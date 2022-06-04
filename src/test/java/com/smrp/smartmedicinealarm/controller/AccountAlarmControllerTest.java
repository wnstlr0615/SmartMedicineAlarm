package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.alarm.SimpleAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.account.AccountAlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers =AccountAlarmController.class)
class AccountAlarmControllerTest extends BaseControllerTest{
    @MockBean
    AccountAlarmService accountAlarmService;

    @Test
    @DisplayName("[성공][GET] 사용자 알람 목록 조회하기")
    public void givenNothing_whenFindAllAlarms_thenSimpleAlarmDtos() throws Exception{
        //given
        Account account = createAccount();
        when(accountAlarmService.findAllAlarm(any(Account.class)))
                .thenReturn(
                        List.of(
                                createSimpleAlarmDto(1L, "알람1"),
                                createSimpleAlarmDto(2L, "알람2")
                        )
                );
        //when //then
        mvc.perform(get("/api/v1/accounts/me/alarms")
            .contentType(MediaType.APPLICATION_JSON)
                        .with(
                                authentication(
                                        getAuthentication(account)
                                )
                        )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(AccountAlarmController.class))
            .andExpect(handler().methodName("findAllAlarm"))
            .andExpect(jsonPath("$._embedded.items[0].alarmId").exists())
            .andExpect(jsonPath("$._embedded.items[0].title").exists())
            .andExpect(jsonPath("$._embedded.items[0].used").exists())
            .andExpect(jsonPath("$._embedded.items[0].createdAt").exists())
            .andExpect(jsonPath("$._embedded.items[0]._links.self.href").exists())
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(accountAlarmService).findAllAlarm(any(Account.class));
    }

    private SimpleAlarmDto createSimpleAlarmDto(long alarmId, String title) {
        LocalDate createAt = LocalDate.of(2022, 6, 4);
        return SimpleAlarmDto.createAlarmDetailDto(alarmId, title, true, createAt);
    }
}