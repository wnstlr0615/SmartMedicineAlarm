package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.MedicineDetailsDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.error.code.MedicineErrorCode;
import com.smrp.smartmedicinealarm.error.exception.MedicineException;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import com.smrp.smartmedicinealarm.repository.medicine.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService{
    private final MedicineRepository medicineRepository;

    @Override
    public Page<SimpleMedicineDto> findAllMedicine(int page, int size, MedicineSearchCondition condition) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return medicineRepository.findSimpleMedicineAllBySearchCond(pageRequest, condition);
    }

    @Override
    public MedicineDetailsDto findMedicineDetails(Long medicineId) {
        Medicine medicine = getMedicine(medicineId);
        return MedicineDetailsDto.fromEntity(medicine);
    }

    private Medicine getMedicine(Long medicineId) {
        return medicineRepository.findById(medicineId)
                .orElseThrow(() -> new MedicineException(MedicineErrorCode.NOT_FOUND_MEDICINE));
    }
}
