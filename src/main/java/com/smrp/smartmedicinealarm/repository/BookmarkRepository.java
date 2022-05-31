package com.smrp.smartmedicinealarm.repository;

import com.smrp.smartmedicinealarm.entity.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
