package com.smrp.smartmedicinealarm.service.refreshtoken;

import com.smrp.smartmedicinealarm.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final String REFRESH_TOKEN_SAVE = "REFRESH:";
    @Value("${jwt.refreshExpiredTime}")
    private Duration refreshTokenExpiredTime;
    private final RedisUtils redisUtils;

    @Override
    public String getToken(String username) {
        return redisUtils.getData(REFRESH_TOKEN_SAVE + username);
    }

    @Override
    public void deleteToken(String username) {
        redisUtils.deleteData(REFRESH_TOKEN_SAVE + username);
    }

    @Override
    public void saveToken(String username, String newRefreshToken) {
        redisUtils.addData(REFRESH_TOKEN_SAVE + username, newRefreshToken, refreshTokenExpiredTime);
    }
}
