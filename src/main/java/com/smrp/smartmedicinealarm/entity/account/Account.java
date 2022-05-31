package com.smrp.smartmedicinealarm.entity.account;

import com.smrp.smartmedicinealarm.entity.BaseTimeEntity;
import com.smrp.smartmedicinealarm.entity.bookmark.Bookmark;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Bookmark> bookmarks = new LinkedHashSet<>();


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

    public void addBookmark(Bookmark bookmark) {
        if(bookmarks == null){
            bookmarks = new LinkedHashSet<>();
        }
        bookmarks.add(bookmark);
        if(bookmark.getAccount() != this){
            bookmark.setAccount(this);
        }
    }


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
                .bookmarks(new LinkedHashSet<>())
                .build();
    }

    //== 비즈니스 메서드 ==//

    public void setBcryptPassword(String bcryptPassword){
        this.password = bcryptPassword;
    }

    //== 사용자 계정 삭제 ==//
    public void remove() {
        status = AccountStatus.DELETED;
    }

    //== 사용자 정보 업데이트 ==//
    public void updateInfo(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }


}
