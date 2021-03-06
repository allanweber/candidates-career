package com.allanweber.candidatescareer.app.candidate;

import com.allanweber.candidatescareer.app.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.app.candidate.service.CandidateAnonymousService;
import com.allanweber.candidatescareer.app.social.github.GithubMessageQueue;
import com.allanweber.candidatescareer.app.social.service.SocialService;
import com.allanweber.candidatescareer.app.social.github.GitHubService;
import com.allanweber.candidatescareer.app.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.app.social.linkedin.LinkedInService;
import com.allanweber.candidatescareer.app.social.linkedin.dto.LinkedInProfile;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import static com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType.*;
import static com.allanweber.candidatescareer.app.candidate.dto.SocialStatus.GRANTED;
import static com.allanweber.candidatescareer.app.candidate.dto.SocialStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class SocialServiceTest {

    @Mock
    LinkedInService linkedInService;

    @Mock
    GitHubService gitHubService;

    @Mock
    CandidateAnonymousService candidateAnonymousService;

    @Mock
    GithubMessageQueue githubMessageQueue;

    @Mock
    AppHostConfiguration appHostConfiguration;

    @InjectMocks
    SocialService socialService;

    @Test
    void getAuthorizationUri_linkedIn() {
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(PENDING).build());
        when(linkedInService.getAuthorizationUri(anyString())).thenReturn("linkedinUri");
        String uri = socialService.getAuthorizationUri("candidateId", LINKEDIN);
        assertEquals("linkedinUri", uri);
    }

    @Test
    void getAuthorizationUri_exception_not_linkedin() {
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(PENDING).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.getAuthorizationUri("candidateId", TWITTER),
                String.format("Social network request %s not implemented", TWITTER.toString())
        );
    }

    @Test
    void getAuthorizationUri_socialEntry_not_pending() {
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(GRANTED).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.getAuthorizationUri("candidateId", LINKEDIN),
                "Social network request was completed"
        );
    }


    @Test
    void callBackLinkedIn() {
        String authorizationCode = "authorizationCode";
        String candidateId = "candidateId";
        LinkedInProfile linkedInProfile = new LinkedInProfile("fist", "last", "image");
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(PENDING).build());
        when(linkedInService.callback(authorizationCode)).thenReturn(linkedInProfile);
        doNothing().when(candidateAnonymousService).saveLinkedInData(candidateId, linkedInProfile);
        socialService.callbackLinkedIn(authorizationCode, candidateId);
    }

    @Test
    void callBackLinkedIn_socialEntry_not_pending() {
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(GRANTED).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.callbackLinkedIn("authorizationCode", "candidateId"),
                "Social network request was completed"
        );
    }

    @Test
    void getAuthorizationUri_github() {
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(GITHUB).status(PENDING).build());
        when(gitHubService.getAuthorizationUri(anyString())).thenReturn("githubUri");
        String uri = socialService.getAuthorizationUri("candidateId", GITHUB);
        assertEquals("githubUri", uri);
    }

    @Test
    void callBackGithub() {
        String authorizationCode = "authorizationCode";
        String candidateId = "candidateId";
        GitHubProfile githubProfile = GitHubProfile.builder()
                .name("new name")
                .company("company")
                .bio("my bio")
                .apiProfile("my profile")
                .imageBase64("base64image")
                .location("where I live")
                .build();
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(GITHUB).status(PENDING).build());
        when(gitHubService.callback(authorizationCode)).thenReturn(githubProfile);
        when(appHostConfiguration.getFrontEnd()).thenReturn("http://localhost");
        doNothing().when(candidateAnonymousService).saveGitGithubData(candidateId, githubProfile);
        doNothing().when(githubMessageQueue).send(any());
        String redirect = socialService.callbackGithub(authorizationCode, candidateId);
        assertEquals("http://localhost/auth/social-granted", redirect);
        verify(candidateAnonymousService).saveGitGithubData(candidateId, githubProfile);
    }

    @Test
    void callBackGithub_socialEntry_not_pending() {
        when(candidateAnonymousService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(GITHUB).status(GRANTED).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.callbackLinkedIn("authorizationCode", "candidateId"),
                "Social network request was completed"
        );
    }
}