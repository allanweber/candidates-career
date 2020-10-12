package com.allanweber.candidatescareer.app.social.github;

import com.allanweber.candidatescareer.app.social.service.SocialServiceHelper;
import com.allanweber.candidatescareer.app.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.GitHubConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GitHubServiceTest {

    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_PROFILE_URL = "https://api.github.com/user";

    @Mock
    GitHubConfiguration gitHubConfiguration;

    @Mock
    SocialServiceHelper socialServiceHelper;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    GitHubService gitHubService;

    @Test
    void test_generated_url() {
        String state = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        when(gitHubConfiguration.getClientId()).thenReturn(clientId);
        String expected = "https://github.com/login/oauth/authorize?client_id=" + clientId + "&state=" + state;
        String url = gitHubService.getAuthorizationUri(state);
        assertEquals(expected, url);
    }

    @Test
    void test_callback() throws JSONException {
        String clientId = UUID.randomUUID().toString();
        String secretId = UUID.randomUUID().toString();
        String authorizationCode = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        when(gitHubConfiguration.getClientId()).thenReturn(clientId);
        when(gitHubConfiguration.getClientSecret()).thenReturn(secretId);

        String tokenUrl = GITHUB_TOKEN_URL + "?client_id=" + clientId + "&client_secret=" + secretId + "&code=" + authorizationCode + "&format=json";

        when(socialServiceHelper.getToken(tokenUrl, "access_token")).thenReturn(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        GitHubData gitHubData = GitHubData.builder().name("name").location("location").bio("bio")
                .company("company").imageUrl("imageUrl").apiProfile("profileUrl").build();
        when(restTemplate.exchange(GITHUB_PROFILE_URL, HttpMethod.GET, entity, GitHubData.class)).thenReturn(ResponseEntity.ok(gitHubData));

        GitHubProfile gitHubProfile = gitHubService.callback(authorizationCode);
        assertNotNull(gitHubProfile);
        verify(restTemplate).exchange(GITHUB_PROFILE_URL, HttpMethod.GET, entity, GitHubData.class);
        verify(socialServiceHelper).getToken(tokenUrl, "access_token");
        verify(socialServiceHelper).getBase64Image(gitHubData.getImageUrl());
    }

    @Test
    void test_callback_profile_return_error() throws JSONException {
        String clientId = UUID.randomUUID().toString();
        String secretId = UUID.randomUUID().toString();
        String authorizationCode = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        when(gitHubConfiguration.getClientId()).thenReturn(clientId);
        when(gitHubConfiguration.getClientSecret()).thenReturn(secretId);

        when(socialServiceHelper.getToken(anyString(), anyString())).thenReturn(token);

        when(restTemplate.exchange(anyString(), any(), any(), eq(GitHubData.class))).thenReturn(ResponseEntity.status(500).build());

        assertThrows(HttpClientErrorException.class, () -> gitHubService.callback(authorizationCode));

        verify(restTemplate).exchange(anyString(), any(), any(), eq(GitHubData.class));
        verify(socialServiceHelper).getToken(anyString(), anyString());
        verify(socialServiceHelper, never()).getBase64Image(anyString());
    }

    @Test
    void test_callback_profile_return_null() throws JSONException {
        String clientId = UUID.randomUUID().toString();
        String secretId = UUID.randomUUID().toString();
        String authorizationCode = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        when(gitHubConfiguration.getClientId()).thenReturn(clientId);
        when(gitHubConfiguration.getClientSecret()).thenReturn(secretId);

        when(socialServiceHelper.getToken(anyString(), anyString())).thenReturn(token);

        when(restTemplate.exchange(anyString(), any(), any(), eq(GitHubData.class))).thenReturn(ResponseEntity.ok().build());

        assertThrows(HttpClientErrorException.class, () -> gitHubService.callback(authorizationCode));

        verify(restTemplate).exchange(anyString(), any(), any(), eq(GitHubData.class));
        verify(socialServiceHelper).getToken(anyString(), anyString());
        verify(socialServiceHelper, never()).getBase64Image(anyString());
    }

    @Test
    void test_callback_profile_return_empty_image() throws JSONException {
        String clientId = UUID.randomUUID().toString();
        String secretId = UUID.randomUUID().toString();
        String authorizationCode = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        when(gitHubConfiguration.getClientId()).thenReturn(clientId);
        when(gitHubConfiguration.getClientSecret()).thenReturn(secretId);

        when(socialServiceHelper.getToken(anyString(), anyString())).thenReturn(token);

        when(restTemplate.exchange(anyString(), any(), any(), eq(GitHubData.class))).thenReturn(ResponseEntity.ok(GitHubData.builder().build()));

        GitHubProfile gitHubProfile = gitHubService.callback(authorizationCode);
        assertNotNull(gitHubProfile);

        verify(restTemplate).exchange(anyString(), any(), any(), eq(GitHubData.class));
        verify(socialServiceHelper).getToken(anyString(), anyString());
        verify(socialServiceHelper, never()).getBase64Image(anyString());
    }

    @Test
    void test_callback_get_token_return_JSONException() throws JSONException {
        String clientId = UUID.randomUUID().toString();
        String secretId = UUID.randomUUID().toString();
        String authorizationCode = UUID.randomUUID().toString();
        when(gitHubConfiguration.getClientId()).thenReturn(clientId);
        when(gitHubConfiguration.getClientSecret()).thenReturn(secretId);

        when(socialServiceHelper.getToken(anyString(), anyString())).thenThrow(new JSONException("json error"));

        assertThrows(HttpClientErrorException.class, () -> gitHubService.callback(authorizationCode), "json error");

        verify(restTemplate, never()).exchange(anyString(), any(), any(), eq(GitHubData.class));
        verify(socialServiceHelper).getToken(anyString(), anyString());
        verify(socialServiceHelper, never()).getBase64Image(anyString());
    }
}