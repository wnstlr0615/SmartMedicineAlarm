package com.smrp.smartmedicinealarm.dto.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginFormDto {
    @Email(message = "올바른 email을 입력해주세요.")
    @ApiModelProperty(value = "사용자 id(email)", example = "jun@naver.com")
    private String email;

    @Size(min = 10, max = 50, message = "패스워드는 10자 이상 50자 이하입니다.")
    @NotBlank(message = "패스워드는 필수입니다.")
    @ApiModelProperty(value = "사용자 패스워드", example = "!abcdefghijk123")
    private String password;

    //== 생성 메서드 ==//
    public static LoginFormDto createLoginFormDto(String email, String password){
        return LoginFormDto.builder()
                .email(email)
                .password(password)
                .build();
    }
}
