package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.dto.medicine.CreateMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.MedicineDetailsDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.UpdateMedicineDto;
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

    @Override
    @Transactional
    public MedicineDetailsDto addMedicine(CreateMedicineDto medicineDto) {
        validAlreadyRegisterMedicine(medicineDto);
        Medicine medicine = saveMedicine(medicineDto);
        return MedicineDetailsDto.fromEntity(medicine);

    }

    @Override
    @Transactional
    public MedicineDetailsDto modifyMedicine(Long medicineId, UpdateMedicineDto medicineDto) {
        Medicine medicine = getMedicine(medicineId);
        validDuplicatedItemSeq(medicineDto.getItemSeq());
        updateMedicine(medicine, medicineDto);
        return MedicineDetailsDto.fromEntity(medicine);
    }

    @Override
    @Transactional
    public void removeMedicine(Long medicineId) {
        Medicine medicine = getMedicine(medicineId);
        medicineRepository.delete(medicine);
    }

    private void validDuplicatedItemSeq(Long itemSeq) {
        if(medicineRepository.countByItemSeq(itemSeq) > 0){
            throw new MedicineException(MedicineErrorCode.DUPLICATED_MEDICINE_ITEMSEQ);
        }
    }

    private void updateMedicine(Medicine medicine, UpdateMedicineDto medicineDto) {
        medicine.updateByDto(medicineDto);
    }

    private void validAlreadyRegisterMedicine(CreateMedicineDto medicineDto) {
        if(medicineRepository.countByItemSeq(medicineDto.getItemSeq()) > 0){
            throw new MedicineException(MedicineErrorCode.ALREADY_REGISTER_MEDICINE);
        }
    }

    private Medicine saveMedicine(CreateMedicineDto medicineDto) {
        Medicine medicine = medicineDto.toEntity();
        return medicineRepository.save(medicine);
    }

    private Medicine getMedicine(Long medicineId) {
        return medicineRepository.findById(medicineId)
                .orElseThrow(() -> new MedicineException(MedicineErrorCode.NOT_FOUND_MEDICINE));
    }
}
