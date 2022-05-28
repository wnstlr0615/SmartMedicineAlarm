package com.smrp.smartmedicinealarm.annotation;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "joon@naver.com", roles = {"ADMIN"})
public @interface MockAdminUser {
}
