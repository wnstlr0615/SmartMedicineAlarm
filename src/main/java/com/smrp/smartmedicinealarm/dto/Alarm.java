package com.smrp.smartmedicinealarm.dto;

import com.smrp.smartmedicinealarm.entity.BaseTimeEntity;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseTimeEntity {
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
    private List<MedicineAlarm> medicineAlarms;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    //== 생성 메서드 ==//
    public static Alarm createAlarm(Long alarmId, String title, Integer doseCount, Account account, List<MedicineAlarm> medicineAlarms, Boolean deleted, LocalDateTime deletedAt) {
        return Alarm.builder()
                .alarmId(alarmId)
                .title(title)
                .doseCount(doseCount)
                .account(account)
                .medicineAlarms(medicineAlarms)
                .deleted(deleted)
                .deletedAt(deletedAt)
                .build();
    }
    public static Alarm createAlarm(String title, Integer doseCount, Account account, List<Medicine> medicines) {
        Alarm alarm = Alarm.builder()
                .title(title)
                .doseCount(doseCount)
                .account(account)
                .build();

        List<MedicineAlarm> medicineAlarms = medicines.stream()
                .map(medicine -> MedicineAlarm.createMedicineAlarm(alarm, medicine)).toList();


        for (MedicineAlarm medicineAlarm : medicineAlarms) {
            alarm.addMedicineAlarm(medicineAlarm);
        }

        return alarm;
    }

    //== 비즈니스 메서드 ==//
    public void addMedicineAlarm(MedicineAlarm medicineAlarm) {
        if(medicineAlarms == null){
            medicineAlarms = new ArrayList<>();
        }
         medicineAlarms.add(medicineAlarm);
        if(medicineAlarm.getAlarm() != this){
            medicineAlarm.setAlarm(this);
        }

    }
}