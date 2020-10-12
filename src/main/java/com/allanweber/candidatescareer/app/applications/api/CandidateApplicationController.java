package com.allanweber.candidatescareer.app.applications.api;

import com.allanweber.candidatescareer.app.applications.service.CandidateApplicationService;
import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class CandidateApplicationController implements CandidateApplicationApi {

    private final CandidateApplicationService candidateApplicationService;

    @Override
    public ResponseEntity<Void> accept(String applicationId) {
        String redirection = candidateApplicationService.accept(applicationId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<Void> deny(String applicationId) {
        String redirection = candidateApplicationService.deny(applicationId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<Void> apply(String accessToken, String applicationId, CandidateProfile body) {
        candidateApplicationService.apply(accessToken, applicationId, body);
        return ok().build();
    }

    @Override
    public ResponseEntity<CandidateProfile> getProfile(String accessToken, String applicationId) {
        return ok(candidateApplicationService.getProfile(accessToken, applicationId));
    }

    @Override
    public ResponseEntity<Void> validateAccess(String accessToken, String applicationId) {
        candidateApplicationService.validateToken(accessToken, applicationId);
        return ok().build();
    }

    @Override
    public ResponseEntity<VacancyDto> getVacancy(String accessToken, String applicationId) {
        return ok(candidateApplicationService.getVacancy(accessToken, applicationId));
    }
}
