package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.linkedin.LinkedInService;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.LINKEDIN;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.PENDING;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RequiredArgsConstructor
@Service
public class SocialService {

    private static final String NOT_IMPLEMENTED_MESSAGE = "Social network request %s not implemented";
    private static final String SOCIAL_NETWORK_COMPLETED = "Social network request was completed already";

    private final LinkedInService linkedInService;
    private final CandidateService candidateService;

    public String getAuthorizationUri(String candidateId, SocialNetworkType socialNetworkType) {
        validateSocialEntry(candidateId, socialNetworkType);
        if(socialNetworkType.equals(LINKEDIN)){
            return linkedInService.getAuthorizationUri(candidateId);
        }
        throw new HttpClientErrorException(NOT_IMPLEMENTED, String.format(NOT_IMPLEMENTED_MESSAGE, socialNetworkType.toString()));
    }

    public void callBackLinkedIn(String authorizationCode, String candidateId) {
        validateSocialEntry(candidateId, LINKEDIN);
        LinkedInProfile linkedInProfile = linkedInService.callBackLinkedIn(authorizationCode);
        candidateService.saveLinkedInData(candidateId, linkedInProfile);
    }

    private void validateSocialEntry(String candidateId, SocialNetworkType socialNetworkType) {
        SocialEntry socialEntry = candidateService.getSocialEntry(candidateId, socialNetworkType);
        if(!socialEntry.getStatus().equals(PENDING)) {
            throw new HttpClientErrorException(BAD_REQUEST, SOCIAL_NETWORK_COMPLETED);
        }
    }
}
