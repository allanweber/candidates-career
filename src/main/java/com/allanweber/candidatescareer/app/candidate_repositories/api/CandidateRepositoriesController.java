package com.allanweber.candidatescareer.app.candidate_repositories.api;

import com.allanweber.candidatescareer.app.candidate_repositories.service.CandidateRepositoriesService;
import com.allanweber.candidatescareer.app.candidate_repositories.dto.RepositoryCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class CandidateRepositoriesController implements CandidateRepositoriesApi {

    private final CandidateRepositoriesService candidateRepositoriesService;

    @Override
    public ResponseEntity<RepositoryCounter> count(String id) {
        return ok(candidateRepositoriesService.count(id));
    }
}
