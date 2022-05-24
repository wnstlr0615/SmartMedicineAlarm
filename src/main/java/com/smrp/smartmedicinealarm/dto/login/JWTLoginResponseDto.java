package com.smrp.smartmedicinealarm.dto.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JWTLoginResponseDto {
    @ApiModelProperty(value = "AccessToken", notes = "토큰 만료 시간 30 분, 쿠키에도 저장됨")
    private String accessToken;

    @ApiModelProperty(value = "사용자 email", notes = "사용자 로그인 아이디", example = "joon@naver.com")
    private String username;

    //== 생성 메서드 ==//
    public static JWTLoginResponseDto createJwtLoginResponseDto(String accessToken, String username){
        return JWTLoginResponseDto.builder()
                .accessToken(accessToken)
                .username(username)
                .build();
    }
}
