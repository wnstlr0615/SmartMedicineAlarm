package com.smrp.smartmedicinealarm.repository.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor.*;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine.MINUS;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine.PLUS;
import static com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape.CIRCLE;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SpringBootTest
@ActiveProfiles("test")
class MedicineCustomRepositoryImplTest {
    @Autowired
    MedicineCustomRepositoryImpl medicineCustomRepository;

    @Nested
    @DisplayName("페이징 조회 테스트")
    class whenFindAllBySearchCond{

        @Test
        @DisplayName("[성공] 입력 값 이 null 이여도 문제 없이 조회 되는지 확인")
        public void givenNull_whenFindSimpleMedicineAllBySearchCond_thenSuccess(){
            //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond(null, null, null, null);

            //when
            PageRequest pageRequest = PageRequest.of(0, 20);
            Page<SimpleMedicineDto> pageSimpleMedicineDto = medicineCustomRepository.findSimpleMedicineAllBySearchCond(pageRequest, condition);
            assertAll(
                    () -> assertThat(pageSimpleMedicineDto.getSize()).isEqualTo(pageRequest.getPageSize()),
                    () -> assertThat(pageSimpleMedicineDto.getNumber()).isEqualTo(pageRequest.getPageNumber())
            );
        }

        @ParameterizedTest
        @MethodSource("pageRequestProvider")
        @DisplayName("[성공]페이징 처리가 잘 되는지 확인")
        public void givenPageSize_whenFindSimpleMedicineAllBySearchCond_thenSuccess(PageRequest pageRequest){
            //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond("", List.of(), List.of(), List.of());

            //when
            Page<SimpleMedicineDto> pageSimpleMedicineDto = medicineCustomRepository.findSimpleMedicineAllBySearchCond(pageRequest, condition);
            assertAll(
                    () -> assertThat(pageSimpleMedicineDto.getSize()).isEqualTo(pageRequest.getPageSize()),
                    () -> assertThat(pageSimpleMedicineDto.getNumber()).isEqualTo(pageRequest.getPageNumber())
            );
        }
        public static Stream<Arguments> pageRequestProvider(){
            return Stream.of(
                    arguments(PageRequest.of(0, 10)),
                    arguments(PageRequest.of(1, 10)),
                    arguments(PageRequest.of(2, 100)),
                    arguments(PageRequest.of(0, 50)),
                    arguments(PageRequest.of(3, 20))
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"쿨", "알", "아즈정", "소독정", "정", "알비스", "다온", "타리노정"})
        @DisplayName("[성공] 약품명을 통한 페이징 테스트")
        public void givenItemName_whenFindAllBySearchCond_thenSuccess (String searchName) {
            //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond(searchName, List.of(), List.of(), List.of());
            PageRequest pageRequest = PageRequest.of(0, 10);
            //when
            Page<SimpleMedicineDto> pageSimpleMedicineDto = medicineCustomRepository.findSimpleMedicineAllBySearchCond(pageRequest, condition);

            //then
            List<SimpleMedicineDto> contents = pageSimpleMedicineDto.getContent();
            assertThat(contents).map(SimpleMedicineDto::getItemName)
                    .allSatisfy(
                            itemName -> assertThat(itemName).contains(searchName)
                    ).as("'쿨' 검색 한경우 조회한 모든 약품 명에 쿨이 포함하는지 확인");

        }
        @ParameterizedTest
        @MethodSource(value = "shapesProvider")
        @DisplayName("[성공] 모양을 통한 페이징 테스트")
        public void givenShapeList_whenFindAllBySearchCond_thenSuccess (List<MedicineCndShape> shapes) {
             //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond("", shapes, List.of(), List.of());
            PageRequest pageRequest = PageRequest.of(0, 50);
            //when
            Page<Medicine> pageMedicines = medicineCustomRepository.findMedicineAllBySearchCond(pageRequest, condition);

            //then
            List<Medicine> medicines = pageMedicines.getContent();
            assertThat(medicines).map(Medicine::getMedicineIdentification).allSatisfy(medicineIdentification ->
                    assertThat(medicineIdentification.getDrugShape())
                            .isIn(
                                    shapes.stream().map(MedicineCndShape::getDescription).collect(toList()
                                    )
                    )
            ).as("입력받은 모양들을 을 앞이나 뒤에 모두  포함 하는지 확인");
        }
        public static Stream<Arguments> shapesProvider() {
            return Stream.of(
                    arguments(List.of(CIRCLE, MedicineCndShape.OVAL)),
                    arguments(List.of(MedicineCndShape.RECTANGLE, MedicineCndShape.TRIANGLE)),
                    arguments(List.of(MedicineCndShape.LONG_RECTANGLE, MedicineCndShape.HEXAGON)),
                    arguments(List.of(CIRCLE, MedicineCndShape.PENTAGON)),
                    arguments(List.of( MedicineCndShape.LONG_RECTANGLE))
            );
        }

