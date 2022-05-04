package com.smrp.smartmedicinealarm.dto.account;

import com.smrp.smartmedicinealarm.entity.account.Gender;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountModifyDto {
    @NotBlank(message = "이름 입력은 필수 입니다.")
    @ApiModelProperty(value = "사용자 이름", example = "최모씨")
    private String name;

    @NotNull(message = "성별 입력은 필수 입니다.")
    @ApiModelProperty(value = "사용자 성별", example = "MAN")
    private Gender gender;

    //== 생성 메서드 ==//
    public static AccountModifyDto createAccountModifyDto(String name, Gender gender){
        return AccountModifyDto.builder()
                .name(name)
                .gender(gender)
                .build();
    }

}
