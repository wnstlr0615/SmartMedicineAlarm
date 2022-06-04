package com.smrp.smartmedicinealarm.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetail;
import com.smrp.smartmedicinealarm.config.security.CustomUserDetailService;
import com.smrp.smartmedicinealarm.dto.login.JWTLoginResponseDto;
import com.smrp.smartmedicinealarm.error.exception.LoginException;
import com.smrp.smartmedicinealarm.model.VerifyResult;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.CookieUtils;
import com.smrp.smartmedicinealarm.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.smrp.smartmedicinealarm.constant.SecurityConstant.*;
import static com.smrp.smartmedicinealarm.error.code.LoginErrorCode.REFRESH_TOKEN_IS_EXPIRED;
import static com.smrp.smartmedicinealarm.error.code.LoginErrorCode.REFRESH_TOKEN_NOT_FOUND;

@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {
    private final CustomUserDetailService userDetailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JWTUtils jwtUtils;
    private final ObjectMapper mapper;


    public JWTCheckFilter(CustomUserDetailService userDetailService, RefreshTokenService refreshTokenService, AuthenticationEntryPoint authenticationEntryPoint, JWTUtils jwtUtils, ObjectMapper mapper) {
        this.userDetailService = userDetailService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtUtils = jwtUtils;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!StringUtils.hasText(header) || !header.startsWith(BEARER)){
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(BEARER.length()).trim();

        try {
            VerifyResult accessVerifyResult = jwtUtils.verifyToken(token);
            String username = accessVerifyResult.getUsername();
            CustomUserDetail userDetails = (CustomUserDetail) userDetailService.loadUserByUsername(username);
            if(accessVerifyResult.isResult()){ // Access 토큰이 유효한 경우
                try {
                    String refreshToken = getCookieValue(request, REFRESH_TOKEN);
                    VerifyResult refreshVerifyResult = jwtUtils.verifyToken(refreshToken);
                    //1.  refresh 토큰이 redis에 없거나 일치하지 않는 경우 - 로그아웃 처리
                    if(isNotValidateRefreshToken(username, refreshToken)){
                        clearToken(response, username);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    //2. 만료된 경우 재 발급
                    if(!refreshVerifyResult.isResult()){
                        throw new LoginException(REFRESH_TOKEN_IS_EXPIRED);
                    }
                    loginSuccess(userDetails);
                } catch (LoginException e) {
                    refreshTokenReissue(response, username);
                    loginSuccess(userDetails);
                }
            }else{ // AccessToken이 만료한 경우
                try {
                    String refreshToken = getCookieValue(request, REFRESH_TOKEN);
                    VerifyResult refreshVerifyResult = jwtUtils.verifyToken(refreshToken);
                    // Refresh 토큰이 없거나 유효하지 않은 경우
                    if(isNotValidateRefreshToken(username, refreshToken) || !refreshVerifyResult.isResult()){
                        clearToken(response, username);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    refreshTokenReissue(response, username);
                    accessTokenReissueResponse(response, username);
                    return;
                } catch (LoginException e) {
                    clearToken(response, username);
                    throw e;
                }
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void accessTokenReissueResponse(HttpServletResponse response, String username) throws IOException {
        String accessToken = jwtUtils.createAccessToken(username);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                mapper.writeValueAsString(
                        JWTLoginResponseDto.createJwtLoginResponseDto(accessToken, username)
                )
        );
    }

    private void loginSuccess(CustomUserDetail userDetails) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails.getAccount(), null, userDetails.getAuthorities())
        );
    }

    private void refreshTokenReissue(HttpServletResponse response, String username) {
        String newRefreshToken = jwtUtils.createRefreshToken(username);
        response.addCookie(
                CookieUtils.createCookie(REFRESH_TOKEN, newRefreshToken, (int) jwtUtils.getRefreshTokenExpiredTime())
        );
        refreshTokenService.saveToken(username, newRefreshToken);
    }

    private void clearToken(HttpServletResponse response, String username) {
        refreshTokenService.deleteToken(username);
        response.addCookie(
                CookieUtils.resetCookie(ACCESS_TOKEN)
        );
        response.addCookie(
                CookieUtils.resetCookie(REFRESH_TOKEN)
        );
    }

    private boolean isNotValidateRefreshToken(String username, String refreshToken) {
        String findRefreshToken = refreshTokenService.getToken(username);
        log.info("username : {}, refreshToken : {}, findRefreshToken{}", username, refreshToken, findRefreshToken);
        return findRefreshToken == null || !findRefreshToken.equals(refreshToken);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = CookieUtils.getCookie(request, name);
        if(cookie == null){
            throw new LoginException(REFRESH_TOKEN_NOT_FOUND);
        }
        return cookie.getValue();
    }
}
