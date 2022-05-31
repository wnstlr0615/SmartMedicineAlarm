package com.smrp.smartmedicinealarm.repository.medicine;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smrp.smartmedicinealarm.dto.medicine.QSimpleMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.QMedicine;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndColor;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndLine;
import com.smrp.smartmedicinealarm.model.medicine.MedicineCndShape;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.smrp.smartmedicinealarm.entity.medicine.QMedicine.medicine;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MedicineCustomRepositoryImpl  implements MedicineCustomRepository{

    private final JPAQueryFactory factory;

    @Override
    public Page<SimpleMedicineDto> findSimpleMedicineAllBySearchCond(PageRequest pageRequest, MedicineSearchCondition condition) {
        QMedicine qm = medicine;
        Predicate itemNameContains = itemNameContains(condition.getItemName());
        BooleanExpression medicineShapeIn = medicineShapeIn(condition.getShapes());
        BooleanExpression medicineColorIn = medicineColorIn(condition.getColors());
        BooleanExpression medicineLineIn = medicineLineIn(condition.getLines());
        List<SimpleMedicineDto> simpleMedicineDtos = factory.select(
                        new QSimpleMedicineDto(qm.medicineId, qm.itemSeq, qm.itemName, qm.itemImage,
                                qm.etcOtcName, qm.medicineCompany.entpName))
                .from(qm)
                .where(itemNameContains, medicineShapeIn, medicineColorIn, medicineLineIn)
                .orderBy(medicine.medicineId.asc())
                .limit(pageRequest.getPageSize())
                .offset(pageRequest.getOffset())
                .fetch();
        long totalCnt = factory.selectFrom(qm)
                .where(itemNameContains, medicineShapeIn, medicineColorIn, medicineLineIn)
                .limit(pageRequest.getPageSize())
                .offset(pageRequest.getOffset())
                .fetchCount();
        return new PageImpl<>(simpleMedicineDtos, pageRequest, totalCnt);
    }

    @Override
    public Page<Medicine> findMedicineAllBySearchCond(PageRequest pageRequest, MedicineSearchCondition condition) {
        QMedicine qm = medicine;
        Predicate itemNameContains = itemNameContains(condition.getItemName());
        BooleanExpression medicineShapeIn = medicineShapeIn(condition.getShapes());
        BooleanExpression medicineColorIn = medicineColorIn(condition.getColors());
        BooleanExpression medicineLineIn = medicineLineIn(condition.getLines());
        List<Medicine> medicines = factory.selectFrom(qm)
                .where(itemNameContains, medicineShapeIn, medicineColorIn, medicineLineIn)
                .orderBy(medicine.itemSeq.asc())
                .limit(pageRequest.getPageSize())
                .offset(pageRequest.getOffset())
                .fetch();
        long totalCnt = factory.selectFrom(qm)
                .where(itemNameContains, medicineShapeIn, medicineColorIn, medicineLineIn)
                .limit(pageRequest.getPageSize())
                .offset(pageRequest.getOffset())
                .fetchCount();
        return new PageImpl<>(medicines, pageRequest, totalCnt);
    }

    private BooleanExpression medicineLineIn(List<MedicineCndLine> lines) {
        List<String> lineNames;
        if(lines == null || lines.size() == 0) return null;
        if(lines.contains(MedicineCndLine.NONE)){
            return null;
        }
        if(!lines.contains(MedicineCndLine.OTHER)){
            lineNames = lines.stream()
                    .map(MedicineCndLine::getDescription).collect(Collectors.toList());
            return medicine.medicineLine.lineFront.in(lineNames)
                    .or(medicine.medicineLine.lineBack.in(lineNames));
        }
        //기타가 포함된 경우
        lineNames = getNotLines(lines);
        return medicine.medicineLine.lineFront.notIn(lineNames)
                .or(medicine.medicineLine.lineBack.notIn(lineNames));

    }



    private List<String> getNotLines(List<MedicineCndLine> medicineCndLines) {
        medicineCndLines.remove(MedicineCndLine.OTHER);
        List<MedicineCndLine> cndLines = Arrays.stream(MedicineCndLine.values())
                .collect(Collectors.toList());
        cndLines.removeAll(medicineCndLines);
        List<String> lines = cndLines.stream().map(MedicineCndLine::getDescription).collect(Collectors.toList());
        lines.add("");
        return lines;
    }

    private BooleanExpression medicineColorIn(List<MedicineCndColor> colors) {

        List<String> colorNames;
        if(colors == null || colors.size() == 0) return null;

        if(colors.contains(MedicineCndColor.NONE)){
            return null;
        }
        if(!colors.contains(MedicineCndColor.OTHER)){
            colorNames = colors.stream().map(MedicineCndColor::getDescription).collect(Collectors.toList());
            return medicine.medicineColor.colorFront.in(colorNames).or(medicine.medicineColor.colorBack.in(colorNames));
        }

        colorNames = getNotColors(colors); // 기타 +선택 항목에 반대인 Color 리스트 반환
        return medicine.medicineColor.colorFront.notIn(colorNames).or(medicine.medicineColor.colorBack.notIn(colorNames));
    }

    private List<String> getNotColors(List<MedicineCndColor> colorList) {
        colorList.remove(MedicineCndColor.OTHER);
        List<MedicineCndColor> medicineCndColors = new ArrayList<>(List.of(MedicineCndColor.values()));
        medicineCndColors.removeAll(colorList);
        List<String> colorNames = medicineCndColors.stream().map(MedicineCndColor::getDescription).collect(Collectors.toList());
        colorNames.add("");
        return colorNames;
    }

    private BooleanExpression medicineShapeIn(List<MedicineCndShape> shape) {
        if(shape == null || shape.size() == 0) return null;

        if(shape.contains(MedicineCndShape.NONE)){
            return null;
        }
        if(!shape.contains(MedicineCndShape.OTHER)){
            List<String> shapeList = shape.stream().map(MedicineCndShape::getDescription).collect(Collectors.toList());
            return medicine.medicineIdentification.drugShape.in(shapeList);
        }
        //기타가 포함된 경우
        List<String> shapes = getNotShapes(shape); //선택된 항목에 반대
        return medicine.medicineIdentification.drugShape.notIn(shapes);
    }

    private List<String> getNotShapes(List<MedicineCndShape> shape) {
        shape.remove(MedicineCndShape.OTHER);
        List<MedicineCndShape> cndShapes = Arrays.stream(MedicineCndShape.values()).collect(Collectors.toList());
        cndShapes.removeAll(shape);
        List<String> shapes = cndShapes.stream().map(MedicineCndShape::getDescription).collect(Collectors.toList());
        shapes.add("");
        return shapes;
    }

    private Predicate itemNameContains(String keyword) {
        if(!StringUtils.hasText(keyword)){
            return null;
        }
        return medicine.itemName.contains(keyword);
    }
}
