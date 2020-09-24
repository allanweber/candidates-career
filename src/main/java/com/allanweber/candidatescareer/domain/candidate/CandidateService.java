package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
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
