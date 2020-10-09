package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateRegisterCandidateActionsService;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class CandidateRegisterController implements CandidateRegisterApi {

    private final CandidateRegisterCandidateActionsService candidateRegisterCandidateActionsService;

    @Override
    public ResponseEntity<Void> accept(String registerId) {
        String redirection = candidateRegisterCandidateActionsService.accept(registerId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<Void> deny(String registerId) {
        String redirection = candidateRegisterCandidateActionsService.deny(registerId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<Void> register( String accessToken, String registerId, CandidateProfile body) {
        candidateRegisterCandidateActionsService.register(accessToken, registerId, body);
        return ok().build();
    }

    @Override
    public ResponseEntity<CandidateProfile> getProfile(String accessToken, String registerId) {
        return ok(candidateRegisterCandidateActionsService.getProfile(accessToken, registerId));
    }

    @Override
    public ResponseEntity<Void> validateAccess(String accessToken, String registerId) {
        candidateRegisterCandidateActionsService.validateToken(accessToken, registerId);
         return ok().build();
    }

    @Override
    public ResponseEntity<VacancyDto> getVacancy(String accessToken, String registerId) {
        return ok(candidateRegisterCandidateActionsService.getVacancy(accessToken, registerId));
    }
}
