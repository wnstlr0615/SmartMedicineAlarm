package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.MedicineDetailsDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import org.springframework.data.domain.Page;

public interface MedicineService {
    /**약품 리스트 조회 */
    Page<SimpleMedicineDto> findAllMedicine(int page, int size, MedicineSearchCondition medicineSearchCondition);

    /** 약품 상세 조회 */
    MedicineDetailsDto findMedicineDetails(Long medicineId);
}
