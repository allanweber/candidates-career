package com.allanweber.candidatescareer.app.applications.mapper;

import com.allanweber.candidatescareer.app.applications.dto.ApplicationCandidate;
import com.allanweber.candidatescareer.app.applications.dto.ApplicationResponse;
import com.allanweber.candidatescareer.app.applications.dto.ApplicationVacancy;
import com.allanweber.candidatescareer.app.applications.repository.CandidateApplication;
import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateApplicationMapper {

    public static ApplicationResponse toResponse(CandidateApplication entity, VacancyDto vacancyDto) {
        return toResponseCommonData(entity)
                .vacancy(ApplicationVacancy.builder().id(vacancyDto.getId()).name(vacancyDto.getName()).build())
                .build();
    }

    public static ApplicationResponse toResponse(CandidateApplication entity) {
        return toResponseCommonData(entity).build();
    }

    private static ApplicationResponse.ApplicationResponseBuilder toResponseCommonData(CandidateApplication entity) {
        return ApplicationResponse.builder()
                .candidate(ApplicationCandidate.builder().id(entity.getCandidateId()).name(entity.getCandidateName()).build())
                .error(entity.getError())
                .status(entity.getStatus())
                .statusText(entity.getStatusText())
                .extraDenyReason(entity.getExtraDenyReason())
                .sent(entity.getSent())
                .updated(entity.getUpdated());
    }

    public static CandidateProfile toResponse(Candidate candidate) {
        return CandidateProfile.builder()
                .name(candidate.getName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .bio(candidate.getBio())
                .location(candidate.getLocation())
                .experiences(candidate.getExperiences())
                .lastUpdate(candidate.getLastUpdate())
                .build();
    }
}
