package com.smrp.smartmedicinealarm.service.refreshtoken;


public interface RefreshTokenService {
    String getToken(String username);

    void deleteToken(String username);

    void saveToken(String username, String newRefreshToken);
}
