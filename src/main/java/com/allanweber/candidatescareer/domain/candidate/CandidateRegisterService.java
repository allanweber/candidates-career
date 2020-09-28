package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterStatus;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.email.CandidateRegisterEmailService;
import com.allanweber.candidatescareer.domain.candidate.email.SendRegisterDto;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateRegisterMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRegister;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRegisterRepository;
import com.allanweber.candidatescareer.domain.user.UserService;
import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.vacancy.VacancyService;
import com.allanweber.candidatescareer.domain.vacancy.dto.Skill;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CandidateRegisterService {

    private static final String NOT_FOUND_MESSAGE = "Solicitação de registro não encontrada";
    private static final String INVALID_STATUS_MESSAGE = "Solicitação de registro não é inválida e já foi concluída.";
    private static final String REGISTER_PATH = "candidate-register";
    private static final String REGISTER_DENIED = "denied";

    private final CandidateService candidateService;
    private final UserService userService;
    private final VacancyService vacancyService;
    private final CandidateRegisterRepository candidateRegisterRepository;
    private final CandidateRegisterEmailService candidateRegisterEmailService;
    private final AppHostConfiguration appHostConfiguration;

    public CandidateRegisterResponse sendRegister(String candidateId, String vacancyId) {
        CandidateResponse candidate = candidateService.getById(candidateId);
        VacancyDto vacancy = vacancyService.getById(vacancyId);

        candidateRegisterRepository.findByCandidateIdAndVacancyId(candidate.getId(), vacancy.getId())
                .ifPresent(candidateRegisterRepository::delete);
        CandidateRegister candidateRegister = candidateRegisterRepository.save(
                CandidateRegister.builder()
                        .candidateId(candidate.getId())
                        .vacancyId(vacancy.getId())
                        .owner(candidate.getOwner())
                        .status(PENDING)
                        .build()
        );

        UserDto userDto = userService.getByUserName(candidate.getOwner());
        SendRegisterDto sendRegisterDto = SendRegisterDto.builder().candidateEmail(candidate.getEmail()).candidateName(candidate.getName())
                .candidateRegisterId(candidateRegister.getId()).recruiterName(userDto.getFullName())
                .vacancyName(vacancy.getName())
                .vacancySkills(vacancy.getSkills().stream().limit(5).map(Skill::getName).collect(Collectors.toList()))
                .build();

        candidateRegisterEmailService.sendEmail(sendRegisterDto);

        return CandidateRegisterMapper.toResponse(candidateRegister);
    }

    public String accept(String registerId) {
        CandidateRegister candidateRegister = getCandidateRegister(registerId, PENDING);
        candidateRegister.setStatus(ACCEPTED);
        candidateRegisterRepository.save(candidateRegister);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(REGISTER_PATH)
                .pathSegment(REGISTER_DENIED)
                .toUriString();
    }

    public String deny(String registerId) {
        CandidateRegister candidateRegister = getCandidateRegister(registerId, PENDING);
        candidateRegister.setStatus(DENIED);
        candidateRegisterRepository.save(candidateRegister);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(REGISTER_PATH)
                .toUriString();
    }

    public Object register(String registerId) {
        CandidateRegister candidateRegister = getCandidateRegister(registerId, ACCEPTED);
        candidateRegister.setStatus(DONE);
        candidateRegisterRepository.save(candidateRegister);
        return candidateRegister.getCandidateId();
    }

    private CandidateRegister getCandidateRegister(String registerId, CandidateRegisterStatus expectedStatus) {
        CandidateRegister candidateRegister = candidateRegisterRepository.findById(registerId)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));
        if (!candidateRegister.getStatus().equals(expectedStatus)) {
            throw new HttpClientErrorException(BAD_REQUEST, INVALID_STATUS_MESSAGE);
        }
        return candidateRegister;
    }
}
