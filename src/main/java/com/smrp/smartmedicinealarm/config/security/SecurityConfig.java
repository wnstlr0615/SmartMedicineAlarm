package com.smrp.smartmedicinealarm.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smrp.smartmedicinealarm.config.security.filter.JWTCheckFilter;
import com.smrp.smartmedicinealarm.config.security.filter.JWTLoginFilter;
import com.smrp.smartmedicinealarm.config.security.handler.CustomAuthenticationEntryPoint;
import com.smrp.smartmedicinealarm.config.security.handler.JWTLoginAuthenticationFailHandler;
import com.smrp.smartmedicinealarm.service.refreshtoken.RefreshTokenService;
import com.smrp.smartmedicinealarm.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailService customUserDetailService;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper mapper;
    private final JWTUtils jwtUtils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web){
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/accounts").permitAll()
                .antMatchers("/api/v1/accounts/**").hasRole("NORMAL")
                .antMatchers(HttpMethod.POST, "/api/v1/login").anonymous()
                .antMatchers(HttpMethod.POST, "/api/v1/logout").authenticated()
                .anyRequest().authenticated()
        ;
        http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        http
                .addFilterAt(new JWTLoginFilter(authenticationManagerBean(), mapper, jwtUtils, authenticationFailureHandler(), refreshTokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new JWTCheckFilter(customUserDetailService, refreshTokenService, authenticationEntryPoint(), jwtUtils, mapper), BasicAuthenticationFilter.class)
        ;
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint(mapper);
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler(){
        return new JWTLoginAuthenticationFailHandler(mapper);
    }
}
