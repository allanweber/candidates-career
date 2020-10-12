package com.allanweber.candidatescareer.app.social.service;

import com.allanweber.candidatescareer.app.candidate.service.CandidateAnonymousService;
import com.allanweber.candidatescareer.app.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.app.social.dto.GitHubProfileMessage;
import com.allanweber.candidatescareer.app.social.github.GitHubService;
import com.allanweber.candidatescareer.app.social.github.GithubMessageQueue;
import com.allanweber.candidatescareer.app.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.app.social.linkedin.LinkedInService;
import com.allanweber.candidatescareer.app.social.linkedin.dto.LinkedInProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType.GITHUB;
import static com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType.LINKEDIN;
import static com.allanweber.candidatescareer.app.candidate.dto.SocialStatus.PENDING;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RequiredArgsConstructor
@Service
public class SocialService {

    private static final String NOT_IMPLEMENTED_MESSAGE = "Acesso ao tipo de rede social %s não está disponível";
    private static final String SOCIAL_NETWORK_COMPLETED = "Acesso à rede social já foi concluído";
    private static final String AUTH_PATH = "auth";
    private static final String SOCIAL_GRANTED_PATH = "social-granted";
    private static final String SOCIAL_DENIED_PATH = "social-denied";

    private final LinkedInService linkedInService;
    private final GitHubService gitHubService;
    private final CandidateAnonymousService candidateAnonymousService;
    private final GithubMessageQueue githubMessageQueue;
    private final AppHostConfiguration appHostConfiguration;

    public String getAuthorizationUri(String candidateId, SocialNetworkType socialNetworkType) {
        validateSocialEntry(candidateId, socialNetworkType);
        String url;
        if (socialNetworkType.equals(LINKEDIN)) {
            url = linkedInService.getAuthorizationUri(candidateId);
        } else if (socialNetworkType.equals(GITHUB)) {
            url = gitHubService.getAuthorizationUri(candidateId);
        } else {
            throw new HttpClientErrorException(NOT_IMPLEMENTED, String.format(NOT_IMPLEMENTED_MESSAGE, socialNetworkType.toString()));
        }
        return url;
    }

    public void callbackLinkedIn(String authorizationCode, String candidateId) {
        validateSocialEntry(candidateId, LINKEDIN);
        LinkedInProfile linkedInProfile = linkedInService.callback(authorizationCode);
        candidateAnonymousService.saveLinkedInData(candidateId, linkedInProfile);
    }

    public String callbackGithub(String authorizationCode, String candidateId) {
        validateSocialEntry(candidateId, GITHUB);
        GitHubProfile githubProfile = gitHubService.callback(authorizationCode);
        candidateAnonymousService.saveGitGithubData(candidateId, githubProfile);
        try {
            githubMessageQueue.send(
                    GitHubProfileMessage.builder()
                            .user(githubProfile.getUser())
                            .candidateId(candidateId)
                            .apiProfile(githubProfile.getApiProfile())
                            .token(githubProfile.getToken())
                            .build()
            );
        } catch (AmqpException e) {
            candidateAnonymousService.invalidateSocialEntry(candidateId, GITHUB, e.getMessage());
        }

        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(AUTH_PATH)
                .pathSegment(SOCIAL_GRANTED_PATH)
                .toUriString();
    }

    public String denyAccess(String candidateId, SocialNetworkType network) {
        validateSocialEntry(candidateId, network);
        candidateAnonymousService.denySocialAccess(candidateId, network);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(AUTH_PATH)
                .pathSegment(SOCIAL_DENIED_PATH)
                .toUriString();
    }

    private void validateSocialEntry(String candidateId, SocialNetworkType socialNetworkType) {
        SocialEntry socialEntry = candidateAnonymousService.getSocialEntry(candidateId, socialNetworkType);
        if (!socialEntry.getStatus().equals(PENDING)) {
            throw new HttpClientErrorException(BAD_REQUEST, SOCIAL_NETWORK_COMPLETED);
        }
    }
}
