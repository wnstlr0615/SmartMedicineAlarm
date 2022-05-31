package com.smrp.smartmedicinealarm.repository;

import com.smrp.smartmedicinealarm.entity.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    Optional<Account> findByEmail(String username);
    @EntityGraph(attributePaths = {"bookmarks"})
    Optional<Account> findDetailByAccountId(Long id);
}