        @ParameterizedTest
        @DisplayName("[성공] 약 색상을 통한 페이징 테스트")
        @MethodSource("colorsProvider")
        public void givenColorList_whenFindAllBySearchCond_thenSuccess (List<MedicineCndColor> colors) {
            //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond("", List.of(), colors, List.of());
            PageRequest pageRequest = PageRequest.of(0, 50);
            //when
            Page<Medicine> pageMedicines = medicineCustomRepository.findMedicineAllBySearchCond(pageRequest, condition);

            //then
            List<Medicine> medicines = pageMedicines.getContent();
            assertThat(medicines).map(Medicine::getMedicineColor).anySatisfy(medicineColor ->
                assertThat(List.of(medicineColor.getColorFront(), medicineColor.getColorBack()))
                        .containsAnyElementsOf(
                                colors.stream().map(MedicineCndColor::getDescription).collect(toList())
                        )
            ).as("약 앞면 색상과 뒷면 색상중 하나 라도 주황, 보라, 검정을 포함");
        }
        public static Stream<Arguments> colorsProvider(){
            return Stream.of(
                    arguments(List.of(ORANGE, BLUE, GRAY)),
                    arguments(List.of(WHITE, BROWN, TURQUOISE)),
                    arguments(List.of(YELLOW)),
                    arguments(List.of(BROWN, BLUE)),
                    arguments(List.of(PURPLE, TURQUOISE, GRAY)),
                    arguments(List.of(BLUE, BROWN))
            );
        }

        @ParameterizedTest
        @MethodSource("linesProvider")
        @DisplayName("[성공] 약 분할선을 을 통한 페이징 테스트")
        public void givenMedicineLineList_whenFindAllBySearchCond_thenSuccess (List<MedicineCndLine> lines) {
            //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond("", List.of(), List.of(), lines);
            PageRequest pageRequest = PageRequest.of(0, 50);

            //when
            Page<Medicine> pageMedicines = medicineCustomRepository.findMedicineAllBySearchCond(pageRequest, condition);

            //then
            List<Medicine> medicines = pageMedicines.getContent();
            assertThat(medicines).map(Medicine::getMedicineLine).anySatisfy(medicineLine ->
                assertThat(List.of(medicineLine.getLineFront(), medicineLine.getLineBack()))
                        .containsAnyElementsOf(
                                lines.stream().map(MedicineCndLine::getDescription).collect(toList())
                        )
            ).as("약 앞면 색상과 뒷면 색상중 하나 라도 주황, 보라, 검정을 포함");
        }

        public static Stream<Arguments> linesProvider(){
            return Stream.of(
                arguments(List.of(MINUS)),
                arguments(List.of(PLUS)),
                arguments(List.of(PLUS, MINUS))
            );
        }

        @ParameterizedTest
        @MethodSource("medicineConditionSearchProvider")
        @DisplayName("[성공] 여러 조건을 통해서 데이터 가져오기 ")
        public void givenMedicineConditionSearch_whenFindAllBySearchCond_thenSuccess (
                String itemName, List<MedicineCndShape> shapes,
                List<MedicineCndColor> colors, List<MedicineCndLine> lines
        ) {
            //given
            MedicineSearchCondition condition
                    = createMedicineSearchCond(itemName, shapes, colors, lines);
            PageRequest pageRequest = PageRequest.of(0, 50);

            //when
            Page<Medicine> pageMedicines = medicineCustomRepository.findMedicineAllBySearchCond(pageRequest, condition);

            //then
            List<Medicine> medicines = pageMedicines.getContent();
            assertThat(medicines).allSatisfy(medicine -> {
                if(StringUtils.hasText(itemName)) {
                    assertThat(medicine.getItemName()).contains(itemName).as("이름 조건을 만족하는지 확인");
                }
                if(!shapes.isEmpty()) {
                    assertThat(
                            medicine.getMedicineIdentification().getDrugShape())
                            .isIn(shapes.stream().map(MedicineCndShape::getDescription).collect(toList()))
                            .as("약 모양이 선택한 조건을 만족 하는지 확인");
                }
                if(!colors.isEmpty()) {
                    assertThat(
                            List.of(medicine.getMedicineColor().getColorFront(), medicine.getMedicineColor().getColorBack()))
                            .containsAnyElementsOf(colors.stream().map(MedicineCndColor::getDescription).collect(toList()))
                            .as("약 앞이나 뒤 색상이 선택한 조건을 만족 하는지 확인");
                }
                if(!lines.isEmpty()) {
                    assertThat(
                            List.of(medicine.getMedicineLine().getLineFront(), medicine.getMedicineLine().getLineBack()))
                            .containsAnyElementsOf(lines.stream().map(MedicineCndLine::getDescription).collect(toList()))
                            .as("약 앞이나 뒤 구분 선이 선택한 조건을 만족 하는지 확인");
                }

            }).as("약품명, 색상, 모양, 구분 선 조건 검색을 모두 만족 하는지 확인");
        }

        public static Stream<Arguments> medicineConditionSearchProvider(){
            return Stream.of(
                    arguments("쿨", List.of(), List.of(YELLOWISH_GREEN), List.of()),
                    arguments("노플정", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments("", List.of(CIRCLE), List.of(WHITE), List.of()),
                    arguments("", List.of(), List.of(WHITE), List.of(MINUS))
            );
        }
    }

    private MedicineSearchCondition createMedicineSearchCond(String itemName, List<MedicineCndShape> shapes, List<MedicineCndColor> colors, List<MedicineCndLine> lines) {
        return MedicineSearchCondition.createMedicineSearchCondition(itemName, shapes, colors, lines);
    }
}