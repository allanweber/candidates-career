package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.SocialService;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class AppsAuthorizationController implements AppsAuthorizationApi {

    private final SocialService socialService;

    @Override
    public ResponseEntity<Void> socialAuthorizationLinkedIn(String candidateId) {
        String authorizationUri = socialService.getAuthorizationUri(candidateId, SocialNetworkType.LINKEDIN);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(authorizationUri))
                .build();
    }

    @Override
    public ResponseEntity<Void> linkedInCallback(String authorizationCode, String state) {
        socialService.callBackLinkedIn(authorizationCode, state);
        return ok().build();
    }
}
