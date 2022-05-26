package com.smrp.smartmedicinealarm.repository.medicine;

import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> ,MedicineCustomRepository{
}
