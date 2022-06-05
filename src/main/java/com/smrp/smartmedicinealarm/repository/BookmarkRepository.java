package com.smrp.smartmedicinealarm.repository;

import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.bookmark.Bookmark;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @EntityGraph(attributePaths = {"account", "medicine"})
    List<Bookmark> findAllByAccount(Account account);
}
