package com.smrp.smartmedicinealarm.service.bookmark;

import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.entity.account.Account;

public interface BookmarkService {
    /** 약 즐겨찾기 추가*/
    NewBookmarkDto.Response addBookmark(Account account, NewBookmarkDto.Request bookmarkDto);
    /** 약 즐겨찾기 목록 조회*/
    SimpleBookmarkDto findAllBookmark(Account account);
}
