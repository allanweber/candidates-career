package com.allanweber.candidates_career_recruiter.app.applications.service;

import com.allanweber.candidates_career_recruiter.app.applications.dto.ApplicationResponse;
import com.allanweber.candidates_career_recruiter.app.applications.mapper.CandidateApplicationMapper;
import com.allanweber.candidates_career_recruiter.app.applications.repository.CandidateApplication;
import com.allanweber.candidates_career_recruiter.app.applications.repository.CandidateApplicationRepository;
import com.allanweber.candidates_career_recruiter.app.candidate.dto.CandidateResponse;
import com.allanweber.candidates_career_recruiter.app.candidate.email.SendApplicationDto;
import com.allanweber.candidates_career_recruiter.app.candidate.service.CandidateService;
import com.allanweber.candidates_career_recruiter.app.shared.Skill;
import com.allanweber.candidates_career_recruiter.app.vacancy.dto.Salary;
import com.allanweber.candidates_career_recruiter.app.vacancy.dto.VacancyDto;
import com.allanweber.candidates_career_recruiter.app.vacancy.service.VacancyService;
import com.allanweber.candidates_career_recruiter.authentication.user.dto.UserDto;
import com.allanweber.candidates_career_recruiter.authentication.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.allanweber.candidates_career_recruiter.app.applications.dto.ApplicationStatus.PENDING;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecruiterApplicationService {

    private final CandidateService candidateService;
    private final UserService userService;
    private final VacancyService vacancyService;
    private final CandidateApplicationEmailService candidateApplicationEmailService;
    private final CandidateApplicationRepository candidateApplicationRepository;

    public ApplicationResponse sendApplication(String candidateId, String vacancyId) {
        CandidateResponse candidate = candidateService.getById(candidateId);
        VacancyDto vacancy = vacancyService.getById(vacancyId);

        candidateApplicationRepository.findByCandidateIdAndVacancyId(candidate.getId(), vacancy.getId())
                .ifPresent(candidateApplicationRepository::delete);

        String accessCode = UUID.randomUUID().toString().split("-")[0].toUpperCase(Locale.getDefault());
        CandidateApplication candidateApplication = candidateApplicationRepository.save(
                CandidateApplication.builder()
                        .candidateId(candidate.getId())
                        .candidateName(candidate.getName())
                        .vacancyId(vacancy.getId())
                        .owner(candidate.getOwner())
                        .accessCode(accessCode)
                        .status(PENDING)
                        .statusText(PENDING.getText())
                        .sent(LocalDateTime.now())
                        .build()
        );

        UserDto userDto = userService.getByUserName(candidate.getOwner());
        SendApplicationDto.SendApplicationDtoBuilder sendApplicationDtoBuilder = SendApplicationDto.builder().candidateEmail(candidate.getEmail()).candidateName(candidate.getName())
                .candidateApplicationId(candidateApplication.getId()).recruiterName(userDto.getFullName())
                .vacancyName(vacancy.getName())
                .accessCode(accessCode)
                .vacancySkills(vacancy.getSkills().stream().limit(5).map(Skill::getName).collect(Collectors.toList()));

        if(Optional.ofNullable(vacancy.getSalary()).map(Salary::getVisible).orElse(false)){
            sendApplicationDtoBuilder.salary(vacancy.getSalary());
        }

        candidateApplicationEmailService.sendEmail(sendApplicationDtoBuilder.build());

        return CandidateApplicationMapper.toResponse(candidateApplication, vacancy);
    }

    public List<ApplicationResponse> getCandidateApplications(String candidateId) {
        CandidateResponse candidate = candidateService.getById(candidateId);
        return candidateApplicationRepository.findByCandidateId(candidate.getId())
                .stream()
                .map(entity -> {
                    VacancyDto vacancy = vacancyService.getById(entity.getVacancyId());
                    return CandidateApplicationMapper.toResponse(entity, vacancy);
                })
                .sorted(Comparator.comparing(application -> application.getVacancy().getName()))
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getVacancyApplications(String vacancyId) {
        List<CandidateApplication> applications = candidateApplicationRepository.findByVacancyId(vacancyId);
        return applications.stream().map(CandidateApplicationMapper::toResponse)
                .sorted(Comparator.comparing(application -> application.getCandidate().getName()))
                .collect(Collectors.toList());
    }
}
