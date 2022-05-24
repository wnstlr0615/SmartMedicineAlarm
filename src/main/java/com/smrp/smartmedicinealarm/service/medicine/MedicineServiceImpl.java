package com.smrp.smartmedicinealarm.service.medicine;

import com.smrp.smartmedicinealarm.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService{
    private final MedicineRepository medicineRepository;
}
