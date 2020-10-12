package com.allanweber.candidatescareer.app.applications.api;

import com.allanweber.candidatescareer.app.applications.dto.CandidateApplicationResponse;
import com.allanweber.candidatescareer.app.applications.dto.VacancyApplicationResponse;
import com.allanweber.candidatescareer.app.applications.service.RecruiterApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class RecruiterApplicationController implements RecruiterApplicationApi {

    private final RecruiterApplicationService recruiterApplicationService;

    @Override
    public ResponseEntity<CandidateApplicationResponse> sendApplication(String candidateId, String vacancyId) {
        return ok(recruiterApplicationService.sendApplication(candidateId, vacancyId));
    }

    @Override
    public ResponseEntity<List<CandidateApplicationResponse>> getCandidateApplications(String candidateId) {
        return ok(recruiterApplicationService.getCandidateApplications(candidateId));
    }

    @Override
    public ResponseEntity<List<VacancyApplicationResponse>> getVacancyApplications(String vacancyId) {
        return ok(recruiterApplicationService.getVacancyApplications(vacancyId));
    }
}
