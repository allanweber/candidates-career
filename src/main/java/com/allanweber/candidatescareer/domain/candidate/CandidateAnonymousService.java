package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.GITHUB;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.LINKEDIN;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CandidateAnonymousService {

    private static final String NOT_FOUND_MESSAGE = "Candidate not found.";
    private static final String INVALID_SOCIAL_NETWORK_MESSAGE = "Social network request is invalid";

    private final CandidateMongoRepository candidateMongoRepository;

    public SocialEntry getSocialEntry(String id, SocialNetworkType socialNetworkType) {
        return candidateMongoRepository.findById(id)
                .map(Candidate::getSocialEntries)
                .orElse(Collections.emptyList())
                .stream()
                .filter(socialEntry -> socialEntry.getType().equals(socialNetworkType))
                .findFirst()
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, INVALID_SOCIAL_NETWORK_MESSAGE));
    }

    public void saveLinkedInData(String id, LinkedInProfile linkedInProfile) {
        candidateMongoRepository.findById(id)
                .map(candidate -> candidate.markSocialEntry(LINKEDIN, GRANTED))
                .map(candidate -> candidate.addLinkedInData(linkedInProfile))
                .map(candidateMongoRepository::save)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public void saveGitGithubData(String id, GitHubProfile githubProfile) {
        candidateMongoRepository.findById(id)
                .map(candidate -> candidate.markSocialEntry(GITHUB, RUNNING))
                .map(candidate -> candidate.addGithubData(githubProfile))
                .map(candidateMongoRepository::save)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public void denySocialAccess(String id, SocialNetworkType network) {
        candidateMongoRepository.findById(id)
                .map(candidate -> candidate.markSocialEntry(network, DENIED))
                .map(candidateMongoRepository::save)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public void invalidateSocialEntry(String id, SocialNetworkType network, String error) {
        candidateMongoRepository.findById(id)
                .map(candidate -> candidate.markSocialEntry(network, ERROR, error))
                .map(candidateMongoRepository::save)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }
}
