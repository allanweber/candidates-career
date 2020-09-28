package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateAuthenticatedRepository;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepositoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CandidateRepositoriesService {

    private static final String NOT_FOUND_MESSAGE = "Candidato não encontrado.";

    private final CandidateAuthenticatedRepository authenticatedRepository;
    private final CandidateRepositoriesRepository candidateRepositoriesRepository;

    public Integer count(String candidateId) {
        Candidate candidate = authenticatedRepository.findById(candidateId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));

        return candidateRepositoriesRepository.countRepositories(candidateId, candidate.getOwner());
    }
}
