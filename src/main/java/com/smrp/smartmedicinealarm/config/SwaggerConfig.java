package com.smrp.smartmedicinealarm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class SwaggerConfig {
    @Bean
    Docket docket(){
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .build()
                .apiInfo(
                        getApiInfo()
                ).ignoredParameterTypes(HttpServletResponse.class)
            ;
    }

    public ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("Smart Medicine Alarm API Doc")
                .description("스마트한 약알리미 API DOC 입니다.")
                .version("1.0.0")
                .contact(new Contact("joon", "https://github.com/wnstlr0615/SmartMedicineAlarm", "ryan0@kakao.com"))
                .build();
    }
}
