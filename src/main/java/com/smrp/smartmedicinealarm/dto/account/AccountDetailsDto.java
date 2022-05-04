package com.smrp.smartmedicinealarm.dto.account;

import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDetailsDto {
    @ApiModelProperty(value = "사용자 PK", example = "1")
    private Long accountId;

    @ApiModelProperty(value = "사용자 아이디(이메일)", example = "joon@naver.com")
    private String email;

    @ApiModelProperty(value = "사용자 이름", example = "최모씨")
    private String name;

    @ApiModelProperty(value = "사용자 성별", example = "MAN")
    private Gender gender;

    @ApiModelProperty(value = "사용자 상태", example = "USE")
    private AccountStatus status;

    @ApiModelProperty(value = "사용자 권한", example = "USER")
    private Role role;

    //== 생성 메서드 ==//
    public static AccountDetailsDto createAccountDetailsDto(Long accountId, String email, String name, Gender gender, AccountStatus status, Role role){
        return AccountDetailsDto.builder()
                .accountId(accountId)
                .email(email)
                .name(name)
                .gender(gender)
                .status(status)
                .role(role)
                .build();
    }

    public static AccountDetailsDto fromEntity(Account account){
        return AccountDetailsDto.builder()
                .accountId(account.getAccountId())
                .email(account.getEmail())
                .name(account.getName())
                .gender(account.getGender())
                .status(account.getStatus())
                .role(account.getRole())
                .build();
    }
}
