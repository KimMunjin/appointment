package com.zerobase.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api(){
        String title = "약속 기능 구현 프로젝트";
        String description ="약속 기능에 필요한 API를 구현한다.";
        String version = "1.0";

        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zerobase.appointment"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        String title = "약속 기능 구현 프로젝트";
        String description ="약속 기능에 필요한 API를 구현한다.";
        String version = "1.0";

        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .build();
    }
}
