package com.smrp.smartmedicinealarm.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class CookieUtils {
    public static Cookie createCookie(String name, String value, int expiry){
        final Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiry);
        return cookie;
    }
    public static Cookie resetCookie(String name){
        return createCookie(name, "", 0);
    }

    @Nullable
    public static Cookie getCookie(HttpServletRequest request, String name){
        final Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie;
                }
            }
        }
        return null;
    }
}
