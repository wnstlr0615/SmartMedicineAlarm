package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.CreateMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.MedicineDetailsDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.UpdateMedicineDto;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import org.springframework.data.domain.Page;

public interface MedicineService {
    /**약품 리스트 조회 */
    Page<SimpleMedicineDto> findAllMedicine(int page, int size, MedicineSearchCondition medicineSearchCondition);

    /** 약품 상세 조회 */
    MedicineDetailsDto findMedicineDetails(Long medicineId);

    /** 약품 추가*/
    MedicineDetailsDto addMedicine(CreateMedicineDto medicineDto);

    /** 약품 정보 수정*/
    MedicineDetailsDto modifyMedicine(Long medicineId, UpdateMedicineDto medicineDto);
    /** 약품 제거 하기 */
    void removeMedicine(Long medicineId);
}
