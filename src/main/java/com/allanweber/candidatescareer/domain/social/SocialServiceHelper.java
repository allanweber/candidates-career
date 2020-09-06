package com.allanweber.candidatescareer.domain.social;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class SocialServiceHelper {
    private final RestTemplate restTemplate;

    public String getToken(String exchangeTokenUri, String tokenPath) throws JSONException {
        String accessTokenRequest = restTemplate.getForObject(exchangeTokenUri, String.class);
        JSONObject jsonObjOfAccessToken = new JSONObject(accessTokenRequest);
        return jsonObjOfAccessToken.get(tokenPath).toString();
    }

    public String getBase64Image(String profilePictureUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(profilePictureUrl, HttpMethod.GET, entity, byte[].class);
        return Base64.getEncoder().encodeToString(response.getBody());
    }
}
