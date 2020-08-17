package com.allanweber.candidatescareer.infrastructure.configuration;

import com.allanweber.candidatescareer.infrastructure.configuration.swagger.SwaggerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SwaggerFilter> registrySwaggerFilter(){
        FilterRegistrationBean<SwaggerFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        SwaggerFilter swaggerFilter = new SwaggerFilter();
        filterRegistrationBean.setFilter(swaggerFilter);
        return filterRegistrationBean;
    }
}
