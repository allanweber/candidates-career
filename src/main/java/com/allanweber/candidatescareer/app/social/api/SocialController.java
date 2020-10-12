package com.allanweber.candidatescareer.app.social.api;

import com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.app.social.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class SocialController implements SocialApi {

    private final SocialService socialService;

    @Override
    public ResponseEntity<Void> socialAuthorizationLinkedIn(String candidateId) {
        return redirectSocialAuth(candidateId, SocialNetworkType.LINKEDIN);
    }

    @Override
    public ResponseEntity<Void> linkedInCallback(String authorizationCode, String state) {
        socialService.callbackLinkedIn(authorizationCode, state);
        return ok().build();
    }

    @Override
    public ResponseEntity<Void> socialAuthorizationGitHub(String candidateId) {
        return redirectSocialAuth(candidateId, SocialNetworkType.GITHUB);
    }

    @Override
    public ResponseEntity<Void> githubCallback(String authorizationCode, String state) {
        String redirection = socialService.callbackGithub(authorizationCode, state);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<Void> denySocialAuthorization(String candidateId, SocialNetworkType network) {
        String redirection = socialService.denyAccess(candidateId, network);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    private ResponseEntity<Void> redirectSocialAuth(String candidateId, SocialNetworkType socialNetworkType) {
        String authorizationUri = socialService.getAuthorizationUri(candidateId, socialNetworkType);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(authorizationUri))
                .build();
    }
}
