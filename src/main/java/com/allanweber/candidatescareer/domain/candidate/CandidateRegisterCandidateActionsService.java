package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterProfile;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateRegisterMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRegister;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRegisterRepository;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.mapper.VacancyMapper;
import com.allanweber.candidatescareer.domain.vacancy.repository.Vacancy;
import com.allanweber.candidatescareer.domain.vacancy.repository.VacancyMongoRepository;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class CandidateRegisterCandidateActionsService {

    private final CandidateRegisterRepository candidateRegisterRepository;
    private final AppHostConfiguration appHostConfiguration;
    private final CandidateMongoRepository candidateMongoRepository;
    private final VacancyMongoRepository vacancyMongoRepository;

    public void validateToken(String accessToken, String registerId) {
        validateTokenAndGetRegister(accessToken, registerId);
    }

    public String accept(String registerId) {
        CandidateRegister candidateRegister = getCandidateRegister(registerId);
        candidateRegister.setStatus(ACCEPTED);
        candidateRegisterRepository.save(candidateRegister);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(CandidateRegisterUtils.REGISTER_PATH)
                .pathSegment(CandidateRegisterUtils.REGISTER_ACCEPT)
                .path(registerId)
                .toUriString();
    }

    public String deny(String registerId) {
        CandidateRegister candidateRegister = getCandidateRegister(registerId);
        if (!candidateRegister.getStatus().equals(PENDING)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateRegisterUtils.INVALID_STATUS_MESSAGE);
        }
        candidateRegister.setStatus(DENIED);
        candidateRegisterRepository.save(candidateRegister);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(CandidateRegisterUtils.REGISTER_PATH)
                .pathSegment(CandidateRegisterUtils.REGISTER_DENIED)
                .toUriString();
    }

    public void register(String accessToken, String registerId, CandidateRegisterProfile registerProfile) {
        CandidateRegister candidateRegister = validateTokenAndGetRegister(accessToken, registerId);
        if (!candidateRegister.getStatus().equals(ACCEPTED)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, CandidateRegisterUtils.INVALID_STATUS_MESSAGE);
        }

        Candidate candidate = candidateMongoRepository.findById(candidateRegister.getCandidateId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateRegisterUtils.CANDIDATE_NOT_FOUND_MESSAGE));
        if (!candidateRegister.getOwner().equals(candidate.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateRegisterUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        CandidateRegisterUtils.validateDates(registerProfile).ifPresent(message -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, String.format(CandidateRegisterUtils.INVALID_DATE_MESSAGE, message));
        });

        candidate = CandidateMapper.toEntity(candidate, registerProfile);
        candidateMongoRepository.save(candidate);
        candidateRegister.setStatus(DONE);
        candidateRegisterRepository.save(candidateRegister);
    }

    public CandidateRegisterProfile getProfile(String accessToken, String registerId) {
        CandidateRegister candidateRegister = validateTokenAndGetRegister(accessToken, registerId);
        Candidate candidate = candidateMongoRepository.findById(candidateRegister.getCandidateId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateRegisterUtils.CANDIDATE_NOT_FOUND_MESSAGE));
        if (!candidateRegister.getOwner().equals(candidate.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateRegisterUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        return CandidateRegisterMapper.toResponse(candidate);
    }

    public VacancyDto getVacancy(String accessToken, String registerId) {
        CandidateRegister candidateRegister = validateTokenAndGetRegister(accessToken, registerId);
        Vacancy vacancy = vacancyMongoRepository.findById(candidateRegister.getVacancyId())
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateRegisterUtils.NOT_FOUND_MESSAGE));

        if (!candidateRegister.getOwner().equals(vacancy.getOwner())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateRegisterUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }

        return VacancyMapper.toResponse(vacancy);
    }

    private CandidateRegister getCandidateRegister(String registerId) {
        return candidateRegisterRepository.findById(registerId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, CandidateRegisterUtils.NOT_FOUND_MESSAGE));
    }


    private CandidateRegister validateTokenAndGetRegister(String accessToken, String registerId) {
        CandidateRegister candidateRegister = getCandidateRegister(registerId);
        if (!candidateRegister.getStatus().equals(ACCEPTED)) {
            log.error("Candidate register id {} has status {}", registerId, candidateRegister.getStatus());
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateRegisterUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }
        if (!candidateRegister.getAccessCode().equals(accessToken)) {
            log.error("Access token for candidate register id {} is not {}", registerId, accessToken);
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, CandidateRegisterUtils.UNAUTHORIZED_STATUS_MESSAGE);
        }
        return candidateRegister;
    }
}
