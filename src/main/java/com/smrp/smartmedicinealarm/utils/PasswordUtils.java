package com.smrp.smartmedicinealarm.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtils {
    private final PasswordEncoder bcryptEncoder;

    public String encode(String rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encoderPassword){
        return bcryptEncoder.matches(rawPassword, encoderPassword);
    }
}
