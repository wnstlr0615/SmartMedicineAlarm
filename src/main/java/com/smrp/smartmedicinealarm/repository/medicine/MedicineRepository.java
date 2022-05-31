package com.smrp.smartmedicinealarm.repository.medicine;

import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> ,MedicineCustomRepository{
    int countByItemSeq(Long itemSeq);

    List<Medicine> findAllByMedicineIdIn(List<Long> medicineIds);
}
