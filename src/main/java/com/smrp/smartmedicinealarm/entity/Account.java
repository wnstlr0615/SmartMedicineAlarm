package com.smrp.smartmedicinealarm.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    //== 생성 메서드 ==//
    public static Account createAccount(Long accountId, String email, String password, String name, Gender gender, AccountStatus status, Role role){
        return Account.builder()
                .accountId(accountId)
                .email(email)
                .password(password)
                .name(name)
                .gender(gender)
                .status(status)
                .role(role)
                .build();
    }

    //== 비즈니스 메서드 ==//

    public void setBcryptPassword(String bcryptPassword){
        this.password = bcryptPassword;
    }

    public void remove() {
        status = AccountStatus.DELETED;
    }
}
