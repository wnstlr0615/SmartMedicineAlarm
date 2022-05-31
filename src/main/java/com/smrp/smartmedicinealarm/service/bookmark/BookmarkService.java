package com.smrp.smartmedicinealarm.service.bookmark;

import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.entity.account.Account;

public interface BookmarkService {
    NewBookmarkDto.Response addBookmark(Account account, NewBookmarkDto.Request bookmarkDto);
}
