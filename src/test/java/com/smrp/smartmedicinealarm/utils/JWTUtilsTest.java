package com.smrp.smartmedicinealarm.utils;

import com.auth0.jwt.algorithms.Algorithm;
import com.smrp.smartmedicinealarm.error.code.LoginErrorCode;
import com.smrp.smartmedicinealarm.error.exception.LoginException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JWTUtilsTest {
    final String key = "key";
    JWTUtils jwtUtils = new JWTUtils();
    @Test
    @DisplayName("[성공] JWT 토큰 생성")
    public void successCreateToken (){
        //given
        Duration accessExpiredTime = Duration.ofHours(1);
        String username = "joon@naver.com";

        //when
        String token = jwtUtils.createToken(username, accessExpiredTime, Algorithm.HMAC512(key));

        // then
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("[실패] JWT 토큰 생성 - username 이 공백이거나 null인 경우")
    public void failCreateToken (){
        //given

        Duration accessExpiredTime = Duration.ofHours(1);
        String username = "";

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jwtUtils.createToken(username, accessExpiredTime, Algorithm.HMAC512(key))
        );

        // then
        assertThat(exception.getMessage()).isEqualTo("올 바르지 않은 username 입니다.");
    }

    @Test
    @DisplayName("[성공] JWT 토큰 생성 및 검증 ")
    public void successTokenVerification (){
        //given
        Duration accessExpiredTime = Duration.ofHours(1);
        String username = "joon@naver.com";
        String token = jwtUtils.createToken(username, accessExpiredTime, Algorithm.HMAC512(key));

        //when //then
        assertThat(jwtUtils.verifyToken(token, key)).isEqualTo(username);

    }

    @Test
    @DisplayName("[실패] JWT 토큰 생성 및 검증 - 토큰이 만료한 경우")
    public void failTokenVerification (){
        //given
        LoginErrorCode errorCode = LoginErrorCode.TOKEN_IS_EXPIRED_OR_WRONG;
        Duration accessExpiredTime = Duration.ofSeconds(-1);
        String username = "joon@naver.com";
        String token = jwtUtils.createToken(username, accessExpiredTime, Algorithm.HMAC512(key));

        //when
        LoginException loginException = assertThrows(LoginException.class,
                () -> jwtUtils.verifyToken(token, key)
        );

        //then
        assertThat(loginException.getErrorCode()).isEqualTo(errorCode);
    }
}