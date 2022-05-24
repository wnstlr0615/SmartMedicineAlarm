package com.smrp.smartmedicinealarm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetailService;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
}
