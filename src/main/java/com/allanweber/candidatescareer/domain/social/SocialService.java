package com.allanweber.candidatescareer.domain.social;

import com.allanweber.candidatescareer.domain.candidate.CandidateService;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.social.dto.GitHubProfileMessage;
import com.allanweber.candidatescareer.domain.social.github.GitHubService;
import com.allanweber.candidatescareer.domain.social.github.GithubMessageQueue;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.domain.social.linkedin.LinkedInService;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.GITHUB;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.LINKEDIN;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.PENDING;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RequiredArgsConstructor
@Service
public class SocialService {

    private static final String NOT_IMPLEMENTED_MESSAGE = "Social network request %s not implemented";
    private static final String SOCIAL_NETWORK_COMPLETED = "Social network request was completed";

    private final LinkedInService linkedInService;
    private final GitHubService gitHubService;
    private final CandidateService candidateService;
    private final GithubMessageQueue githubMessageQueue;

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
        candidateService.saveLinkedInData(candidateId, linkedInProfile);
    }

    public void callbackGithub(String authorizationCode, String candidateId) {
        validateSocialEntry(candidateId, GITHUB);
        GitHubProfile githubProfile = gitHubService.callback(authorizationCode);
        candidateService.saveGitGithubData(candidateId, githubProfile);
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
            candidateService.invalidateSocialEntry(candidateId, GITHUB, e.getMessage());
        }
    }

    public void denyAccess(String candidateId, SocialNetworkType network) {
        validateSocialEntry(candidateId, network);
        candidateService.denySocialAccess(candidateId, network);
    }

    private void validateSocialEntry(String candidateId, SocialNetworkType socialNetworkType) {
        SocialEntry socialEntry = candidateService.getSocialEntry(candidateId, socialNetworkType);
        if (!socialEntry.getStatus().equals(PENDING)) {
            throw new HttpClientErrorException(BAD_REQUEST, SOCIAL_NETWORK_COMPLETED);
        }
    }
}
