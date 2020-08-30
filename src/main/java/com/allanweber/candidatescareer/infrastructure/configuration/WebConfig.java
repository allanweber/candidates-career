//TODO: REMOVE IT
//package com.allanweber.candidatescareer.infrastructure.configuration;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter() {
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin(CorsConfiguration.ALL);
//        config.addAllowedMethod(CorsConfiguration.ALL);
//        config.addAllowedHeader(CorsConfiguration.ALL);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return new FilterRegistrationBean<>(new CorsFilter(source));
//    }
//}
