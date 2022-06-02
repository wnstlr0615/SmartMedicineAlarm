package com.smrp.smartmedicinealarm.dto;

import com.smrp.smartmedicinealarm.entity.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer doseCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "medicine_alarm_id", nullable = false)
    private List<MedicineAlarm> medicineAlarms;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

}
