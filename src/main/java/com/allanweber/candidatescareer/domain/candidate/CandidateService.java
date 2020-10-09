package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.*;
import com.allanweber.candidatescareer.domain.candidate.email.CandidateSocialEmailService;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateRegisterMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateAuthenticatedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private static final String NOT_FOUND_MESSAGE = "Candidato não encontrado.";
    private static final String EMAIL_EXIST_MESSAGE = "Email %s já existe para outro candidato";

    private final CandidateAuthenticatedRepository repository;
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

    public CandidateProfile getProfile(String id) {
        Candidate candidate = repository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
        return CandidateRegisterMapper.toResponse(candidate);
    }

    public CandidateResponse update(String id, CandidateUpdate body) {
        if (repository.getByEmail(body.getEmail()).filter(candidate -> !candidate.getId().equals(id)).isPresent()) {
            throw new HttpClientErrorException(CONFLICT, String.format(EMAIL_EXIST_MESSAGE, body.getEmail()));
        }
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
        Candidate candidate = repository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
        return candidate.getImage();
    }

    public List<SocialEntry> addSocialEntries(String id, List<SocialNetworkType> networkTypes) {
        var candidateResponse = repository.findById(id)
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
}
