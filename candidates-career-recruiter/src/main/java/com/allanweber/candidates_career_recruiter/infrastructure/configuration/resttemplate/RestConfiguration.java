package com.allanweber.candidates_career_recruiter.infrastructure.configuration.resttemplate;

import com.allanweber.candidates_career_recruiter.infrastructure.HttpClientProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class RestConfiguration {

    private final HttpClientProperties httpClientProperties;
    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

    @Bean
    public RestTemplate createRestTemplate() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(httpClientProperties.getReadTimeoutInMs());
        factory.setConnectTimeout(httpClientProperties.getConnectTimeoutInMs());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
        return restTemplate;
    }
}
