package com.smrp.smartmedicinealarm.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetail;
import com.smrp.smartmedicinealarm.dto.login.JWTLoginResponseDto;
import com.smrp.smartmedicinealarm.dto.login.LoginFormDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.error.code.LoginErrorCode;
import com.smrp.smartmedicinealarm.error.exception.LoginException;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.CookieUtils;
import com.smrp.smartmedicinealarm.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.smrp.smartmedicinealarm.constant.SecurityConstant.REFRESH_TOKEN;
import static com.smrp.smartmedicinealarm.error.code.LoginErrorCode.WRONG_LOGIN_FORM;

@Slf4j
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper mapper;
    private final JWTUtils jwtUtils;
    private final AuthenticationFailureHandler failureHandler;
    private final RefreshTokenService refreshTokenService;

    public JWTLoginFilter(AuthenticationManager authenticationManager, ObjectMapper mapper, JWTUtils jwtUtils, AuthenticationFailureHandler failureHandler, RefreshTokenService refreshTokenService) {
        super("/api/v1/login", authenticationManager);
        this.mapper = mapper;
        this.jwtUtils = jwtUtils;
        this.failureHandler = failureHandler;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginFormDto loginFormDto;

        try {
            loginFormDto = mapper.readValue(request.getInputStream(), LoginFormDto.class);
        } catch (IOException e) {
            throw new LoginException(WRONG_LOGIN_FORM);
        }

        if(!StringUtils.hasText(loginFormDto.getEmail()) || !StringUtils.hasText(loginFormDto.getPassword())){
            throw new LoginException(LoginErrorCode.EMAIL_OR_PASSWORD_WRONG);
        }

        return this.getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(loginFormDto.getEmail(), loginFormDto.getPassword())
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException{
        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
        }
        Account account = ((CustomUserDetail) authResult.getPrincipal()).getAccount();
        String username = account.getEmail();

        // 쿠키 생성
        String accessToken = jwtUtils.createAccessToken(username);
        String refreshToken = jwtUtils.createRefreshToken(username);

        refreshTokenService.saveToken(username, refreshToken);

        //쿠키 설정
        setRefreshTokenCookie(response, refreshToken);

        //응답 설정
        setResponseContent(response, accessToken, username);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshCookie = CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime());
        response.addCookie(refreshCookie);
    }

    private void setResponseContent(HttpServletResponse response, String accessToken, String username) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                mapper.writeValueAsString(
                        JWTLoginResponseDto.createJwtLoginResponseDto(accessToken, username)
                )
        );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
