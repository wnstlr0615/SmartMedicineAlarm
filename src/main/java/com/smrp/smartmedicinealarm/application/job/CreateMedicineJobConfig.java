package com.smrp.smartmedicinealarm.application.job;

import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import com.smrp.smartmedicinealarm.entity.medicine.embedded.*;
import com.smrp.smartmedicinealarm.repository.medicine.MedicineRepository;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;

import java.time.LocalDate;

import static com.smrp.smartmedicinealarm.utils.DateTimeUtils.dateToLocalDateNullAble;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CreateMedicineJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MedicineRepository medicineRepository;

    @Bean
    public Job csvFileLoadJob() throws Exception {
        return jobBuilderFactory.get("csvFileLoadJob")
                .incrementer(new RunIdIncrementer())
                .start(csvFileLoadStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step csvFileLoadStep(@Value("#{jobParameters[filePath]}")String filePath) throws Exception {
        return stepBuilderFactory.get("csvFileLoadStep")
                .<Medicine, Medicine>chunk(1000)
                .reader(csvFileReader(filePath))
                .writer(jpaWriter())
                .build();
    }



    private ItemReader<? extends Medicine> csvFileReader(String filePath) throws Exception {
        DefaultLineMapper<Medicine> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
                "itemSeq", "itemName", "entpSeq", "entpName", "chart", "itemImage", "printFront", "printBack", "drugShape",
                "colorClass1", "colorClass2", "lineFront", "lineBack", "lengLong", "lengShort", "thick","imgRegistTs", "classNo",
                "className", "etcOtcName", "itemPermitDate", "formCodeName", "markCodeFrontAnal","markCodeBackAnal",
                "markCodeFrontImg", "markCodeBackImg", "changeDate"
        );
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(getMedicineMapper());
        FlatFileItemReader<Medicine> csfFileReader = new FlatFileItemReaderBuilder<Medicine>()
                .name("csfFileReader")
                .encoding("UTF-8")
                .resource(new FileUrlResource(filePath))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
        csfFileReader.afterPropertiesSet();
        return csfFileReader;
    }

    private FieldSetMapper<Medicine> getMedicineMapper() {
        return (fs) ->{
            Long itemSeq = fs.readLong("itemSeq");
            String itemName = fs.readString("itemName");
            String itemImage = fs.readString("itemImage");
            String etcOtcName = fs.readString("etcOtcName");


            MedicineIdentification medicineIdentification = getMedicineIdentification(fs);
            MedicineCompany medicineCompany = getMedicineCompany(fs);

            MedicineColor medicineColor = getMedicineColor(fs);
            MedicineLine medicineLine = getMedicineLine(fs);
            LengAndThick lengAndThick = getLengAndThick(fs);
            ClassNoAndName classNoAndName = getClassNoAndName(fs);
            MarkCode markCode = getMarkCode(fs);
            MedicineDate medicineDate = getMedicineDate(fs);

            return Medicine.createMedicine(
                    itemSeq, itemName, itemImage, etcOtcName, classNoAndName,  lengAndThick,
                    medicineCompany, medicineIdentification, medicineLine, medicineColor, markCode, medicineDate
            );
        };
    }
    private ItemWriter<? super Medicine> jpaWriter() {
        return (medicines) -> medicines.forEach(medicineRepository::save
        );
    }

    private MedicineIdentification getMedicineIdentification(FieldSet fs) {
        String printFront = fs.readString("printFront");
        String printBack = fs.readString("printBack");
        String drugShape = fs.readString("drugShape");
        String chart = fs.readString("chart");
        String formCodeName = fs.readString("formCodeName");
        return MedicineIdentification.createMedicineIdentification(printFront, printBack, drugShape, chart, formCodeName);
    }

    private MedicineLine getMedicineLine(FieldSet fs) {
        String lineFront = fs.readString("lineFront");
        String lineBack = fs.readString("lineBack");
        return MedicineLine.crateMedicineLine(lineFront, lineBack);
    }

    private MedicineCompany getMedicineCompany(FieldSet fs) {
        Long entpSeq = fs.readLong("entpSeq");
        String entpName = fs.readString("entpName");
        return MedicineCompany.createMedicineCompany(entpSeq, entpName);
    }

    private MarkCode getMarkCode(FieldSet fs) {
        String markCodeFrontAnal = fs.readString("markCodeFrontAnal");
        String markCodeBackAnal = fs.readString("markCodeBackAnal");
        String markCodeFrontImg = fs.readString("markCodeFrontImg");
        String markCodeBackImg = fs.readString("markCodeBackImg");
        return MarkCode.createMarkCode(markCodeFrontAnal, markCodeBackAnal, markCodeFrontImg, markCodeBackImg);
    }

    private MedicineColor getMedicineColor(FieldSet fs) {
        String colorClass1 = fs.readString("colorClass1");
        String colorClass2 = fs.readString("colorClass2");
        return MedicineColor.createMedicineColor(colorClass1, colorClass2);
    }

    private LengAndThick getLengAndThick(FieldSet fs) {
        String lengShort = fs.readString("lengShort");
        String lengLong = fs.readString("lengLong");
        String thick = fs.readString("thick");
        return LengAndThick.createLengAndThick(lengShort, lengLong, thick);
    }

    private MedicineDate getMedicineDate(FieldSet fs) {
        LocalDate imgRegistTs = dateToLocalDateNullAble(fs.readDate("imgRegistTs", "yyyyMMdd",null));
        LocalDate itemPermitDate = dateToLocalDateNullAble(fs.readDate("itemPermitDate", "yyyyMMdd",null));
        LocalDate changeDate = dateToLocalDateNullAble(fs.readDate("changeDate", "yyyyMMdd", null));

        return MedicineDate.createMedicineDate(itemPermitDate, imgRegistTs, changeDate);
    }

    private ClassNoAndName getClassNoAndName(FieldSet fs) {
        String classNo = fs.readString("classNo");
        String className = fs.readString("className");
        return ClassNoAndName.createClassNoAndName(classNo, className);
    }

    
}
