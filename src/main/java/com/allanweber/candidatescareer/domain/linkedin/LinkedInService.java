package com.allanweber.candidatescareer.domain.linkedin;

import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.LinkedInConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class LinkedInService {

    private static final String CALL_BACK_METHOD = "auth/callback";

    private final LinkedInConfiguration linkedInConfiguration;
    private final RestTemplate restTemplate;
    private final ObjectReader reader = ObjectMapperHelper.get().readerFor(LinkedInData.class);

    public String getAuthorizationUri(String candidateId) {
        String redirectUri = UriComponentsBuilder.newInstance()
                .uri(URI.create(linkedInConfiguration.getRedirectHost()))
                .path(CALL_BACK_METHOD)
                .toUriString();
        String redirectEncoded = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(linkedInConfiguration.getAuthorizationUrl()))
                .query(linkedInConfiguration.getClientIdQuery())
                .query(linkedInConfiguration.getRedirectUriQuery())
                .query(linkedInConfiguration.getStateQuery())
                .buildAndExpand(linkedInConfiguration.getClientId(), redirectEncoded, candidateId)
                .toUriString();
    }

    public LinkedInProfile callBackLinkedIn(String authorizationCode) {
        try {
            String redirectUri = UriComponentsBuilder.newInstance()
                    .uri(URI.create(linkedInConfiguration.getRedirectHost()))
                    .path(CALL_BACK_METHOD)
                    .toUriString();

            //To trade your authorization code for access token
            String accessTokenUri = "https://www.linkedin.com/oauth/v2/accessToken?grant_type=authorization_code&code=" + authorizationCode +
                    "&redirect_uri=" + redirectUri +
                    "&client_id=" + linkedInConfiguration.getClientId() +
                    "&client_secret=" + linkedInConfiguration.getClientSecret();

            String accessTokenRequest = restTemplate.getForObject(accessTokenUri, String.class);
            JSONObject jsonObjOfAccessToken = new JSONObject(accessTokenRequest);
            String accessToken = jsonObjOfAccessToken.get("access_token").toString();

            String linkedInDetailUri = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> linkedinDetailRequest = restTemplate.exchange(linkedInDetailUri, HttpMethod.GET, entity, String.class);

            LinkedInData linkedInData = reader.readValue(linkedinDetailRequest.getBody());

            Optional<String> profilePictureUrl = linkedInData.getProfilePicture().getDisplayImage().getElements().stream()
                    .filter(element -> element.getAuthorizationMethod().equals("PUBLIC"))
                    .filter(element -> element.getData().getStillImage().getStorageSize().getWidth() < 500)
                    .reduce((first, second) -> second)
                    .map(element -> element.getIdentifiers().stream().findFirst().orElse(new IdentifierImage()).getIdentifier());

            String base64Image = null;
            if(profilePictureUrl.isPresent()) {
                base64Image = getBase64Image(profilePictureUrl.get());
            }

            String firstName = getLinkedInName(linkedInData.getFirstName().getName());
            String lastName = getLinkedInName(linkedInData.getLastName().getName());

            return new LinkedInProfile(firstName, lastName, base64Image);

        } catch (JSONException | JsonProcessingException e) {
            throw (HttpClientErrorException)new HttpClientErrorException(INTERNAL_SERVER_ERROR, e.getMessage()).initCause(e);
        }
    }

    private String getBase64Image(String profilePictureUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(profilePictureUrl, HttpMethod.GET, entity, byte[].class);
        return Base64.getEncoder().encodeToString(response.getBody());
    }

    private String getLinkedInName(Map<String, String> name){
        Map.Entry<String,String> entry = name.entrySet().iterator().next();
        return entry.getValue();
    }
}
