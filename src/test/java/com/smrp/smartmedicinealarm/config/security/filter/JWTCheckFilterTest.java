package com.smrp.smartmedicinealarm.config.security.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetail;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetailService;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.entity.account.AccountStatus;
import com.smrp.smartmedicinealarm.entity.account.Gender;
import com.smrp.smartmedicinealarm.entity.account.Role;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.CookieUtils;
import com.smrp.smartmedicinealarm.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static com.smrp.smartmedicinealarm.constant.SecurityConstant.BEARER;
import static com.smrp.smartmedicinealarm.constant.SecurityConstant.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class JWTCheckFilterTest {
    @SpyBean
    JWTUtils jwtUtils;
    @SpyBean
    JWTCheckFilter jwtCheckFilter;
    @MockBean
    CustomUserDetailService userDetailsService;
    @MockBean
    RefreshTokenService refreshTokenService;
    @MockBean
    AuthenticationEntryPoint authenticationEntryPoint;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockFilterChain mockFilterChain;

    @BeforeEach
    void initMock(){
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockFilterChain = new MockFilterChain();
    }

    @Test
    @DisplayName("[로그인 성공] AccessToken 과 RefreshToken이 유효한 경우")
    public void givenAccessAndRefreshToken_whenCheckFilter_thenLoginSuccess() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createAccessToken(username);
        String refreshToken = jwtUtils.createRefreshToken(username);
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);
        request.setCookies(
                CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );
        when(refreshTokenService.getToken(anyString()))
                .thenReturn(
                        refreshToken
                );

        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(((Account)authentication.getPrincipal()).getEmail()).isEqualTo(username);
        assertThat(authentication.getCredentials()).isNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService).getToken(eq(username));
        verify(refreshTokenService, never()).saveToken(eq(username), anyString());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("[로그인 성공] AccessToken이 유효하지만 Refresh 쿠키가 없는 경우 재발급 후 로그인- REFRESH_TOKEN_NOT_FOUND")
    public void givenOnlyAccessToken_whenCheckFilter_thenLoginSuccess() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createAccessToken(username);
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );


        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(((Account)authentication.getPrincipal()).getEmail()).isEqualTo(username);
        assertThat(authentication.getCredentials()).isNull();
        assertThat(response.getCookie(REFRESH_TOKEN)).isNotNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService, never()).getToken(eq(username));
        verify(refreshTokenService).saveToken(eq(username), anyString());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("[로그인 실패] AccessToken이 유효하지만 Redis에 해당 토큰이 없는 경우")
    public void givenAccessToken_whenCheckFilter_thenLoginFail() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createAccessToken(username);
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);

        String refreshToken = jwtUtils.createRefreshToken(username);
        request.setCookies(
                CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );


        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );
        when(refreshTokenService.getToken(anyString()))
                .thenReturn(null);
        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(authentication).isNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService).getToken(eq(username));
        verify(refreshTokenService, never()).saveToken(eq(username), anyString());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("[로그인 성공] AccessToken이 유효하지만 Refresh 토큰이 만료한 경우 재발급 후 로그인")
    public void givenAccessTokenAndExpiredRefreshToken_whenCheckFilter_thenLoginFail() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createAccessToken(username);
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);

        String refreshToken = jwtUtils.createToken(username, Duration.ofSeconds(-1), Algorithm.HMAC512(jwtUtils.getKey()));
        request.setCookies(
                CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );


        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );

        when(refreshTokenService.getToken(anyString()))
                .thenReturn(refreshToken);

        doNothing().when(refreshTokenService)
                .saveToken(anyString(), anyString());

        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(((Account)authentication.getPrincipal()).getEmail()).isEqualTo(username);
        assertThat(authentication.getCredentials()).isNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService).getToken(eq(username));
        verify(refreshTokenService).saveToken(anyString(), anyString());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("[로그인 실패] AccessToken이 만료되었지만 RefreshToken 이 살아있는 경우 accessToken 반환 응답")
    public void givenExpiredAccessTokenAndRefreshToken_whenCheckFilter_thenReturnAccessToken() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createToken(username, Duration.ofSeconds(-1), Algorithm.HMAC512(jwtUtils.getKey()));
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);

        String refreshToken = jwtUtils.createRefreshToken(username);
        request.setCookies(
                CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );


        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );

        when(refreshTokenService.getToken(anyString()))
                .thenReturn(refreshToken);

        doNothing().when(refreshTokenService)
                .saveToken(anyString(), anyString());

        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(authentication).isNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService).getToken(eq(username));
        verify(refreshTokenService).saveToken(anyString(), anyString());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("[로그인 실패] AccessToken 과 RefreshToken이 다 만료된 경우 로그아웃")
    public void givenExpiredAccessTokenAndExpiredRefreshToken_whenCheckFilter_thenLoginFail() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createToken(username, Duration.ofSeconds(-1), Algorithm.HMAC512(jwtUtils.getKey()));
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);

        String refreshToken = jwtUtils.createToken(username, Duration.ofSeconds(-1), Algorithm.HMAC512(jwtUtils.getKey()));
        request.setCookies(
                CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );


        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );

        when(refreshTokenService.getToken(anyString()))
                .thenReturn(refreshToken);

        doNothing().when(refreshTokenService)
                .saveToken(anyString(), anyString());

        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(authentication).isNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService).getToken(eq(username));
        verify(refreshTokenService, never()).saveToken(anyString(), anyString());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("[로그인 실패] JWTUtils 토큰이 만료된 경우 authenticationEntryPoint 실행")
    public void givenWrongToken_whenCheckFilter_thenLoginFail() throws Exception{
        //given
        String username = "joon@naver.com";
        Account account = Account.createAccount(1L, username, "{noop}1234", "joon", Gender.MAN, AccountStatus.USE, Role.NORMAL);
        String accessToken = jwtUtils.createToken(username, Duration.ofSeconds(-1), Algorithm.HMAC512(jwtUtils.getKey()));
        request.addHeader(HttpHeaders.AUTHORIZATION,  BEARER + " " + accessToken);

        String refreshToken = jwtUtils.createToken(username, Duration.ofSeconds(-1), Algorithm.HMAC512("1234"));
        request.setCookies(
                CookieUtils.createCookie(REFRESH_TOKEN, refreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );


        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(
                        new CustomUserDetail(account)
                );

        when(refreshTokenService.getToken(anyString()))
                .thenReturn(refreshToken);

        //when
        jwtCheckFilter.doFilter(request, response, mockFilterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //then
        assertThat(authentication).isNull();

        verify(userDetailsService).loadUserByUsername(eq(username));
        verify(refreshTokenService, never()).getToken(eq(username));
        verify(refreshTokenService, never()).saveToken(anyString(), anyString());
        verify(authenticationEntryPoint).commence(any(), any(), any());
    }
}