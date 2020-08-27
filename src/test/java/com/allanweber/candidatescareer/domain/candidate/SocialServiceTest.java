package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.linkedin.LinkedInService;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.LINKEDIN;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.TWITTER;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.GRANTED;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SocialServiceTest {

    @Mock
    LinkedInService linkedInService;

    @Mock
    CandidateService candidateService;

    @InjectMocks
    SocialService socialService;

    @Test
    void getAuthorizationUri() {
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
        when(linkedInService.callBackLinkedIn(authorizationCode)).thenReturn(linkedInProfile);
        doNothing().when(candidateService).saveLinkedInData(candidateId, linkedInProfile);
        socialService.callBackLinkedIn(authorizationCode, candidateId);
    }

    @Test
    void callBackLinkedIn_socialEntry_not_pending() {
        when(candidateService.getSocialEntry(anyString(), any())).thenReturn(SocialEntry.builder().type(LINKEDIN).status(GRANTED).build());
        assertThrows(
                HttpClientErrorException.class,
                () -> socialService.callBackLinkedIn("authorizationCode", "candidateId"),
                "Social network request was completed"
        );
    }
}