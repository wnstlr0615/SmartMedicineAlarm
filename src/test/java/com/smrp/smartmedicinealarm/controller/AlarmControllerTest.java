package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.alarm.AlarmDetailDto;
import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.error.code.AlarmErrorCode;
import com.smrp.smartmedicinealarm.error.exception.AlarmException;
import com.smrp.smartmedicinealarm.service.alarm.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto.Request.createNewAlarmRequestDto;
import static com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto.createSimpleMedicineDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlarmController.class)
class AlarmControllerTest extends BaseControllerTest {
    @MockBean
    AlarmService alarmService;

    @Nested
    @DisplayName("[POST] 알람 생성 API")
    class WhenAlarmAdd{
        @Test
        @DisplayName("[성공][POST] 알람 생성하기 API")
        public void givenNewAlarmDtoRequest_whenAlarmAdd_thenReturnNewAlarmDtoResponse() throws Exception{
            //given
            long alarmId = 1L;
            String title = "알람명";
            int doseCount = 6;

            List<SimpleMedicineDto> medicines = List.of(
                    createSimpleMedicineDto(1L, 200502447L, "쿨정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/1Mxwka5v0jL", "일반의약품", "알파제약(주)"),
                    createSimpleMedicineDto(2L, 200401321L, "속코정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147428089523200037", "일반의약품", "일양약품(주)")
            );

            when(alarmService.addAlarm(any(Account.class), any(NewAlarmDto.Request.class)))
                    .thenReturn(
                            NewAlarmDto.Response.createNewAlarmResponseDto(alarmId, title, doseCount, "joon@naver.com", medicines)
                    );

            //when //then
            mvc.perform(post("/api/v1/alarms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createNewAlarmRequestDto(title, doseCount, List.of(1L, 2L))
                        )
                ).with(
                        authentication(getAuthentication(createAccount())))
            )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alarmId").value(1L))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.doseCount").value(doseCount))
                .andExpect(jsonPath("$.medicines[0].medicineId").exists())
                .andExpect(jsonPath("$.medicines[0].itemSeq").exists())
                .andExpect(jsonPath("$.medicines[0].itemName").exists())
                .andExpect(jsonPath("$.medicines[0].itemImage").exists())
                .andExpect(jsonPath("$.medicines[0].etcOtcName").exists())
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
            ;
            verify(alarmService).addAlarm(any(Account.class), any(NewAlarmDto.Request.class));
        }
        @Test
        @DisplayName("[실패][POST] 잘못된 약품 id로 인해 찾은 약 종류 갯수가 0 개인 인 경우")
        public void givenWrongMedicineIds_whenAlarmAdd_themAlarmException() throws Exception{
            //given
           AlarmErrorCode errorCode = AlarmErrorCode.FOUND_MEDICINES_SIZE_IS_ZERO;
            String title = "알람명";
            int doseCount = 6;
            long wrongMedicineIds = 99999999999L;

            when(alarmService.addAlarm(any(Account.class), any(NewAlarmDto.Request.class)))
                    .thenThrow(
                            new AlarmException(errorCode)
                    );

            //when //then
            mvc.perform(post("/api/v1/alarms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createNewAlarmRequestDto(title, doseCount, List.of(wrongMedicineIds))
                                    )
                            ).with(
                                    authentication(getAuthentication(createAccount())))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                    .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
            verify(alarmService).addAlarm(any(Account.class), any(NewAlarmDto.Request.class));

        }
    }
    @Nested
    @DisplayName("[GET] 알람 상세 조회 API")
    class WhenAlarmDetails{
        @Test
        @DisplayName("[성공][GET] 알랑 상세 조회 API")
        public void givenAlarmId_whenAlarmDetails_thenReturnAlarmDetailDto() throws Exception{
            //given
            long alarmId = 1L;
            String title = "알람명";
            int doseCount = 6;
            Account account = createAccount();
            List<SimpleMedicineDto> medicines = List.of(
                    createSimpleMedicineDto(1L, 200502447L, "쿨정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/1Mxwka5v0jL", "일반의약품", "알파제약(주)"),
                    createSimpleMedicineDto(2L, 200401321L, "속코정", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147428089523200037", "일반의약품", "일양약품(주)")
            );
            when(alarmService.findAlarmDetails(any(Account.class), anyLong()))
                    .thenReturn(
                            AlarmDetailDto.createAlarmDetailDto(alarmId, title, doseCount, account.getEmail(), medicines, LocalDate.of(2022,6,1))
                    );

            //when //then
            mvc.perform(get("/api/v1/alarms/{alarmId}", alarmId)
                            .with(
                                    authentication(
                                            getAuthentication(account)
                                    )
                            )
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alarmId").value(alarmId))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.doseCount").value(doseCount))
                .andExpect(jsonPath("$.email").value(account.getEmail()))
                .andExpect(jsonPath("$.medicines").isNotEmpty())
                .andExpect(jsonPath("$.medicines[0].medicineId").isNotEmpty())
                .andExpect(jsonPath("$.medicines[0].itemSeq").exists())
                .andExpect(jsonPath("$.medicines[0].itemName").exists())
                .andExpect(jsonPath("$.medicines[0].itemImage").exists())
                .andExpect(jsonPath("$.medicines[0].etcOtcName").exists())
                .andExpect(jsonPath("$.medicines[0].entpName").exists())
                .andExpect(jsonPath("$.medicines[0]._links.self").exists())
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
            ;
            verify(alarmService).findAlarmDetails(any(Account.class), anyLong());
        }
        
        @Test
        @DisplayName("[실패][GET] 없는 알람 아이디인 경우 - NOF_FOUND_ALARM_ID")
        public void givenWrongAlarmId_whenAlarmDetails_thenAlarmException() throws Exception{
            //given
            AlarmErrorCode errorCode = AlarmErrorCode.NOF_FOUND_ALARM_ID;
            Account account = createAccount();
            long wrongAlarmId = 999999L;
            when(alarmService.findAlarmDetails(any(Account.class), anyLong()))
                    .thenThrow(new AlarmException(errorCode));
            //when //then
            mvc.perform(get("/api/v1/alarms/{alarmId}", wrongAlarmId)
                            .with(
                                    authentication(
                                            getAuthentication(account)
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
            verify(alarmService).findAlarmDetails(any(Account.class), anyLong());
        }
        @Test
        @DisplayName("[실패][GET] 다른 유저 알람 아이디로 조회 한 경우 - ACCESS_DENIED_ALARM")
        public void givenOtherUserAlarmId_whenAlarmDetails_thenAlarmException() throws Exception{
            //given
            AlarmErrorCode errorCode = AlarmErrorCode.ACCESS_DENIED_ALARM;
            Account account = createAccount();
            long wrongAlarmId = 1515L;
            when(alarmService.findAlarmDetails(any(Account.class), anyLong()))
                    .thenThrow(new AlarmException(errorCode));
            //when //then
            mvc.perform(get("/api/v1/alarms/{alarmId}", wrongAlarmId)
                            .with(
                                    authentication(
                                            getAuthentication(account)
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                    .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
            verify(alarmService).findAlarmDetails(any(Account.class), anyLong());
        }
    }
}