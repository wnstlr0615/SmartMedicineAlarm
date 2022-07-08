package com.smrp.smartmedicinealarm.application.job;

import com.smrp.smartmedicinealarm.dto.Alarm;
import com.smrp.smartmedicinealarm.repository.alarm.AlarmRepository;
import com.smrp.smartmedicinealarm.service.notification.SendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MedicineAlarmNotificationConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AlarmRepository alarmRepository;
    private final SendService sendService;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job  medicineAlarmJob(){
        return jobBuilderFactory.get("medicineAlarmJob")
                .incrementer(new RunIdIncrementer())
                .start(medicineAlarmStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step medicineAlarmStep(@Value("#{jobParameters[date]}") String date) {
        return stepBuilderFactory.get("medicineAlarmStep")
                .<Alarm, Alarm>chunk(1000)
                .reader(alarmRepositoryReader())
                .writer(sendNotificationWriter())
                .build();
    }


    private ItemWriter<? super Alarm> sendNotificationWriter() {
        return items -> {
            log.info("item size : {}", items.size());
            items.forEach(item -> {
                log.info("{} 사용자에게 복용 알림  메시지 전송 ",item.getAccount().getEmail());
                sendService.send(item.getAccount(), "약복용 알람", "약 복용 하실 시간 입니다.");
                item.takeDose();
            });
            JpaItemWriter<Alarm> jpaItemWriter = new JpaItemWriter<>();
            jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
            jpaItemWriter.setUsePersist(true);
            jpaItemWriter.afterPropertiesSet();
            jpaItemWriter.write(items);
        };
    }

    private ItemReader<? extends Alarm> alarmRepositoryReader() {
        return new RepositoryItemReaderBuilder<Alarm>()
                .name("alarmRepositoryReader")
                .repository(alarmRepository)
                .methodName("findAllByDeletedIsFalseAndLeftOverDoseCountGreaterThan")
                .pageSize(1000)
                .arguments(List.of(0))
                .sorts(Collections.singletonMap("alarmId", Sort.Direction.DESC))
                .build();
    }
}
