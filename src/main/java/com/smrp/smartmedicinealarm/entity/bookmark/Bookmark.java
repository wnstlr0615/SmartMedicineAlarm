package com.smrp.smartmedicinealarm.entity.bookmark;

import com.smrp.smartmedicinealarm.entity.BaseEntity;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.medicine.Medicine;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {
    @Id @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long bookmarkId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    public static Bookmark createBookmark(Account account, Medicine medicine){
        return Bookmark.builder()
                .account(account)
                .medicine(medicine)
                .build();
    }

    public void setAccount(Account account) {
        if(account != null ){
            account.getBookmarks().remove(this);
        }
        this.account = account;
        if(account.getBookmarks() == null || !account.getBookmarks().contains(account)){
            account.addBookmark(this);
        }
    }
}