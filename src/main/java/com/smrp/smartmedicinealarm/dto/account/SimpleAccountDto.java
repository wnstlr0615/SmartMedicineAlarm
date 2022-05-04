package com.smrp.smartmedicinealarm.dto.account;

import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Builder
@AllArgsConstructor
@Relation(collectionRelation = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleAccountDto {
    @ApiModelProperty(value = "사용자 PK", example = "1")
    private Long accountId;

    @ApiModelProperty(value = "사용자 아이디(이메일)", example = "joon@naver.com")
    private String email;

    @ApiModelProperty(value = "사용자 이름", example = "최모씨")
    private String name;

    @ApiModelProperty(value = "사용자 성별", example = "MAN")
    private Gender gender;

    //== 생성 메서드 ==//
    public static SimpleAccountDto createSimpleAccountDto(Long accountId, String email, String name, Gender gender){
        return SimpleAccountDto.builder()
                .accountId(accountId)
                .email(email)
                .name(name)
                .gender(gender)
                .build();
    }

    public static SimpleAccountDto fromEntity(Account account){
        return SimpleAccountDto.builder()
                .accountId(account.getAccountId())
                .email(account.getEmail())
                .name(account.getName())
                .gender(account.getGender())
                .build();
    }
}
