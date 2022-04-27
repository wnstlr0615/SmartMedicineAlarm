package com.smrp.smartmedicinealarm.dto.account;

import com.smrp.smartmedicinealarm.entity.Account;
import com.smrp.smartmedicinealarm.entity.AccountStatus;
import com.smrp.smartmedicinealarm.entity.Gender;
import com.smrp.smartmedicinealarm.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewAccountDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ApiModel(value = "NewAccountDtoRequest")
    public static class Request{
        @Email(message = "email 형식이 맞지 않습니다.")
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @ApiModelProperty(value = "사용자 계정", example = "joon@naver.com")
        private String email;

        @Size(min = 10, max = 50, message = "패스워드는 10자 이상 50자 미만입니다." )
        @ApiModelProperty(value = "사용자 패스워드", example = "!!password")
        private String password;

        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @ApiModelProperty(value = "사용자 이름", example ="김땡떙")
        private String name;

        @NotNull(message = "성별 입력은 필수 입니다.")
        @ApiModelProperty(value = "사용자 성별", example = "MAN,WOMAN")
        private Gender gender;


        public static Request createNewAccountDtoRequest(String email, String password, String name, Gender gender){
            return Request.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .gender(gender)
                    .build();
        }

        public  Account toEntity(){
            return Account.builder()
                    .email(email)
                    .name(name)
                    .password(password)
                    .gender(gender)
                    .status(AccountStatus.USE)
                    .role(Role.NORMAL)
                    .accountId(null)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ApiModel(value = "NewAccountDtoResponse")
    public static class Response{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long accountId;

        @ApiModelProperty(value = "사용자 계정", example = "joon@naver.com")
        private String email;

        @ApiModelProperty(value = "사용자 이름", example ="김땡떙")
        private String name;

        @ApiModelProperty(value = "사용자 성별", example = "MAN,WOMAN")
        private Gender gender;

        public static Response createNewAccountDtoResponse(Long accountId, String email, String name, Gender gender){
            return Response.builder()
                    .accountId(accountId)
                    .email(email)
                    .name(name)
                    .gender(gender)
                    .build();
        }

        public static Response fromEntity(Account account){
            return Response.builder()
                    .accountId(account.getAccountId())
                    .email(account.getEmail())
                    .name(account.getName())
                    .gender(account.getGender())
                    .build();
        }
    }
}
