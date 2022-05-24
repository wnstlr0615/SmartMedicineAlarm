package com.smrp.smartmedicinealarm.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Date;

@UtilityClass
public class DateTimeUtils {
    public static LocalDate dateToLocalDate(Date date){
        //Assert.notNull(date, "date must not null");
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return sqlDate.toLocalDate();
    }
    public static LocalDate dateToLocalDateNullAble(Date date) {
        if (date == null) return null;
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return sqlDate.toLocalDate();
    }
}
