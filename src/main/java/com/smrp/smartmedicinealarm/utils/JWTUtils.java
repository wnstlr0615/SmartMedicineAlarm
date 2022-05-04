package com.smrp.smartmedicinealarm.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smrp.smartmedicinealarm.error.code.LoginErrorCode;
import com.smrp.smartmedicinealarm.error.exception.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JWTUtils {
    @Value("${jwt.accessExpiredTime}")
    private Duration accessTokenExpiredTime;

    @Value("${jwt.refreshExpiredTime}")
    private Duration refreshTokenExpiredTime;

    @Value("${jwt.key}")
    private  String key;



    @PostConstruct
    public void print(){
        System.out.println("accessTokenExpiredTime = " + accessTokenExpiredTime);
        System.out.println("refreshTokenExpiredTime = " + refreshTokenExpiredTime);
        System.out.println("key = " + key);
    }

    public String createAccessToken(String username){
        return createToken(username, accessTokenExpiredTime, Algorithm.HMAC512(key));
    }

    public String createRefreshToken(String username){
        return createToken(username, refreshTokenExpiredTime, Algorithm.HMAC512(key));
    }

    public String createToken(String username, Duration expiredTime, Algorithm key){
        Assert.hasText(username, "올 바르지 않은 username 입니다.");
        LocalDateTime expiredDate = LocalDateTime.now().plusSeconds(expiredTime.getSeconds());
        Timestamp expirationTime = Timestamp.valueOf(expiredDate);

        return JWT.create()
                .withExpiresAt(expirationTime)
                .withSubject(username)
                .sign(key);
    }

    public String verifyToken(String token){
        return verifyToken(token, key);
    }

    public String verifyToken(String token, String key){
        Assert.hasText(token, "token 이 올 바르지 않습니다. ");
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(key)).build().verify(token);
        } catch (JWTVerificationException | IllegalArgumentException  e) {
            throw new LoginException(LoginErrorCode.TOKEN_IS_EXPIRED_OR_WRONG);
        }
        return decodedJWT.getSubject();
    }

    public Duration getAccessTokenExpiredTime() {
        return accessTokenExpiredTime;
    }

    public Duration getRefreshTokenExpiredTime() {
        return refreshTokenExpiredTime;
    }
}
