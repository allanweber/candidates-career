package com.allanweber.candidatescareer.domain.linkedin;

import com.allanweber.candidatescareer.infrastructure.configuration.LinkedInConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LinkedInServiceTest {

    @Mock
    private LinkedInConfiguration linkedInConfiguration;

    @InjectMocks
    private  LinkedInService service;

    @Test
    void  test_generated_url() {
        when(linkedInConfiguration.getClientId()).thenReturn("123456");
        when(linkedInConfiguration.getClientSecret()).thenReturn("456789");
        when(linkedInConfiguration.getRedirectHost()).thenReturn("http://localhost:8080");
        when(linkedInConfiguration.getAuthorizationUrl()).thenReturn("https://www.linkedin.com/oauth/v2/authorization?response_type=code&scope=r_liteprofile%20r_emailaddress");
        when(linkedInConfiguration.getClientIdQuery()).thenReturn("client_id={client_id}");
        when(linkedInConfiguration.getRedirectUriQuery()).thenReturn("redirect_uri={redirect_uri}");
        when(linkedInConfiguration.getStateQuery()).thenReturn("state={state}");
        String state = UUID.randomUUID().toString();
        String expected = "https://www.linkedin.com/oauth/v2/authorization?response_type=code&scope=r_liteprofile%20r_emailaddress&client_id=123456&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Fcallback&state=" + state;

        String url = service.getAuthorizationUri(state);
        assertEquals(expected, url);
    }
}