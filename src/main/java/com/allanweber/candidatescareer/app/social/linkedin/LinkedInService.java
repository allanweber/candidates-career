package com.allanweber.candidatescareer.app.social.linkedin;

import com.allanweber.candidatescareer.app.social.service.SocialServiceHelper;
import com.allanweber.candidatescareer.app.helper.ObjectMapperHelper;
import com.allanweber.candidatescareer.app.social.linkedin.dto.LinkedInProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.LinkedInConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkedInService {

    private static final String CALL_BACK_METHOD = "auth/callback";
    private static final String LINKEDIN_AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization?response_type=code&scope=r_liteprofile%20r_emailaddress";
    private static final String LINKEDIN_TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken?grant_type=authorization_code&code=";
    private static final String LINKEDIN_PROFILE_URL = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";
    private static final String CLIENT_ID_QUERY = "client_id={client_id}";
    private static final String REDIRECT_URI_QUERY = "redirect_uri={redirect_uri}";
    private static final String STATE_QUERY = "state={state}";

    private final LinkedInConfiguration linkedInConfiguration;
    private final RestTemplate restTemplate;
    private final SocialServiceHelper socialServiceHelper;
    private final ObjectReader reader = ObjectMapperHelper.get().readerFor(LinkedInData.class);

    public String getAuthorizationUri(String candidateId) {
        String redirectUri = UriComponentsBuilder.newInstance()
                .uri(URI.create(linkedInConfiguration.getRedirectHost()))
                .path(CALL_BACK_METHOD)
                .toUriString();
        String redirectEncoded = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(LINKEDIN_AUTH_URL))
                .query(CLIENT_ID_QUERY)
                .query(REDIRECT_URI_QUERY)
                .query(STATE_QUERY)
                .buildAndExpand(linkedInConfiguration.getClientId(), redirectEncoded, candidateId)
                .toUriString();
    }

    public LinkedInProfile callback(String authorizationCode) {
        try {
            LinkedInData linkedInData = getLinkedInData(authorizationCode);
            String base64Image = getImage(linkedInData);
            String firstName = null;
            if (Objects.nonNull(linkedInData.getFirstName())) {
                firstName = getLinkedInName(linkedInData.getFirstName().getName());
            }
            String lastName = null;
            if (Objects.nonNull(linkedInData.getLastName())) {
                lastName = getLinkedInName(linkedInData.getLastName().getName());
            }
            return new LinkedInProfile(firstName, lastName, base64Image);
        } catch (JSONException | JsonProcessingException e) {
            log.error("Error when getting linkedin data.", e);
            throw (HttpClientErrorException) new HttpClientErrorException(INTERNAL_SERVER_ERROR, e.getMessage()).initCause(e);
        }
    }

    private LinkedInData getLinkedInData(String authorizationCode) throws JSONException, JsonProcessingException {
        String redirectUri = UriComponentsBuilder.newInstance()
                .uri(URI.create(linkedInConfiguration.getRedirectHost()))
                .path(CALL_BACK_METHOD)
                .toUriString();
        String accessTokenUri = LINKEDIN_TOKEN_URL + authorizationCode +
                "&redirect_uri=" + redirectUri +
                "&client_id=" + linkedInConfiguration.getClientId() +
                "&client_secret=" + linkedInConfiguration.getClientSecret();
        String accessToken = socialServiceHelper.getToken(accessTokenUri, "access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> linkedinDetailRequest = restTemplate.exchange(LINKEDIN_PROFILE_URL, HttpMethod.GET, entity, String.class);
        return reader.readValue(linkedinDetailRequest.getBody());
    }

    @SuppressWarnings("PMD")
    private String getImage(LinkedInData linkedInData) {
        String base64Image = null;
        try {
            Optional<String> profilePictureUrl = Optional.ofNullable(linkedInData.getProfilePicture())
                    .map(ProfilePicture::getDisplayImage)
                    .map(DisplayImage::getElements)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(element -> element.getAuthorizationMethod().equals("PUBLIC"))
                    .filter(element -> element.getData().getStillImage().getStorageSize().getWidth() < 500)
                    .reduce((first, second) -> second)
                    .map(element -> element.getIdentifiers().stream().findFirst().orElse(new IdentifierImage()).getIdentifier());

            if (profilePictureUrl.isPresent()) {
                base64Image = socialServiceHelper.getBase64Image(profilePictureUrl.get());
            }
        } catch (Exception e) {
            log.error("Error when getting image.", e);
        }

        return base64Image;
    }

    @SuppressWarnings("PMD")
    private String getLinkedInName(Map<String, String> name) {
        if (name == null) return null;
        Map.Entry<String, String> entry = name.entrySet().iterator().next();
        return entry.getValue();
    }
}
