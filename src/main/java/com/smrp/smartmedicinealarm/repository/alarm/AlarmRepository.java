package com.smrp.smartmedicinealarm.repository.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    @EntityGraph(attributePaths = {"account", "medicineAlarms"})
    Optional<Alarm> findDetailsByAlarmId(Long alarmId);
}
