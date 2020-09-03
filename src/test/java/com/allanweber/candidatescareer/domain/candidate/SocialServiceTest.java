package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.social.SocialService;
import com.allanweber.candidatescareer.domain.social.github.GitHubService;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.domain.social.linkedin.LinkedInService;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.*;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.GRANTED;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.PENDING;
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
    CandidateService candidateService;

    @InjectMocks
    SocialService socialService;

    @Test
    void getAuthorizationUri_linkedIn() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(PENDING).build());
        when(linkedInService.getAuthorizationUri(anyString())).thenReturn("linkedinUri");
        String uri = socialService.getAuthorizationUri("candidateId", LINKEDIN);
        assertEquals("linkedinUri", uri);
    }

    @Test
    void getAuthorizationUri_exception_not_linkedin() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(PENDING).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.getAuthorizationUri("candidateId", TWITTER),
                String.format("Social network request %s not implemented", TWITTER.toString())
        );
    }

    @Test
    void getAuthorizationUri_socialEntry_not_pending() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(GRANTED).build());
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
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(PENDING).build());
        when(linkedInService.callback(authorizationCode)).thenReturn(linkedInProfile);
        doNothing().when(candidateService).saveLinkedInData(candidateId, linkedInProfile);
        socialService.callbackLinkedIn(authorizationCode, candidateId);
    }

    @Test
    void callBackLinkedIn_socialEntry_not_pending() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(GRANTED).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.callbackLinkedIn("authorizationCode", "candidateId"),
                "Social network request was completed"
        );
    }

    @Test
    void getAuthorizationUri_github() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(GITHUB).status(PENDING).build());
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
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(GITHUB).status(PENDING).build());
        when(gitHubService.callback(authorizationCode)).thenReturn(githubProfile);
        doNothing().when(candidateService).saveGitGithubData(candidateId, githubProfile);
        socialService.callbackGithub(authorizationCode, candidateId);
        verify(candidateService).saveGitGithubData(candidateId, githubProfile);
    }

    @Test
    void callBackGithub_socialEntry_not_pending() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(GITHUB).status(GRANTED).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.callbackLinkedIn("authorizationCode", "candidateId"),
                "Social network request was completed"
        );
    }
}