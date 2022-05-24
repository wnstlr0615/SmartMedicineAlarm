package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.login.JWTLoginResponseDto;
import com.smrp.smartmedicinealarm.dto.login.LoginFormDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.CookieUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.smrp.smartmedicinealarm.constant.SecurityConstant.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class LoginController {
    private final RefreshTokenService refreshTokenService;

    @ApiOperation(value = "사용자 로그인 API", notes = "스웨거 노출용 API로 처리는 필터 단에서 처리 되며 실제 출력 되지 않음")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JWTLoginResponseDto login(@RequestBody LoginFormDto loginForm) {
        return JWTLoginResponseDto.createJwtLoginResponseDto("", "");
    }


    @ApiOperation(value = "사용자 로그아웃 API")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public void logout(HttpServletResponse response){
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //쿠키 제거
        Cookie refreshCookie = CookieUtils.resetCookie(REFRESH_TOKEN);
        response.addCookie(refreshCookie);
        refreshTokenService.deleteToken(account.getEmail());
        SecurityContextHolder.clearContext();
    }
}
