package com.smrp.smartmedicinealarm.config.security;

import com.smrp.smartmedicinealarm.entity.Account;
import com.smrp.smartmedicinealarm.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

@Getter
public class CustomUserDetail extends User {
    private final Account account;


    public CustomUserDetail(Account account) {
        super(account.getEmail(), account.getPassword(), getAuthority(account.getRole()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> getAuthority(Role role) {
        return Set.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
