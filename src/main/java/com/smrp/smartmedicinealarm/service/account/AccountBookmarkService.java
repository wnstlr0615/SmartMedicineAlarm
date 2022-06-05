package com.smrp.smartmedicinealarm.service.account;

import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.entity.account.Account;

public interface AccountBookmarkService {
    /** 사용자 즐겨찾기 약 목록 조회*/
    SimpleBookmarkDto findAllBookmark(Account account);
}
