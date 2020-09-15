package com.allanweber.candidatescareer.domain.social.github;

import com.allanweber.candidatescareer.domain.social.SocialServiceHelper;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.GitHubConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_PROFILE_URL = "https://api.github.com/user";
    private static final String CLIENT_ID_QUERY = "client_id={client_id}";
    private static final String STATE_QUERY = "state={state}";
    private static final String CLIENT_SECRET_QUERY = "client_secret={client_secret}";
    private static final String CODE_QUERY = "code={code}";
    private static final String FORMAT_JSON = "&format=json";

    private final GitHubConfiguration gitHubConfiguration;
    private final RestTemplate restTemplate;
    private final SocialServiceHelper socialServiceHelper;

    public String getAuthorizationUri(String candidateId) {
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(GITHUB_AUTH_URL))
                .query(CLIENT_ID_QUERY)
                .query(STATE_QUERY)
                .buildAndExpand(gitHubConfiguration.getClientId(), candidateId)
                .toUriString();
    }

    public GitHubProfile callback(String authorizationCode) {
        try {
            String exchangeTokenUri = UriComponentsBuilder.newInstance()
                    .uri(URI.create(GITHUB_TOKEN_URL))
                    .query(CLIENT_ID_QUERY)
                    .query(CLIENT_SECRET_QUERY)
                    .query(CODE_QUERY)
                    .buildAndExpand(gitHubConfiguration.getClientId(), gitHubConfiguration.getClientSecret(), authorizationCode)
                    .toUriString().concat(FORMAT_JSON);

            String accessToken = socialServiceHelper.getToken(exchangeTokenUri, "access_token");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<?> githubDataResponse = restTemplate.exchange(GITHUB_PROFILE_URL, HttpMethod.GET, entity, GitHubData.class);

            if(githubDataResponse.getStatusCode().isError())
            {
                throw new HttpClientErrorException(githubDataResponse.getStatusCode(), githubDataResponse.toString());
            }

            GitHubData gitHubData = (GitHubData)githubDataResponse.getBody();

            if(Objects.isNull(gitHubData)) {
                throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, "Error to get github profile data, the data is null.");
            }

            String base64Image = null;
            if(Objects.nonNull(gitHubData.getImageUrl())){
                base64Image = socialServiceHelper.getBase64Image(gitHubData.getImageUrl());
            }

            return GitHubProfile.builder()
                    .user(gitHubData.getLogin())
                    .name(gitHubData.getName())
                    .company(gitHubData.getCompany())
                    .location(gitHubData.getLocation())
                    .bio(gitHubData.getBio())
                    .imageBase64(base64Image)
                    .apiProfile(gitHubData.getApiProfile())
                    .githubProfile(gitHubData.getGithubProfile())
                    .token(accessToken)
                    .build();
        } catch (JSONException e) {
            log.error("Error when getting linkedin data.", e);
            throw (HttpClientErrorException) new HttpClientErrorException(INTERNAL_SERVER_ERROR, e.getMessage()).initCause(e);
        }
    }
}
