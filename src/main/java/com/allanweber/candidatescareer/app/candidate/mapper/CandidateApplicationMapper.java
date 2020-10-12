package com.allanweber.candidatescareer.app.candidate.mapper;

import com.allanweber.candidatescareer.app.applications.dto.VacancyApplicationResponse;
import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.applications.dto.CandidateApplicationResponse;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.applications.repository.CandidateApplication;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateApplicationMapper {

    public static CandidateApplicationResponse toResponse(CandidateApplication entity, VacancyDto vacancyDto) {
        return CandidateApplicationResponse.builder()
                .error(entity.getError())
                .status(entity.getStatus())
                .accessCode(entity.getAccessCode())
                .vacancyId(vacancyDto.getId())
                .vacancyName(vacancyDto.getName())
                .sent(entity.getSent())
                .updated(entity.getUpdated())
                .build();
    }

    public static VacancyApplicationResponse toResponse(CandidateApplication application) {
        return VacancyApplicationResponse.builder()
                .candidateId(application.getCandidateId())
                .candidateName(application.getCandidateName())
                .error(application.getError())
                .status(application.getStatus())
                .sent(application.getSent())
                .updated(application.getUpdated())
                .build();
    }

    public static CandidateProfile toResponse(Candidate candidate){
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
