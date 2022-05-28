package com.smrp.smartmedicinealarm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.annotation.MockAdminUser;
import com.smrp.smartmedicinealarm.dto.medicine.CreateMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.UpdateMedicineDto;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.error.code.MedicineErrorCode;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import com.smrp.smartmedicinealarm.service.medicine.MedicineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.smrp.smartmedicinealarm.entity.medicine.embedded.ClassNoAndName.createClassNoAndName;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.LengAndThick.createLengAndThick;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MarkCode.createMarkCode;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineColor.createMedicineColor;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineCompany.createMedicineCompany;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineDate.createMedicineDate;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineIdentification.createMedicineIdentification;
import static com.smrp.smartmedicinealarm.entity.medicine.embedded.MedicineLine.crateMedicineLine;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor.WHITE;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor.YELLOWISH_GREEN;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine.MINUS;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape.CIRCLE;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition.createMedicineSearchCondition;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MedicineControllerTest {
    @Autowired
    MedicineService medicineService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Nested
    @DisplayName("페이징 조회 테스트")
    class WhenFindAll{
        @ParameterizedTest
        @MethodSource("medicineSearchConditionProvider")
        @DisplayName("[성공][GET] 약품 명, 색상, 모양, 구분 선을 통한 약품 검사 테스트")
        public void givenMedicine_whenFindAllMedicine_thenReturnPageResourceSimpleMedicineDto (
                int page, int size, String itemName, List<MedicineCndShape> shapes,
                List<MedicineCndColor> colors, List<MedicineCndLine> lines
        ) throws Exception{
            //given
            MedicineSearchCondition medicineSearchCondition
                    = createMedicineSearchCondition(itemName, shapes, colors, lines);

            //when //then
            mvc.perform(get("/api/v1/medicines")
                            .queryParam("page", String.valueOf(page))
                            .queryParam("size", String.valueOf(size))
                            .content(
                                    mapper.writeValueAsBytes(medicineSearchCondition)
                            )
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    )
                    .andDo(print())
                    .andExpect(handler().handlerType(MedicineController.class))
                    .andExpect(handler().methodName("findAllMedicine"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.items").isNotEmpty())
                    .andExpect(jsonPath("$._links.self").isNotEmpty())
                    .andExpect(jsonPath("$._links.profile").isNotEmpty())
                    .andExpect(jsonPath("_embedded.items[0].medicineId").exists())
                    .andExpect(jsonPath("_embedded.items[0].itemSeq").exists())
                    .andExpect(jsonPath("_embedded.items[0].itemName").exists())
                    .andExpect(jsonPath("_embedded.items[0].itemImage").exists())
                    .andExpect(jsonPath("_embedded.items[0].etcOtcName").exists())
                    .andExpect(jsonPath("_embedded.items[0].entpName").exists())
                    .andExpect(jsonPath("$.page.totalElements").exists())
                    .andExpect(jsonPath("$.page.totalPages").exists())
                    .andExpect(jsonPath("$.page.size").value(size))
                    .andExpect(jsonPath("$.page.number").value(page))
            ;
        }
        public static Stream<Arguments> medicineSearchConditionProvider(){
            return Stream.of(
                    arguments(0, 10, null, null, null, null),
                    arguments(3, 30, "", List.of(), List.of(), List.of()),
                    arguments(0, 5, "쿨", List.of(), List.of(YELLOWISH_GREEN), List.of()),
                    arguments(0, 15, "노플정", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments(0, 10, "", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments(0, 10, "", List.of(), List.of(WHITE), List.of(MINUS))
            );
        }
    }
    @Nested
    @DisplayName("약 상세 조회 테스트")
    class MedicineDetails{
        @Test
        @DisplayName("[GET} 약 상세 정보 조회")
        public void givenMedicineId_whenMedicineDetails_thenMedicineDetailDto() throws Exception{
            //given
            long medicineId = 1L;

            //when //then
            mvc.perform(get("/api/v1/medicines/{medicineId}", medicineId)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(MedicineController.class))
                    .andExpect(handler().methodName("medicineDetails"))
                    .andExpect(jsonPath("$.medicineId").exists())
                    .andExpect(jsonPath("$.itemSeq").exists())
                    .andExpect(jsonPath("$.itemName").exists())
                    .andExpect(jsonPath("$.itemImage").exists())
                    .andExpect(jsonPath("$.etcOtcName").exists())
                    .andExpect(jsonPath("$.className").exists())
                    .andExpect(jsonPath("$.lengLong").exists())
                    .andExpect(jsonPath("$.lengShort").exists())
                    .andExpect(jsonPath("$.thick").exists())
                    .andExpect(jsonPath("$.entpName").exists())
                    .andExpect(jsonPath("$.printFront").exists())
                    .andExpect(jsonPath("$.printBack").exists())
                    .andExpect(jsonPath("$.drugShape").exists())
                    .andExpect(jsonPath("$.chart").exists())
                    .andExpect(jsonPath("$.formCodeName").exists())
                    .andExpect(jsonPath("$.lineFront").exists())
                    .andExpect(jsonPath("$.lineBack").exists())
                    .andExpect(jsonPath("$.colorFront").exists())
                    .andExpect(jsonPath("$.colorBack").exists())
                    .andExpect(jsonPath("$.colorBack").exists())
                    .andExpect(jsonPath("$.colorBack").exists())
                    .andExpect(jsonPath("$._links.self.href").exists())
                    .andExpect(jsonPath("$._links.profile.href").exists())
            ;
        }
        @Test
        @DisplayName("[실패][GET] 약 PK가 없는 경우 - MEDICINE_NOT_FOUND")
        public void givenDeleteId_whenModifyAccount_thenUserException() throws Exception{
            //given
            long notFoundMedicineId = 9999999L;
            MedicineErrorCode errorCode = MedicineErrorCode.NOT_FOUND_MEDICINE  ;

            //when //then
            mvc.perform(get("/api/v1/medicines/{id}", notFoundMedicineId)
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
        }
    }
    @Nested
    @DisplayName("약 정보 추가 하기")
    class WhenMedicineAdd{
        @Test
        @MockAdminUser
        @DisplayName("[성공][POST] 약 정보 추가하기")
        public void givenCreateMedicineDto_whenMedicineAdd_thenSuccess() throws Exception{
            //given
            CreateMedicineDto medicineDto = createMedicineDto(200611525L);

            //when //then
            mvc.perform(post("/api/v1/medicines")
                .contentType(MediaType.APPLICATION_JSON)
                    .content(
                            mapper.writeValueAsBytes(
                                    medicineDto
                            )
                    )

            )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(handler().methodName("medicineAdd"))
                .andExpect(handler().handlerType(MedicineController.class))
                .andExpect(header().string("Location", containsString("/api/v1/medicines/")))
                .andExpect(jsonPath("$.medicineId").exists())
                .andExpect(jsonPath("$.itemSeq").exists())
                .andExpect(jsonPath("$.itemName").exists())
                .andExpect(jsonPath("$.itemImage").exists())
                .andExpect(jsonPath("$.etcOtcName").exists())
                .andExpect(jsonPath("$.className").exists())
                .andExpect(jsonPath("$.lengLong").exists())
                .andExpect(jsonPath("$.lengShort").exists())
                .andExpect(jsonPath("$.thick").exists())
                .andExpect(jsonPath("$.entpName").exists())
                .andExpect(jsonPath("$.printFront").exists())
                .andExpect(jsonPath("$.printBack").exists())
                .andExpect(jsonPath("$.drugShape").exists())
                .andExpect(jsonPath("$.chart").exists())
                .andExpect(jsonPath("$.formCodeName").exists())
                .andExpect(jsonPath("$.lineFront").exists())
                .andExpect(jsonPath("$.lineBack").exists())
                .andExpect(jsonPath("$.colorFront").exists())
                .andExpect(jsonPath("$.colorBack").exists())
                .andExpect(jsonPath("$.colorBack").exists())
                .andExpect(jsonPath("$.colorBack").exists())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.profile.href").exists())
            ;
        }

        private CreateMedicineDto createMedicineDto(long seq) {
            Long itemSeq = seq;
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
            return CreateMedicineDto.createMedicineDto(itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                    medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
        }

        @Test
        @MockAdminUser
        @DisplayName("[실패][POST] 이미 추가된 약 추가 요청")
        public void givenAlreadyRegisterCreatMedicineDto_whenMedicineAdd_thenMedicineException() throws Exception{
            //given
            MedicineErrorCode errorCode = MedicineErrorCode.ALREADY_REGISTER_MEDICINE;

            //when //then
            long duplicatedItemSeq = 200611524L;
            mvc.perform(post("/api/v1/medicines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsBytes(
                                createMedicineDto(duplicatedItemSeq)
                        )
                )
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
        }
    }
    @Nested
    @DisplayName("약 정보 수정하기")
    class WhenMedicineModify{
        @Test
        @MockAdminUser
        @DisplayName("[성공][PATCH] 약정보 업데이트 API")
        public void givenDeleteId_whenModifyAccount_thenUserException() throws Exception{
            //given
            long updateMedicineId = 1L;

            //when //then
            mvc.perform(patch("/api/v1/medicines/{id}", updateMedicineId)
                .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createUpdateMedicineDto(200611525L, "전문의약품", "일반의약품")
                                    ))
            )
                .andDo(print())
                .andExpect(status().isOk())
                    .andExpect(handler().handlerType(MedicineController.class))
                    .andExpect(handler().methodName("medicineModify"))
                    .andExpect(jsonPath("$.medicineId").exists())
                    .andExpect(jsonPath("$.itemSeq").exists())
                    .andExpect(jsonPath("$.itemName").exists())
                    .andExpect(jsonPath("$.itemImage").exists())
                    .andExpect(jsonPath("$.etcOtcName").exists())
                    .andExpect(jsonPath("$.className").exists())
                    .andExpect(jsonPath("$.lengLong").exists())
                    .andExpect(jsonPath("$.lengShort").exists())
                    .andExpect(jsonPath("$.thick").exists())
                    .andExpect(jsonPath("$.entpName").exists())
                    .andExpect(jsonPath("$.printFront").exists())
                    .andExpect(jsonPath("$.printBack").exists())
                    .andExpect(jsonPath("$.drugShape").exists())
                    .andExpect(jsonPath("$.chart").exists())
                    .andExpect(jsonPath("$.formCodeName").exists())
                    .andExpect(jsonPath("$.lineFront").exists())
                    .andExpect(jsonPath("$.lineBack").exists())
                    .andExpect(jsonPath("$.colorFront").exists())
                    .andExpect(jsonPath("$.colorBack").exists())
                    .andExpect(jsonPath("$.colorBack").exists())
                    .andExpect(jsonPath("$.colorBack").exists())
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
            ;
        }

        private UpdateMedicineDto  createUpdateMedicineDto(long seq, String itemName, String etcOtcName) {
            Long itemSeq = seq;
            String itemImage =  "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149";
            ClassNoAndName classNoAndName = createClassNoAndName("01190", "기타의 중추신경용약");
            LengAndThick lengAndThick = createLengAndThick("13.0", "13.0", "3.5");
            MedicineCompany medicineCompany = createMedicineCompany(20161439L, "(주)한국로슈");
            MedicineIdentification medicineIdentification
                    = createMedicineIdentification("RO분할선C분할선HE분할선마크분할선", "십자분할선", "원형", "십자눈금이 새겨져 있는 분홍색의 원형정제이다", "나정");
            MedicineLine medicineLine = crateMedicineLine("+", "+");
            MedicineColor medicineColor = createMedicineColor("분홍", "");
            MarkCode markCode = createMarkCode("육각형", "", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147938640332900169", "");
            MedicineDate medicineDate = createMedicineDate(null, LocalDate.parse("2004-12-22"), LocalDate.parse("2020-02-27"));
            return UpdateMedicineDto.createUpdateMedicineDto(itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                    medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
        }
        @Test
        @MockAdminUser
        @DisplayName("[실패][PATCH] 존재하지 않는 medicineId로 업데이트 요청을 보낸 경우 - NOT_FOUND_MEDICINE ")
        public void givenNotFoundMedicineId_whenMedicineModify_thenMedicineException() throws Exception{
            //given
            MedicineErrorCode errorCode = MedicineErrorCode.NOT_FOUND_MEDICINE;
            //when //then
            long notFoundMedicineId = 9999999999L;

            //when //then
            mvc.perform(patch("/api/v1/medicines/{id}", notFoundMedicineId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createUpdateMedicineDto(200611525L, "전문의약품", "일반의약품")
                                    ))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                    .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
        }

        @Test
        @MockAdminUser
        @DisplayName("[실패][PATCH] 존재하지 않는 medicineId로 업데이트 요청을 보낸 경우 - NOT_FOUND_MEDICINE ")
        public void givenDuplicatedItemSeq_whenMedicineModify_thenMedicineException() throws Exception{
            //given
            MedicineErrorCode errorCode = MedicineErrorCode.DUPLICATED_MEDICINE_ITEMSEQ;
            //when //then
            long medicineId = 1L;

            //when //then
            mvc.perform(patch("/api/v1/medicines/{id}", medicineId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    mapper.writeValueAsString(
                                            createUpdateMedicineDto(200611524L, "전문의약품", "일반의약품")
                                    ))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                    .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
            ;
        }
    }

}