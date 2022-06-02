package com.smrp.smartmedicinealarm.dto;

import com.smrp.smartmedicinealarm.entity.BaseTimeEntity;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineAlarm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicineAlarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", nullable = false, updatable = false)
    private Alarm alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id" , nullable = false, updatable = false)
    private Medicine medicine;

    //== 생성 메서드 ==//
    public static MedicineAlarm createMedicineAlarm(Long medicationId, Alarm alarm,  Medicine medicine) {
        return MedicineAlarm.builder()
                .medicineAlarmId(medicationId)
                .alarm(alarm)
                .medicine(medicine)
                .build();
    }

    public static MedicineAlarm createMedicineAlarm(Alarm alarm,  Medicine medicine) {
        return MedicineAlarm.createMedicineAlarm(null, alarm, medicine);
    }

    public void setAlarm(Alarm alarm) {
        if(alarm.getMedicineAlarms() != null){
            alarm.getMedicineAlarms().remove(this);
        }
        this.alarm = alarm;
        if(alarm.getMedicineAlarms() == null || !alarm.getMedicineAlarms().contains(this)){
            alarm.addMedicineAlarm(this);
        }
    }
}
