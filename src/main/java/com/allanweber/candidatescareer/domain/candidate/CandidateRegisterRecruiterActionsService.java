package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterStatus.PENDING;

@RequiredArgsConstructor
@Service
@Slf4j
public class CandidateRegisterRecruiterActionsService {

    private final CandidateService candidateService;
    private final UserService userService;
    private final VacancyService vacancyService;
    private final CandidateRegisterEmailService candidateRegisterEmailService;
    private final CandidateRegisterRepository candidateRegisterRepository;

    public CandidateRegisterResponse sendRegister(String candidateId, String vacancyId) {
        CandidateResponse candidate = candidateService.getById(candidateId);
        VacancyDto vacancy = vacancyService.getById(vacancyId);

        candidateRegisterRepository.findByCandidateIdAndVacancyId(candidate.getId(), vacancy.getId())
                .ifPresent(candidateRegisterRepository::delete);

        String accessCode = UUID.randomUUID().toString().split("-")[0].toUpperCase(Locale.getDefault());
        CandidateRegister candidateRegister = candidateRegisterRepository.save(
                CandidateRegister.builder()
                        .candidateId(candidate.getId())
                        .vacancyId(vacancy.getId())
                        .owner(candidate.getOwner())
                        .accessCode(accessCode)
                        .status(PENDING)
                        .build()
        );

        UserDto userDto = userService.getByUserName(candidate.getOwner());
        SendRegisterDto sendRegisterDto = SendRegisterDto.builder().candidateEmail(candidate.getEmail()).candidateName(candidate.getName())
                .candidateRegisterId(candidateRegister.getId()).recruiterName(userDto.getFullName())
                .vacancyName(vacancy.getName())
                .accessCode(accessCode)
                .vacancySkills(vacancy.getSkills().stream().limit(5).map(Skill::getName).collect(Collectors.toList()))
                .build();

        candidateRegisterEmailService.sendEmail(sendRegisterDto);

        return CandidateRegisterMapper.toResponse(candidateRegister, CandidateRegisterMapper.toResponse(vacancy));
    }

    public List<CandidateRegisterResponse> getRegisters(String candidateId) {
        CandidateResponse candidate = candidateService.getById(candidateId);
        return candidateRegisterRepository.findByCandidateId(candidate.getId())
                .stream()
                .map(entity -> {
                    VacancyDto vacancy = vacancyService.getById(entity.getVacancyId());
                    return CandidateRegisterMapper.toResponse(entity, CandidateRegisterMapper.toResponse(vacancy));
                })
                .sorted(Comparator.comparing(dto -> dto.getVacancy().getName()))
                .collect(Collectors.toList());
    }
}
