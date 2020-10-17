package com.allanweber.candidatescareer.app.applications.service;

import com.allanweber.candidatescareer.app.applications.dto.ApplicationStatus;
import com.allanweber.candidatescareer.app.applications.dto.DenyReason;
import com.allanweber.candidatescareer.app.applications.mapper.CandidateApplicationMapper;
import com.allanweber.candidatescareer.app.applications.repository.CandidateApplication;
import com.allanweber.candidatescareer.app.applications.repository.CandidateApplicationRepository;
import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.candidate.repository.CandidateMongoRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.app.applications.dto.ApplicationStatus.*;
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
        validateApplicationTokenAndGet(accessToken, applicationId);
    }

    public String view(String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId)
                .withStatus(VIEW).withStatusText(VIEW.getText()).withUpdated(LocalDateTime.now());
        candidateApplicationRepository.save(candidateApplication);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(CandidateApplicationUtils.APPLICATION_PATH)
                .pathSegment(CandidateApplicationUtils.APPLICATION_VIEW)
                .path(applicationId)
                .toUriString();
    }

    public void validateView(String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        if (!candidateApplication.getStatus().equals(VIEW)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateApplicationUtils.INVALID_STATUS_MESSAGE);
        }
    }

    public void accept(String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        if (!candidateApplication.getStatus().equals(VIEW)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateApplicationUtils.INVALID_STATUS_MESSAGE);
        }
        candidateApplication = candidateApplication.withStatus(ACCEPT).withStatusText(ACCEPT.getText()).withUpdated(LocalDateTime.now());
        candidateApplicationRepository.save(candidateApplication);
    }

    public void deny(String applicationId, DenyReason denyReason) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        if (candidateApplication.getStatus().equals(VIEW) || candidateApplication.getStatus().equals(ACCEPT)) {
            candidateApplication = candidateApplication
                    .withStatus(denyReason.getOption())
                    .withStatusText(denyReason.getOptionText())
                    .withExtraDenyReason(denyReason.getExtraReason())
                    .withUpdated(LocalDateTime.now());
            candidateApplicationRepository.save(candidateApplication);
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateApplicationUtils.INVALID_STATUS_MESSAGE);
        }
    }

    public void apply(String accessToken, String applicationId, CandidateProfile candidateProfile) {
        CandidateApplication candidateApplication = validateApplicationTokenAndGet(accessToken, applicationId);
        if (!candidateApplication.getStatus().equals(ACCEPT)) {
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
        candidateApplication = candidateApplication.withStatus(DONE).withStatusText(DONE.getText()).withUpdated(LocalDateTime.now());
        candidateApplicationRepository.save(candidateApplication);
    }

    public CandidateProfile getProfile(String accessToken, String applicationId) {
        CandidateApplication candidateApplication = validateApplicationTokenAndGet(accessToken, applicationId);
        if (!candidateApplication.getStatus().equals(ACCEPT)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateApplicationUtils.INVALID_STATUS_MESSAGE);
        }
        Candidate candidate = candidateMongoRepository.findById(candidateApplication.getCandidateId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.CANDIDATE_NOT_FOUND_MESSAGE));
        if (!candidateApplication.getOwner().equals(candidate.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        return CandidateApplicationMapper.toResponse(candidate);
    }

    public VacancyDto getVacancy(String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        Vacancy vacancy = vacancyMongoRepository.findById(candidateApplication.getVacancyId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.NOT_FOUND_MESSAGE));

        if (!candidateApplication.getOwner().equals(vacancy.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        return VacancyMapper.toResponseView(vacancy);
    }

    public List<DenyReason> getDenyReasons() {
        return Arrays.stream(values())
                .filter(ApplicationStatus::isChoosable)
                .map(status -> DenyReason.builder().option(status).optionText(status.getText()).build())
                .collect(Collectors.toList());
    }

    private CandidateApplication getCandidateApplication(String applicationId) {
        return candidateApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateApplicationUtils.NOT_FOUND_MESSAGE));
    }


    private CandidateApplication validateApplicationTokenAndGet(String accessToken, String applicationId) {
        CandidateApplication candidateApplication = getCandidateApplication(applicationId);
        if (!candidateApplication.getAccessCode().equals(accessToken)) {
            log.error("Access token for candidate application id {} is not {}", applicationId, accessToken);
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateApplicationUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }
        return candidateApplication;
    }
}
