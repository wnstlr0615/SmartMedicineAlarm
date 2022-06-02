package com.smrp.smartmedicinealarm.repository.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
