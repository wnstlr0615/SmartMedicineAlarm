package com.smrp.smartmedicinealarm.application.scheduler;

import com.smrp.smartmedicinealarm.application.job.MedicineAlarmNotificationConfig;
import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;
import com.smrp.smartmedicinealarm.error.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicineAlarmNotificationScheduler {
    private final JobLauncher jobLauncher;
    private final MedicineAlarmNotificationConfig notificationConfig;
    @Scheduled(cron = "0 0 9 * * *")
    public void morningAlarm(){
        log.info("모닝 알림 전송");
        Map<String, JobParameter> parameter = Collections.singletonMap("date", new JobParameter(LocalDateTime.now().toString()));
        JobParameters jobParameters = new JobParameters(parameter);
        try {
            jobLauncher.run(notificationConfig.medicineAlarmJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("JobLauncher Error ", e);
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Scheduled(cron = "0 0 13 * * *")
    public void launchAlarm(){
        log.info("점심 알림 전송");
        Map<String, JobParameter> parameter = Collections.singletonMap("date", new JobParameter(LocalDateTime.now().toString()));
        JobParameters jobParameters = new JobParameters(parameter);
        try {
            jobLauncher.run(notificationConfig.medicineAlarmJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("JobLauncher Error ", e);
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Scheduled(cron = "0 0 19 * * *")
    public void dinnerAlarm(){
        log.info("저녁 알림 전송");
        Map<String, JobParameter> parameter = Collections.singletonMap("date", new JobParameter(LocalDateTime.now().toString()));
        JobParameters jobParameters = new JobParameters(parameter);
        try {
            jobLauncher.run(notificationConfig.medicineAlarmJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("JobLauncher Error ", e);
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
