package com.smrp.smartmedicinealarm.repository.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MedicineCustomRepository {
    Page<SimpleMedicineDto> findSimpleMedicineAllBySearchCond(PageRequest pageRequest, MedicineSearchCondition condition);
    Page<Medicine> findMedicineAllBySearchCond(PageRequest pageRequest, MedicineSearchCondition condition);

}
