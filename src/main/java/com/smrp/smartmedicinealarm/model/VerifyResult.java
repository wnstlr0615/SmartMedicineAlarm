package com.smrp.smartmedicinealarm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class VerifyResult {
    private boolean result;
    private String username;

    //== 생성 메서드 ==//
    public static VerifyResult fail(String username){
        return VerifyResult.builder()
                .result(false)
                .username(username)
                .build();
    }

    public static VerifyResult success(String username){
        return VerifyResult.builder()
                .result(true)
                .username(username)
                .build();
    }
}
