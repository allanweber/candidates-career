package com.allanweber.candidatescareer.infrastructure.configuration.resttemplate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@NoArgsConstructor
@Component
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().series() == CLIENT_ERROR || httpResponse.getStatusCode().series() == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

        String message = getMessage(httpResponse);

        if (httpResponse.getStatusCode().series() == SERVER_ERROR) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, message);
        } else if (httpResponse.getStatusCode().series() == CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
            }
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
    }

    private String getMessage(ClientHttpResponse httpResponse) throws IOException {
        String message = new BufferedReader(new InputStreamReader(httpResponse.getBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        //Linkedin Error
        JSONObject jsonObjOfAccessToken = null;
        try {
            jsonObjOfAccessToken = new JSONObject(message);
            String error = Optional.ofNullable(jsonObjOfAccessToken.get("error").toString()).orElse("Error");
            String errorDescription = Optional.ofNullable(jsonObjOfAccessToken.get("error_description").toString()).orElse(message);
            message = error.concat(" - ").concat(errorDescription);
        } catch (JSONException e) {
            log.error("JSON access error", e);
        }
        return message;
    }
}
