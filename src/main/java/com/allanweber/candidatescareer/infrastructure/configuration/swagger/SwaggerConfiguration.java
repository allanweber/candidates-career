package com.allanweber.candidatescareer.infrastructure.configuration.swagger;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    private static final String API_BASE_PACKAGE = "com.allanweber.candidatescareer.app";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(API_BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .pathMapping("/")
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Allan Weber", "https://allanweber.dev", "a.cassianoweber@gmail.com");
        return new ApiInfo(
                "Candidates Career",
                "A HTTP REST API to manage candidates, vacancies and applications.",
                "1.0",
                "Terms of service",
                contact,
                "MIT", "", Collections.emptyList());
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer Token", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("Bearer Token", authorizationScopes));
    }
}
