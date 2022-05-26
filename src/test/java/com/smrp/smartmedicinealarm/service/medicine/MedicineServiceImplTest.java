package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor.WHITE;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor.YELLOWISH_GREEN;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine.MINUS;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape.CIRCLE;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition.createMedicineSearchCondition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@ActiveProfiles("test")
class MedicineServiceImplTest {
    @Autowired
    MedicineService medicineService;
    @Nested
    @DisplayName("페이징 조회 테스트")
    class WhenFindAll {
        @ParameterizedTest
        @MethodSource("medicineSearchConditionProvider")
        @DisplayName("[성공] 약품 명, 색상, 모양, 구분 선을 통한 약품 검사 테스트")
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
                    arguments(0, 5, "쿨", List.of(), List.of(YELLOWISH_GREEN), List.of()),
                    arguments(0, 15, "노플정", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments(0, 10, "", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments(0, 10, "", List.of(), List.of(WHITE), List.of(MINUS))
            );
        }
    }
}