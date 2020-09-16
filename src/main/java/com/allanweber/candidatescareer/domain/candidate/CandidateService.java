package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateAuthenticatedRepository;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.GITHUB;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.LINKEDIN;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private static final String NOT_FOUND_MESSAGE = "Candidate not found.";
    private static final String INVALID_SOCIAL_NETWORK_MESSAGE = "Social network request is invalid";
    private static final String EMAIL_EXIST_MESSAGE = "Candidate email %s already exist";

    private final CandidateAuthenticatedRepository repository;
    private final CandidateMongoRepository candidateMongoRepository;
    private final CandidateSocialEmailService candidateSocialEmailService;

    public List<CandidateResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(CandidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CandidateResponse getById(String id) {
        return repository.findById(id)
                .map(CandidateMapper::toResponse)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public CandidateResponse update(String id, CandidateRequest body) {
        return repository.findById(id)
                .map(entity -> CandidateMapper.mapToUpdate(entity, body))
                .map(repository::save)
                .map(CandidateMapper::toResponse)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public CandidateResponse insert(CandidateRequest body) {
        if (repository.getByEmail(body.getEmail()).isPresent()) {
            throw new HttpClientErrorException(CONFLICT, String.format(EMAIL_EXIST_MESSAGE, body.getEmail()));
        }
        var entity = repository.save(CandidateMapper.toEntity(body));
        return CandidateMapper.toResponse(entity);
    }

    public void delete(String id) {
        repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
        repository.deleteById(id);
    }

    public String getImage(String id) {
        return repository
                .findById(id)
                .map(Candidate::getImage)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public List<SocialEntry> addSocialEntries(String id, List<SocialNetworkType> networkTypes) {
        CandidateResponse candidateResponse = repository.findById(id)
                .map(candidate -> candidate.addSocialEntriesPending(networkTypes))
                .map(repository::save)
                .map(CandidateMapper::toResponse)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));

        networkTypes.forEach(networkType -> candidateSocialEmailService.sendSocialAccess(candidateResponse, networkType));

        return candidateResponse.getSocialEntries()
                .stream()
                .filter(entry -> networkTypes.contains(entry.getType()))
                .collect(Collectors.toList());
    }

    //
    // These methods below does not need a authenticated user
    //
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
