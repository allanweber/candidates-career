package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateRepositoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class CandidateRepositoriesController implements CandidateRepositoriesApi {

    private final CandidateRepositoriesService candidateRepositoriesService;

    @Override
    public ResponseEntity<Integer> count(String id) {
        return ok(candidateRepositoriesService.count(id));
    }
}
