package com.smrp.smartmedicinealarm.repository.alarm;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.entity.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    @EntityGraph(attributePaths = {"account", "medicineAlarms"})
    Optional<Alarm> findDetailsByAlarmId(Long alarmId);

    @EntityGraph(attributePaths = {"account", "medicineAlarms"})
    List<Alarm> findByAccountAndDeletedFalse(Account account);

    @EntityGraph(attributePaths = {"account"})
    Page<Alarm> findAllByDeletedIsFalseAndLeftOverDoseCountGreaterThan(Integer doseCount, Pageable pageable);
}
