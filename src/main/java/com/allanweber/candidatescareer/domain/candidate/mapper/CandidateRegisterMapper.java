package com.allanweber.candidatescareer.domain.candidate.mapper;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterVacancy;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRegister;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateRegisterMapper {

    public static CandidateRegisterResponse toResponse(CandidateRegister entity, CandidateRegisterVacancy vacancy) {
        return CandidateRegisterResponse.builder().error(entity.getError()).status(entity.getStatus()).accessCode(entity.getAccessCode()).vacancy(vacancy).build();
    }

    public static CandidateRegisterVacancy toResponse(VacancyDto vacancyDto) {
        return CandidateRegisterVacancy.builder().id(vacancyDto.getId()).name(vacancyDto.getName()).build();
    }

    public static CandidateProfile toResponse(Candidate candidate){
        return CandidateProfile.builder()
                .name(candidate.getName())
                .email(candidate.getEmail())
                .bio(candidate.getBio())
                .location(candidate.getLocation())
                .experiences(candidate.getExperiences())
                .build();
    }
}
