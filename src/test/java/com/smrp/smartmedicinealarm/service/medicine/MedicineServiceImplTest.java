package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.CreateMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.MedicineDetailsDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.UpdateMedicineDto;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.error.code.MedicineErrorCode;
import com.smrp.smartmedicinealarm.error.exception.MedicineException;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MedicineServiceImplTest {
    @Autowired
    MedicineService medicineService;
    @Nested
    @DisplayName("????????? ?????? ?????????")
    class WhenFindAll {
        @ParameterizedTest
        @MethodSource("medicineSearchConditionProvider")
        @DisplayName("[??????] ?????? ???, ??????, ??????, ?????? ?????? ?????? ?????? ?????? ?????????")
        public void givenMedicine_whenFindAllMedicine_thenReturnPageResourceSimpleMedicineDto(
                int page, int size, String itemName, List<MedicineCndShape> shapes,
                List<MedicineCndColor> colors, List<MedicineCndLine> lines
        ) {
            //given
            MedicineSearchCondition  condition = createMedicineSearchCondition(itemName, shapes,colors, lines);
            //when
            Page<SimpleMedicineDto> pageSimpleMedicineDtos = medicineService.findAllMedicine(page, size, condition);

            //then
            assertAll(
                    () -> assertThat(pageSimpleMedicineDtos).allSatisfy(simpleMedicineDtos ->
                                assertThat(simpleMedicineDtos)
                                        .hasFieldOrProperty("medicineId")
                                        .hasFieldOrProperty("itemSeq")
                                        .hasFieldOrProperty("itemName")
                                        .hasFieldOrProperty("itemImage")
                                        .hasFieldOrProperty("etcOtcName")
                                        .hasFieldOrProperty("entpName")
                ),
                    () -> assertThat(pageSimpleMedicineDtos.getTotalElements()).isNotNull(),
                    () -> assertThat(pageSimpleMedicineDtos.getTotalPages()).isNotNull(),
                    () -> assertThat(pageSimpleMedicineDtos.getNumber()).isEqualTo(page),
                    () -> assertThat(pageSimpleMedicineDtos.getSize()).isEqualTo(size)
            );

        }
        public static Stream<Arguments> medicineSearchConditionProvider(){
            return Stream.of(
                    arguments(0, 10, null, null, null, null),
                    arguments(3, 30, "", List.of(), List.of(), List.of()),
                    arguments(0, 5, "???", List.of(), List.of(YELLOWISH_GREEN), List.of()),
                    arguments(0, 15, "?????????", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments(0, 10, "", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments(0, 10, "", List.of(), List.of(WHITE), List.of(MINUS))
            );
        }
    }
    @Nested
    @DisplayName("??? ?????? ?????? ?????? ?????????")
    class WhenMedicineDetails{
        @ParameterizedTest
        @ValueSource(longs = {1,2,3,4,5})
        public void givenMedicineId_whenMedicineDetails_thenReturnMedicineDetailDto(Long medicineId){
            //given
            //when
            MedicineDetailsDto medicineDetailsDto = medicineService.findMedicineDetails(medicineId);
            //then
            assertThat(medicineDetailsDto)
                    .hasFieldOrProperty("medicineId").isNotNull()
                    .hasFieldOrProperty("itemSeq").isNotNull()
                    .hasFieldOrProperty("itemName").isNotNull()
                    .hasFieldOrProperty("itemImage").isNotNull()
                    .hasFieldOrProperty("etcOtcName").isNotNull()
                    .hasFieldOrProperty("className").isNotNull()
                    .hasFieldOrProperty("lengLong").isNotNull()
                    .hasFieldOrProperty("lengShort").isNotNull()
                    .hasFieldOrProperty("thick").isNotNull()
                    .hasFieldOrProperty("entpName").isNotNull()
                    .hasFieldOrProperty("printFront").isNotNull()
                    .hasFieldOrProperty("printBack").isNotNull()
                    .hasFieldOrProperty("drugShape").isNotNull()
                    .hasFieldOrProperty("chart").isNotNull()
                    .hasFieldOrProperty("formCodeName").isNotNull()
                    .hasFieldOrProperty("lineFront").isNotNull()
                    .hasFieldOrProperty("lineBack").isNotNull()
                    .hasFieldOrProperty("colorFront").isNotNull()
                    .hasFieldOrProperty("colorBack").isNotNull()
                    ;
        }
    }
    @Test
    @DisplayName("[??????] ???????????? ?????? ?????? ?????? - NOT_FOUND_MEDICINE")
    public void givenNotFoundMedicineId_whenMedicineDetails_thenMedicineException(){
        //given
        long NotFoundMedicineId = 99999999999L;
        final MedicineErrorCode errorCode = MedicineErrorCode.NOT_FOUND_MEDICINE;
        //when
        final MedicineException exception = assertThrows(MedicineException.class,
            () ->  medicineService.findMedicineDetails(NotFoundMedicineId)
        )
        ;
        //then
        assertAll(
            () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
            () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
    }

    @Nested
    @DisplayName("?????? ?????? ?????????")
    class WhenMedicineAdd{

        @Test
        @DisplayName("[??????] ?????? ?????? ?????????")
        public void givenCreateMedicineDto_whenAddMedicine_thenReturnMedicineDetailsDto(){
            //given
            CreateMedicineDto medicineDto = createMedicineDto(200611525);
            //when
            MedicineDetailsDto medicineDetailsDto = medicineService.addMedicine(medicineDto);
            //then
            assertThat(medicineDetailsDto)
                    .hasFieldOrProperty("medicineId").isNotNull()
                    .hasFieldOrPropertyWithValue("itemSeq", medicineDto.getItemSeq())
                    .hasFieldOrPropertyWithValue("itemName", medicineDto.getItemName())
                    .hasFieldOrPropertyWithValue("itemImage", medicineDto.getItemImage())
                    .hasFieldOrPropertyWithValue("etcOtcName", medicineDto.getEtcOtcName())
                    .hasFieldOrPropertyWithValue("className", medicineDto.getClassName())
                    .hasFieldOrPropertyWithValue("lengLong", medicineDto.getLengLong())
                    .hasFieldOrPropertyWithValue("lengShort", medicineDto.getLengShort())
                    .hasFieldOrPropertyWithValue("thick", medicineDto.getThick())
                    .hasFieldOrPropertyWithValue("entpName", medicineDto.getEntpName())
                    .hasFieldOrPropertyWithValue("printFront", medicineDto.getPrintFront())
                    .hasFieldOrPropertyWithValue("printBack", medicineDto.getPrintBack())
                    .hasFieldOrPropertyWithValue("drugShape", medicineDto.getDrugShape())
                    .hasFieldOrPropertyWithValue("chart", medicineDto.getChart())
                    .hasFieldOrPropertyWithValue("formCodeName", medicineDto.getFormCodeName())
                    .hasFieldOrPropertyWithValue("lineFront", medicineDto.getLineFront())
                    .hasFieldOrPropertyWithValue("lineBack", medicineDto.getLineBack())
                    .hasFieldOrPropertyWithValue("colorFront", medicineDto.getColorFront())
                    .hasFieldOrPropertyWithValue("colorBack", medicineDto.getColorBack())
            ;

        }
        private CreateMedicineDto createMedicineDto(long seq) {
            Long itemSeq = seq;
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
            return CreateMedicineDto.createMedicineDto(itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                    medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
        }

        @Test
        @DisplayName("[??????] ?????? ????????? ??? ?????? - ALREADY_REGISTER_MEDICINE")
        public void givenDuplicateItemSeq()  {
            //given
            MedicineErrorCode errorCode = MedicineErrorCode.ALREADY_REGISTER_MEDICINE;

            //when
            final MedicineException exception = assertThrows(MedicineException.class,
                    () ->  medicineService.addMedicine(createMedicineDto(200611524L))
            )
                    ;
            //then
            assertAll(
                    () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                    () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );

        }
    }
    @Nested
    @DisplayName("?????? ???????????? ?????????")
    class WhenMedicineModify{
        @Test
        @DisplayName("[??????] ?????? ???????????? ")
        public void givenUpdateMedicineDto_whenModifyMedicineDto_thenSuccess(){
            //given
            long medicineId = 1L;
            UpdateMedicineDto updateMedicineDto = createUpdateMedicineDto(200611525, "????????????", "???????????????");
            //when

            MedicineDetailsDto medicineDetailsDto = medicineService.modifyMedicine(medicineId, updateMedicineDto);
            //then

            assertThat(medicineDetailsDto)
                    .hasFieldOrPropertyWithValue("medicineId", medicineId)
                    .hasFieldOrPropertyWithValue("itemSeq", medicineDetailsDto.getItemSeq())
                    .hasFieldOrPropertyWithValue("itemName", medicineDetailsDto.getItemName())
                    .hasFieldOrPropertyWithValue("itemImage", medicineDetailsDto.getItemImage())
                    .hasFieldOrPropertyWithValue("etcOtcName", medicineDetailsDto.getEtcOtcName())
                    .hasFieldOrPropertyWithValue("className", medicineDetailsDto.getClassName())
                    .hasFieldOrPropertyWithValue("lengLong", medicineDetailsDto.getLengLong())
                    .hasFieldOrPropertyWithValue("lengShort", medicineDetailsDto.getLengShort())
                    .hasFieldOrPropertyWithValue("thick", medicineDetailsDto.getThick())
                    .hasFieldOrPropertyWithValue("entpName", medicineDetailsDto.getEntpName())
                    .hasFieldOrPropertyWithValue("printFront", medicineDetailsDto.getPrintFront())
                    .hasFieldOrPropertyWithValue("printBack", medicineDetailsDto.getPrintBack())
                    .hasFieldOrPropertyWithValue("drugShape", medicineDetailsDto.getDrugShape())
                    .hasFieldOrPropertyWithValue("chart", medicineDetailsDto.getChart())
                    .hasFieldOrPropertyWithValue("formCodeName", medicineDetailsDto.getFormCodeName())
                    .hasFieldOrPropertyWithValue("lineFront", medicineDetailsDto.getLineFront())
                    .hasFieldOrPropertyWithValue("lineBack", medicineDetailsDto.getLineBack())
                    .hasFieldOrPropertyWithValue("colorFront", medicineDetailsDto.getColorFront())
                    .hasFieldOrPropertyWithValue("colorBack", medicineDetailsDto.getColorBack())
                    ;
        }
    }
    private UpdateMedicineDto createUpdateMedicineDto(long seq, String itemName, String etcOtcName) {
        Long itemSeq = seq;
        String itemImage =  "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/148609543321800149";
        ClassNoAndName classNoAndName = createClassNoAndName("01190", "????????? ??????????????????");
        LengAndThick lengAndThick = createLengAndThick("13.0", "13.0", "3.5");
        MedicineCompany medicineCompany = createMedicineCompany(20161439L, "(???)????????????");
        MedicineIdentification medicineIdentification
                = createMedicineIdentification("RO?????????C?????????HE????????????????????????", "???????????????", "??????", "??????????????? ????????? ?????? ???????????? ??????????????????", "??????");
        MedicineLine medicineLine = crateMedicineLine("+", "+");
        MedicineColor medicineColor = createMedicineColor("??????", "");
        MarkCode markCode = createMarkCode("?????????", "", "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147938640332900169", "");
        MedicineDate medicineDate = createMedicineDate(null, LocalDate.parse("2004-12-22"), LocalDate.parse("2020-02-27"));
        return UpdateMedicineDto.createUpdateMedicineDto(itemSeq, itemName, itemImage, etcOtcName, classNoAndName, lengAndThick,
                medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate);
    }
    @Test
    @DisplayName("[??????] ?????????????????? medicineId ??? ????????? ?????? - NOT_FOUND_MEDICINE")
    public void givenNotFoundMedicineId_whenMedicineModify_thenMedicineException(){
        //given
        Long noFoundMedicineId = 99999999L;
        final MedicineErrorCode errorCode = MedicineErrorCode.NOT_FOUND_MEDICINE;

        //when
        final MedicineException exception = assertThrows(MedicineException.class,
            () -> medicineService.modifyMedicine(noFoundMedicineId, createUpdateMedicineDto(200611525, "????????????", "???????????????"))
        )
        ;
        //then
        assertAll(
            () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
            () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
    }
    @Test
    @DisplayName("[??????]  ???????????? ??? ?????? itemSeq??? ?????? ?????? ?????? - DUPLICATED_MEDICINE_ITEMSEQ")
    public void givenDuplicateItemSeq_whenMedicineModify_thenMedicineException(){
        //given
        Long medicineId = 1L;
        final MedicineErrorCode errorCode = MedicineErrorCode.DUPLICATED_MEDICINE_ITEMSEQ;

        //when
        final MedicineException exception = assertThrows(MedicineException.class,
                () -> medicineService.modifyMedicine(medicineId, createUpdateMedicineDto(200611524L, "????????????", "???????????????"))
        )
                ;
        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
    }
    @Nested
    @DisplayName("?????? ?????? ??????")
    class WhenMedicineRemove{
        @Test
        @DisplayName("[??????]?????? ???????????? - ?????? ??? ???????????? NOF_FOUND_MEDICINE ?????? ??????")
        public void givenMedicineId_whenMedicineRemove_thenSuccess(){
            //given
            long medicineId = 1L;
            MedicineErrorCode errorCode = MedicineErrorCode.NOT_FOUND_MEDICINE;

            //when
            medicineService.removeMedicine(medicineId);
            MedicineException exception = assertThrows(MedicineException.class,
                    () -> medicineService.removeMedicine(1L));

            //then
            assertAll(
                    () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                    () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
            );

        }
    }
    
}