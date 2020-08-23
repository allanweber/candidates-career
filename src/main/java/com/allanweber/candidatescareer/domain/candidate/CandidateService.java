package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.LINKEDIN;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private static final String NOT_FOUND_MESSAGE = "Candidate not found.";
    private static final String INVALID_SOCIAL_NETWORK_MESSAGE = "Social network request is invalid";
    private static final String EMAIL_EXIST_MESSAGE = "Candidate email %s already exist";

    private final CandidateRepository repository;

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

        var entity = repository.insert(CandidateMapper.toEntity(body));
        return CandidateMapper.toResponse(entity);
    }

    public void delete(String id) {
        repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
        repository.deleteById(id);
    }

    public String getImage(String id) {
        return  repository
                .findById(id)
                .map(Candidate::getImage)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public CandidateResponse addSocialEntries(String id, List<SocialNetworkType> networkTypes) {
        return repository.findById(id)
                .map(candidate -> candidate.removeEqualEntries(networkTypes))
                .map(candidate -> candidate.addSocialEntriesPending(networkTypes))
                .map(repository::save)
                .map(CandidateMapper::toResponse)
                // Send email
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    public SocialEntry getSocialEntry(String candidateId, SocialNetworkType socialNetworkType) {
        return getById(candidateId)
                .getSocialEntries()
                .stream()
                .filter(socialEntry -> socialEntry.getType().equals(socialNetworkType))
                .findFirst()
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, INVALID_SOCIAL_NETWORK_MESSAGE));
    }

    public void saveLinkedInData(String id, LinkedInProfile linkedInProfile) {
        repository.findById(id)
                .map(candidate -> candidate.markSocialEntryDone(LINKEDIN))
                .map(candidate -> candidate.addLinkedInData(linkedInProfile))
                .map(repository::save)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }
}
