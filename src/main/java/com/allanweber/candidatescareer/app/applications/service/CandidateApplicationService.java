package com.allanweber.candidatescareer.app.applications.service;

import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.app.candidate.mapper.CandidateApplicationMapper;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.app.applications.repository.CandidateApplication;
import com.allanweber.candidatescareer.app.applications.repository.CandidateApplicationRepository;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.app.vacancy.mapper.VacancyMapper;
import com.allanweber.candidatescareer.app.vacancy.repository.Vacancy;
import com.allanweber.candidatescareer.app.vacancy.repository.VacancyMongoRepository;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

import static com.allanweber.candidatescareer.app.applications.dto.CandidateApplicationStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class CandidateApplicationService {

    public static final String EMAIL_ALREADY_EXIST = "Email %s já esta sendo utilizado por outro candidato.";

    private final CandidateApplicationRepository candidateApplicationRepository;
    private final AppHostConfiguration appHostConfiguration;
    private final CandidateMongoRepository candidateMongoRepository;
    private final VacancyMongoRepository vacancyMongoRepository;

    public void validateToken(String accessToken, String applicationId) {
        validateTokenAndGetApplication(accessToken, applicationId);
    }

    public String accept(String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        candidateApplication.setStatus(ACCEPTED);
        candidateApplication.setUpdated(LocalDateTime.now());
        candidateApplicationRepository.save(candidateApplication);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(CandidateApplicationUtils.APPLICATION_PATH)
                .pathSegment(CandidateApplicationUtils.APPLICATION_ACCEPT)
                .path(applicationId)
                .toUriString();
    }

    public String deny(String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        if (!candidateApplication.getStatus().equals(PENDING)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateApplicationUtils.INVALID_STATUS_MESSAGE);
        }
        candidateApplication.setStatus(DENIED);
        candidateApplication.setUpdated(LocalDateTime.now());
        candidateApplicationRepository.save(candidateApplication);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(CandidateApplicationUtils.APPLICATION_PATH)
                .pathSegment(CandidateApplicationUtils.APPLICATION_DENIED)
                .toUriString();
    }

    public void apply(String accessToken, String applicationId, CandidateProfile candidateProfile) {
        CandidateApplication candidateApplication = validateTokenAndGetApplication(accessToken, applicationId);
        if (!candidateApplication.getStatus().equals(ACCEPTED)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateApplicationUtils.INVALID_STATUS_MESSAGE);
        }

        final Candidate candidate = candidateMongoRepository.findById(candidateApplication.getCandidateId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.CANDIDATE_NOT_FOUND_MESSAGE));
        if (!candidateApplication.getOwner().equals(candidate.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        candidateMongoRepository.getAllByEmailAndOwner(candidateProfile.getEmail(), candidate.getOwner())
                .stream()
                .filter(candidateByEmail -> !candidateByEmail.getId().equals(candidate.getId()))
                .findFirst()
                .ifPresent(invalidCandidate -> {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format(EMAIL_ALREADY_EXIST, invalidCandidate.getEmail()));
                });

        CandidateApplicationUtils.validateDates(candidateProfile).ifPresent(message -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format(CandidateApplicationUtils.INVALID_DATE_MESSAGE, message));
        });

        candidateMongoRepository.save(CandidateMapper.toEntity(candidate, candidateProfile));
        candidateApplication.setStatus(DONE);
        candidateApplication.setUpdated(LocalDateTime.now());
        candidateApplicationRepository.save(candidateApplication);
    }

    public CandidateProfile getProfile(String accessToken, String applicationId) {
        CandidateApplication candidateApplication = validateTokenAndGetApplication(accessToken, applicationId);
        Candidate candidate = candidateMongoRepository.findById(candidateApplication.getCandidateId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.CANDIDATE_NOT_FOUND_MESSAGE));
        if (!candidateApplication.getOwner().equals(candidate.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        return CandidateApplicationMapper.toResponse(candidate);
    }

    public VacancyDto getVacancy(String accessToken, String applicationId) {
        CandidateApplication candidateApplication = validateTokenAndGetApplication(accessToken, applicationId);
        Vacancy vacancy = vacancyMongoRepository.findById(candidateApplication.getVacancyId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.NOT_FOUND_MESSAGE));

        if (!candidateApplication.getOwner().equals(vacancy.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        return VacancyMapper.toResponse(vacancy);
    }

    private CandidateApplication getCandidateApplication(String applicationId) {
        return candidateApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.NOT_FOUND_MESSAGE));
    }


    private CandidateApplication validateTokenAndGetApplication(String accessToken, String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        if (!candidateApplication.getStatus().equals(ACCEPTED)) {
            log.error("Candidate application id {} has status {}", applicationId, candidateApplication.getStatus());
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }
        if (!candidateApplication.getAccessCode().equals(accessToken)) {
            log.error("Access token for candidate application id {} is not {}", applicationId, accessToken);
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }
        return candidateApplication;
    }
}
