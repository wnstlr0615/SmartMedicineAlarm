package com.smrp.smartmedicinealarm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetailService;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest
@ActiveProfiles("test")
public class BaseControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomUserDetailService customUserDetailService;

    @MockBean
    JWTUtils jwtUtils;

    @MockBean
    RefreshTokenService refreshTokenService;

    protected Account createAccount() {
        return Account.createAccount(1L, "joon@naver.com", "asdasdasd", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
    }
    protected UsernamePasswordAuthenticationToken getAuthentication(Account account) {
        return new UsernamePasswordAuthenticationToken(
                account,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_NORMAL")
                )
        );
    }


}
