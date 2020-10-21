package com.allanweber.candidatescareer.app.candidate_repositories.service;

import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.candidate.repository.CandidateAuthenticatedRepository;
import com.allanweber.candidatescareer.app.candidate_repositories.dto.GithubRepository;
import com.allanweber.candidatescareer.app.candidate_repositories.dto.RepositoryCounter;
import com.allanweber.candidatescareer.app.candidate_repositories.repository.CandidateRepositoriesQueries;
import com.allanweber.candidatescareer.app.candidate_repositories.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CandidateRepositoriesService {

    private static final String NOT_FOUND_MESSAGE = "Candidato não encontrado.";
    private static final String[] SORTS = {"name", "updatedAt", "stars", "watchers", "commits", "pulls", "branches"};
    public static final String DEFAULT_SORT = "name";

    private final CandidateAuthenticatedRepository authenticatedRepository;
    private final CandidateRepositoriesQueries candidateRepositoriesQueries;
    private final CandidateRepository candidateRepository;

    public RepositoryCounter count(String candidateId) {
        Candidate candidate = authenticatedRepository.findById(candidateId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));

        RepositoryCounter repositoryCounter = candidateRepositoriesQueries.countFields(candidateId, candidate.getOwner());
        Integer repositories = candidateRepositoriesQueries.countRepositories(candidateId, candidate.getOwner());
        repositoryCounter.setRepositories(repositories);
        return repositoryCounter;
    }

    public List<GithubRepository> getRepositories(String candidateId, int size, Integer offset, String sort) {
        authenticatedRepository.findById(candidateId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
        Sort sortBy = getSortField(sort);
        Pageable page = PageRequest.of(offset, size, sortBy);
        return candidateRepository.getByCandidateId(candidateId, page);
    }

    private Sort getSortField(String sort) {
        String sortField = sort;
        if(!Arrays.asList(SORTS).contains(sortField)) {
            sortField = DEFAULT_SORT;
        }
        Sort sortBy = Sort.by(sortField);
        if (!DEFAULT_SORT.equals(sortField)) {
            sortBy = sortBy.descending();
        }
        return sortBy;
    }
}
