package com.smrp.smartmedicinealarm.entity.medicine.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@ToString
@Embeddable
public class LengAndThick {
    @Column(length = 50)
    private String lengLong; //크기(장축)
    @Column(length = 50)
    private String lengShort; //크기(단축)
    @Column(length = 50)
    private String thick;   //크기(두께)

    //== 생성 메서드 ==//
    public static LengAndThick createLengAndThick(String lengShort, String lengLong, String thick) {
        return  LengAndThick.builder()
                .lengLong(lengLong)
                .lengShort(lengShort)
                .thick(thick)
                .build();
    }

    //== 비즈니스 메서드 ==//
    public void update(String lengLong, String lengShort, String thick) {
        this.lengLong = lengLong != null ? lengLong : this.lengLong;
        this.lengShort = lengShort != null ? lengShort : this.lengShort;
        this.thick = thick != null ? thick : this.thick;
    }


}