package com.smrp.smartmedicinealarm.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smrp.smartmedicinealarm.entity.account.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.util.Optional;

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
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication();
            return Optional.of(account.getAccountId().toString());
        };
    }
}
