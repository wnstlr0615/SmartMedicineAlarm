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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bookmark bookmark = (Bookmark) o;

        if (!account.getAccountId().equals(bookmark.account.getAccountId())) return false;
        return medicine.getMedicineId().equals(bookmark.medicine.getMedicineId());
    }

    @Override
    public int hashCode() {
        int result = account.hashCode();
        result = 31 * result + medicine.hashCode();
        return result;
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