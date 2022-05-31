package com.smrp.smartmedicinealarm.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smrp.smartmedicinealarm.entity.account.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em){
        return new JPAQueryFactory(em);
    }

    @Bean
    AuditorAware<String> auditorProvider(){
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();

            if(principal instanceof Account){
                return Optional.of(((Account) principal).getEmail());
            }else{
                log.info("authentication can not transfer to Account ");
                return Optional.of("알 수 없음");
            }
        };
    }
}
