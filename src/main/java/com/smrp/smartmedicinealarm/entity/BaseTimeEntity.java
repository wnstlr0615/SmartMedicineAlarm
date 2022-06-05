package com.smrp.smartmedicinealarm.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createDate;
    @LastModifiedDate
    protected LocalDateTime lastModified;

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    protected void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
